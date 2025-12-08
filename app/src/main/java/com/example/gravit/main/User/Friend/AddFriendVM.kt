package com.example.gravit.main.User.Friend

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.FriendItem
import com.example.gravit.api.FriendSearchResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.Normalizer

class AddFriendVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    data class UiState(
        val query: String = "",
        val loading: Boolean = false,
        val items: List<FriendItem> = emptyList(),
        val hasNext: Boolean = false,
        val page: Int = 0,
        val error: String? = null,
        val sessionExpired: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
        return runCatching { block() }
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            searchFirst()
        }
    }

    fun searchFirst() {
        val raw = state.value.query.trim()
        if (raw.isEmpty()) {
            _state.update {
                it.copy(
                    items = emptyList(),
                    hasNext = false,
                    page = 0,
                    error = null,
                    loading = false
                )
            }
            return
        }

        val normalized = normalizeQuery(raw)
        if (normalized.isEmpty()) {
            _state.update {
                it.copy(
                    items = emptyList(),
                    hasNext = false,
                    page = 0,
                    error = null,
                    loading = false
                )
            }
            return
        }

        viewModelScope.launch {
            val token = AuthPrefs.load(appContext)?.accessToken
            if (token.isNullOrBlank()) {
                _state.update { it.copy(sessionExpired = true) }
                return@launch
            }

            _state.update { it.copy(loading = true, error = null, page = 0) }

            val result: Result<Response<FriendSearchResponse>> = safeCall {
                api.getFriends(
                    auth = "Bearer $token",
                    queryText = normalized,
                    page = 0
                )
            }

            result.fold(
                onSuccess = { res ->
                    handleSearchResponse(res, reset = true, page = 0)
                },
                onFailure = {
                    _state.update {
                        it.copy(
                            loading = false,
                            error = "네트워크 오류가 발생했어요."
                        )
                    }
                }
            )
        }
    }

    fun loadNext() {
        val cur = state.value
        if (cur.loading || !cur.hasNext) return

        val raw = cur.query.trim()
        if (raw.isEmpty()) return

        val normalized = normalizeQuery(raw)
        if (normalized.isEmpty()) return

        viewModelScope.launch {
            val token = AuthPrefs.load(appContext)?.accessToken
            if (token.isNullOrBlank()) {
                _state.update { it.copy(sessionExpired = true) }
                return@launch
            }

            val nextPage = cur.page + 1

            _state.update { it.copy(loading = true, error = null) }

            val result: Result<Response<FriendSearchResponse>> = safeCall {
                api.getFriends(
                    auth = "Bearer $token",
                    queryText = normalized,
                    page = nextPage
                )
            }

            result.fold(
                onSuccess = { res ->
                    handleSearchResponse(res, reset = false, page = nextPage)
                },
                onFailure = {
                    _state.update {
                        it.copy(
                            loading = false,
                            error = "네트워크 오류가 발생했어요."
                        )
                    }
                }
            )
        }
    }

    private fun handleSearchResponse(
        res: Response<FriendSearchResponse>,
        reset: Boolean,
        page: Int
    ) {
        if (res.code() == 401) {
            _state.update {
                it.copy(
                    loading = false,
                    sessionExpired = true
                )
            }
            return
        }

        if (!res.isSuccessful) {
            _state.update {
                it.copy(
                    loading = false,
                    error = "검색에 실패했어요. (${res.code()})"
                )
            }
            return
        }

        val body = res.body()
        if (body == null) {
            _state.update {
                it.copy(
                    loading = false,
                    error = "응답이 비어 있어요."
                )
            }
            return
        }

        _state.update { prev ->
            prev.copy(
                loading = false,
                items = if (reset) body.contents else prev.items + body.contents,
                hasNext = body.hasNextPage,
                page = page,
                error = null
            )
        }
    }

    fun toggleFollow(targetUserId: Long, currentlyFollowing: Boolean) {
        viewModelScope.launch {
            val token = AuthPrefs.load(appContext)?.accessToken
            if (token.isNullOrBlank()) {
                _state.update { it.copy(sessionExpired = true) }
                return@launch
            }

            val auth = "Bearer $token"
            _state.update { it.copy(error = null) }

            // follow / unfollow 공통 처리
            val result: Result<Response<*>> = safeCall {
                if (currentlyFollowing) {
                    api.unfollow(auth = auth, followeeId = targetUserId)
                } else {
                    api.follow(auth = auth, followeeId = targetUserId)
                } as Response<*>
            }

            result.fold(
                onSuccess = { res ->
                    if (res.code() == 401) {
                        _state.update { it.copy(sessionExpired = true) }
                        return@fold
                    }

                    if (!res.isSuccessful) {
                        val msg = when (res.code()) {
                            400 -> "자기 자신은 팔로우할 수 없어요."
                            404 -> "팔로우 내역이 존재하지 않아요."
                            409 -> "이미 팔로우한 유저예요."
                            else -> "팔로우 요청에 실패했어요. (${res.code()})"
                        }
                        _state.update { it.copy(error = msg) }
                        return@fold
                    }

                    // 성공 시 로컬 상태 토글
                    _state.update { prev ->
                        prev.copy(
                            items = prev.items.map { item ->
                                if (item.userId == targetUserId) {
                                    item.copy(isFollowing = !currentlyFollowing)
                                } else item
                            }
                        )
                    }
                },
                onFailure = {
                    _state.update {
                        it.copy(error = "팔로우 요청 중 네트워크 오류가 발생했어요.")
                    }
                }
            )
        }
    }

    private fun normalizeQuery(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        val nfkc = Normalizer.normalize(trimmed, Normalizer.Form.NFKC)
        return nfkc
    }
}

class AddFriendVMFactory(
    private val api: ApiService,
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFriendVM(api, appContext) as T
    }
}
