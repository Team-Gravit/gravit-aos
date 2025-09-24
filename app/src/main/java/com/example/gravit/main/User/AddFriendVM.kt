package com.example.gravit.main.User

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.FriendUser
import kotlinx.coroutines.Job
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
        data object Loading : UiState
        data class Success(
            val query: String,
            val results: List<FriendUser>,
            val page: Int,
            val hasNext: Boolean,
            val isLoadingNext: Boolean = false,
            val itemLoading: Set<Long> = emptySet(),
            val lastError: String? = null,
            val showUndo: Set<Long> = emptySet()
        ) : UiState
        data class Failed(val message: String? = null) : UiState
        data object SessionExpired : UiState
    }

    private val _state = MutableStateFlow<UiState>(
        UiState.Success(
                query = "",
                results = emptyList(),
                page = 0,
                hasNext = false,
                isLoadingNext = false,
                itemLoading = emptySet(),
                lastError = null,
                showUndo = emptySet()
        )
    )
    val state = _state.asStateFlow()

    private var pagingJob: Job? = null


    //검색
    fun search(rawQuery: String) = viewModelScope.launch {
        pagingJob?.cancel()
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        val nq = normalizeHandle(rawQuery)

        if (nq.isBlank()) {
            _state.value = UiState.Success(
                query = "",
                results = emptyList(),
                page = 0,
                hasNext = false,
                isLoadingNext = false,
                itemLoading = emptySet(),
                lastError = null,
                showUndo = emptySet()
            )
            return@launch
        }
        val requestedPage = 0
        runCatching {
            api.getFriends(auth = auth, queryText = nq, page = requestedPage)
        }.onSuccess { res ->
            _state.value = UiState.Success(
                query = nq,
                results = res.searchUsers,
                page = requestedPage + 1,
                hasNext = res.hasNext,
                isLoadingNext = false,
                itemLoading = emptySet(),
                lastError = null,
                showUndo = emptySet()
            )
        }.onFailure { e ->
            if ((e as? HttpException)?.code() == 401) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
            } else {
                _state.value = UiState.Failed(e.message)
            }
        }
    }

    //다음 페이지
    fun loadNext() {
        val cur = _state.value as? UiState.Success ?: return
        if (cur.isLoadingNext || !cur.hasNext) return

        pagingJob?.cancel()
        pagingJob = viewModelScope.launch {

            val session = AuthPrefs.load(appContext)
            if (session == null || AuthPrefs.isExpired(session)) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
                return@launch
            }
            val auth = "Bearer ${session.accessToken}"

            val requestedPage = (state.value as? UiState.Success)?.page ?: cur.page
            val latestBefore = _state.value as? UiState.Success ?: return@launch
            _state.value = latestBefore.copy(isLoadingNext = true, lastError = null)

            runCatching {
                api.getFriends(auth = auth, queryText = cur.query, page = requestedPage)
            }.onSuccess { res ->
                val latest = _state.value as? UiState.Success ?: return@onSuccess
                _state.value = latest.copy(
                    results = (latest.results + res.searchUsers).distinctBy { it.userId },
                    page = requestedPage + 1,
                    hasNext = res.hasNext,
                    isLoadingNext = false,
                    lastError = null
                )
            }.onFailure { e ->
                if ((e as? HttpException)?.code() == 401) {
                    AuthPrefs.clear(appContext)
                    _state.value = UiState.SessionExpired
                } else {
                    val latest = _state.value as? UiState.Success ?: return@onFailure
                    _state.value = latest.copy(isLoadingNext = false, lastError = e.message)
                }
            }
        }
    }

    //팔로우/언팔로우
    fun toggleFollow(userId: Long) = viewModelScope.launch {
        val cur = _state.value as? UiState.Success ?: return@launch
        val target = cur.results.find { it.userId == userId } ?: return@launch
        val willFollow = !target.isFollowing

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }
        val auth = "Bearer ${session.accessToken}"

        _state.value = cur.copy(
            results = cur.results.map { u ->
                if (u.userId == userId) u.copy(isFollowing = willFollow) else u },
            itemLoading = cur.itemLoading + userId,
            lastError = null
        )

        runCatching {
            if (willFollow) api.sendFolloweeId(auth, userId)
            else api.sendUnFolloweeId(auth, userId)

        }.onSuccess {
            val s = _state.value as? UiState.Success ?: return@onSuccess
            _state.value = s.copy(
                itemLoading = s.itemLoading - userId,
                showUndo = if (willFollow) s.showUndo + userId else s.showUndo - userId
            )
        }.onFailure { e ->
            val s = _state.value as? UiState.Success ?: return@onFailure
            _state.value = s.copy(
                results = s.results.map { u -> if (u.userId == userId) u.copy(isFollowing = !willFollow) else u },
                itemLoading = s.itemLoading - userId,
                lastError = e.message,
                showUndo = s.showUndo - userId
            )
        }
    }

    //핸들 정규화
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