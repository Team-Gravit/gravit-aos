package com.example.gravit.main.User.Friend

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.FriendUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.Normalizer
import java.util.Locale

class AddFriendVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState {
        object Loading : UiState
        data class Success(
            val results: List<FriendUser>,
            val itemLoading: Set<Long> = emptySet(),
            val showUndo: Set<Long> = emptySet()
        ) : UiState
        data class Failed(val message: String? = null) : UiState
        object SessionExpired : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Success(emptyList()))
    val state = _state.asStateFlow()

    fun search(rawQuery: String) = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }
        val auth = "Bearer ${session.accessToken}"

        val nq = normalizeQuery(rawQuery)

        runCatching {
            api.getFriends(auth = auth, queryText = nq, page = 0)
        }.onSuccess { res ->
            _state.value = UiState.Success(res.contents)
        }.onFailure { e ->
            if ((e as? HttpException)?.code() == 401) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
            } else {
                _state.value = UiState.Failed(e.message)
            }
        }
    }

    fun toggleFollow(userId: Long) = viewModelScope.launch {
        val cur = _state.value as? UiState.Success ?: return@launch
        val willFollow = !(cur.results.find { it.userId == userId }?.isFollowing ?: false)

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }
        val auth = "Bearer ${session.accessToken}"

        runCatching {
            if (willFollow) api.sendFolloweeId(auth, userId)
            else api.sendUnFolloweeId(auth, userId)
        }.onSuccess {
            val updated = cur.results.map {
                if (it.userId == userId) it.copy(isFollowing = willFollow) else it
            }
            _state.value = cur.copy(results = updated)
        }
    }

    private fun normalizeQuery(raw: String): String {
        var q = raw.trim()
        q = Normalizer.normalize(q, Normalizer.Form.NFKC)

        if (q.any { it.isLetter() && it.code > 128 }) {
            return q
        }

        if (!q.startsWith("@")) {
            q = "@$q"
        }
        return q.lowercase(Locale.ROOT)
    }
}

@Suppress("UNCHECKED_CAST")
class AddFriendVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFriendVM(api, context.applicationContext) as T
    }
}
