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
        val q = rawQuery.trim()
        if (q.isEmpty()) {
            _state.value = UiState.Success(results = emptyList())
            return@launch
        }

        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }
        val auth = "Bearer ${session.accessToken}"

        val queryText = if (q.startsWith("@")) q.drop(1) else q

        runCatching {
            api.getFriends(auth = auth, queryText = queryText, page = 0)
        }.onSuccess { res ->
            _state.value = UiState.Success(results = res.contents)
        }.onFailure { e ->
            val code = (e as? retrofit2.HttpException)?.code()
            _state.value = when (code) {
                401 -> {
                    AuthPrefs.clear(appContext)
                    UiState.SessionExpired
                }
                else -> UiState.Failed(e.message ?: "검색 실패")
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
            val updated = cur.results.map { if (it.userId == userId) it.copy(isFollowing = willFollow) else it }
            _state.value = cur.copy(results = updated)
        }
    }

    private fun normalizeHandle(raw: String): String {
        var q = raw.trim()
        if (q.startsWith("@")) q = q.drop(1)
        q = Normalizer.normalize(q, Normalizer.Form.NFKC)
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
