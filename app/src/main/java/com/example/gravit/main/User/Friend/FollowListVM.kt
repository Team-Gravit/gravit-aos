package com.inuappcenter.gravit.main.User.Friend

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.FollowerSliceResponse
import com.inuappcenter.gravit.api.FollowingSliceResponse
import com.inuappcenter.gravit.api.FriendCountResponse
import com.inuappcenter.gravit.api.FriendFollowerItem
import com.inuappcenter.gravit.api.FriendUFollowingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.collections.filterNot

enum class FriendTab {
    Follower, Following
}

class FriendListVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    data class UiState(
        val selectedTab: FriendTab = FriendTab.Follower,
        val followerItems: List<FriendFollowerItem> = emptyList(),
        val followingItems: List<FriendUFollowingItem> = emptyList(),
        val followerHasNext: Boolean = false,
        val followingHasNext: Boolean = false,
        val followerPage: Int = 0,
        val followingPage: Int = 0,
        val followerCount: Int = 0,
        val followingCount: Int = 0,
        val loading: Boolean = false,
        val error: String? = null,
        val sessionExpired: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
        return runCatching { block() }
    }

    fun init() {
        viewModelScope.launch {
            loadCount()
            loadFollower(reset = true)
        }
    }

    private suspend fun getAuth(): String? {
        val token = AuthPrefs.load(appContext)?.accessToken
        if (token.isNullOrBlank()) {
            _state.update { it.copy(sessionExpired = true) }
            return null
        }
        return "Bearer $token"
    }

    fun setTab(tab: FriendTab) {
        _state.update { it.copy(selectedTab = tab, error = null) }

        when (tab) {
            FriendTab.Follower -> {
                if (state.value.followerItems.isEmpty()) {
                    loadFollower(reset = true)
                }
            }

            FriendTab.Following -> {
                if (state.value.followingItems.isEmpty()) {
                    loadFollowing(reset = true)
                }
            }
        }
    }

    private suspend fun loadCount() {
        val auth = getAuth() ?: return

        val result: Result<Response<FriendCountResponse>> = safeCall {
            api.getFriendCount(auth)
        }

        result.fold(
            onSuccess = { res ->
                if (!res.isSuccessful) return@fold
                val body = res.body() ?: return@fold

                _state.update {
                    it.copy(
                        followerCount = body.followerCount,
                        followingCount = body.followingCount
                    )
                }
            },
            onFailure = {
            }
        )
    }

    private fun loadFollower(reset: Boolean) {
        viewModelScope.launch {
            val auth = getAuth() ?: return@launch

            val targetPage = if (reset) 0 else state.value.followerPage + 1

            _state.update { it.copy(loading = true, error = null) }

            val result: Result<Response<FollowerSliceResponse>> = safeCall {
                api.getFollowerList(auth = auth, page = targetPage)
            }

            result.fold(
                onSuccess = { res ->
                    handleFollowerResponse(res, reset, targetPage)
                },
                onFailure = {
                    _state.update {
                        it.copy(
                            loading = false,
                            error = "팔로워 목록을 불러오지 못했어요."
                        )
                    }
                }
            )
        }
    }

    fun loadFollowerNext() {
        val cur = state.value
        if (cur.loading || !cur.followerHasNext) return
        loadFollower(reset = false)
    }

    private fun handleFollowerResponse(
        res: Response<FollowerSliceResponse>,
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

        if (!res.isSuccessful || res.body() == null) {
            _state.update {
                it.copy(
                    loading = false,
                    error = "팔로워 목록을 불러오지 못했어요. (${res.code()})"
                )
            }
            return
        }
        val body = res.body()!!
        _state.update { prev ->
            prev.copy(
                loading = false,
                followerItems = if (reset) body.contents else prev.followerItems + body.contents,
                followerHasNext = body.hasNextPage,
                followerPage = page
            )
        }
    }

    private fun loadFollowing(reset: Boolean) {
        viewModelScope.launch {
            val auth = getAuth() ?: return@launch

            val targetPage = if (reset) 0 else state.value.followingPage + 1

            _state.update { it.copy(loading = true, error = null) }

            val result: Result<Response<FollowingSliceResponse>> = safeCall {
                api.getFollowingList(auth = auth, page = targetPage)
            }

            result.fold(
                onSuccess = { res ->
                    handleFollowingResponse(res, reset, targetPage)
                },
                onFailure = {
                    _state.update {
                        it.copy(
                            loading = false,
                            error = "팔로잉 목록을 불러오지 못했어요."
                        )
                    }
                }
            )
        }
    }

    fun loadFollowingNext() {
        val cur = state.value
        if (cur.loading || !cur.followingHasNext) return
        loadFollowing(reset = false)
    }

    private fun handleFollowingResponse(
        res: Response<FollowingSliceResponse>,
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

        if (!res.isSuccessful || res.body() == null) {
            _state.update {
                it.copy(
                    loading = false,
                    error = "팔로잉 목록을 불러오지 못했어요. (${res.code()})"
                )
            }
            return
        }

        val body = res.body()!!
        _state.update { prev ->
            prev.copy(
                loading = false,
                followingItems = if (reset) body.contents else prev.followingItems + body.contents,
                followingHasNext = body.hasNextPage,
                followingPage = page
            )
        }
    }

    /* fun rejectFollower(followerId: Long) {
        viewModelScope.launch {
            val auth = getAuth() ?: return@launch

            val result: Result<Response<Unit>> = safeCall {
                api.rejectFollowing(auth = auth, followerId = followerId)
            }

            result.fold(
                onSuccess = { res ->
                    if (res.code() == 401) {
                        _state.update { it.copy(sessionExpired = true) }
                        return@fold
                    }

                    if (!res.isSuccessful) {
                        _state.update {
                            it.copy(error = "팔로워를 거절하지 못했어요. (${res.code()})")
                        }
                        return@fold
                    }

                    _state.update { prev ->
                        prev.copy(
                            followerItems = prev.followerItems.filterNot { it.id == followerId },
                            followerCount = (prev.followerCount - 1).coerceAtLeast(0)
                        )
                    }
                },
                onFailure = {
                    _state.update {
                        it.copy(error = "팔로워를 거절하지 못했어요.")
                   }
                }
            )
        }
    } */

    fun unfollowFromFollowing(followeeId: Long) {
        viewModelScope.launch {
            val auth = getAuth() ?: return@launch

            val result: Result<Response<Unit>> = safeCall {
                api.unfollow(auth = auth, followeeId = followeeId)
            }

            result.fold(
                onSuccess = { res ->
                    if (res.code() == 401) {
                        _state.update { it.copy(sessionExpired = true) }
                        return@fold
                    }

                    if (!res.isSuccessful) {
                        _state.update {
                            it.copy(error = "팔로우를 취소하지 못했어요. (${res.code()})")
                        }
                        return@fold
                    }

                    _state.update { prev ->
                        prev.copy(
                            followingItems = prev.followingItems.filterNot { it.id == followeeId },
                            followingCount = (prev.followingCount - 1).coerceAtLeast(0)
                        )
                    }
                },
                onFailure = {
                    _state.update {
                        it.copy(error = "팔로우를 취소하지 못했어요.")
                    }
                }
            )
        }
    }
    fun unfollowFromFollower(followeeId: Long) {
        viewModelScope.launch {
            val auth = getAuth() ?: return@launch

            val result: Result<Response<Unit>> = safeCall {
                api.unfollow(auth = auth, followeeId = followeeId)
            }

            result.fold(
                onSuccess = { res ->
                    if (res.code() == 401) {
                        _state.update { it.copy(sessionExpired = true) }
                        return@fold
                    }

                    if (!res.isSuccessful) {
                        _state.update {
                            it.copy(error = "팔로우를 취소하지 못했어요. (${res.code()})")
                        }
                        return@fold
                    }

                    _state.update { prev ->
                        prev.copy(
                            followerItems = prev.followerItems.map {
                                if (it.id == followeeId) {
                                    it.copy(isFollowing = false)
                                } else {
                                    it
                                }
                            },
                            followingCount = (prev.followingCount - 1).coerceAtLeast(0)
                        )
                    }
                },
                onFailure = {
                    _state.update {
                        it.copy(error = "팔로우를 취소하지 못했어요.")
                    }
                }
            )
        }
    }
    fun followFromFollower(userId: Long) = viewModelScope.launch {
        runCatching {
            val session = AuthPrefs.load(appContext) ?: return@launch
            api.follow(
                auth = "Bearer ${session.accessToken}",
                followeeId = userId
            )
        }.onSuccess { res ->
            if (res.isSuccessful) {
                _state.value = _state.value.copy(
                    followerItems = _state.value.followerItems.map {
                        if (it.id == userId) it.copy(isFollowing = true)
                        else it
                    },
                    followingCount = _state.value.followingCount + 1
                )
            }
        }
    }
}

class FriendListVMFactory(
    private val api: ApiService,
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FriendListVM(api, appContext) as T
    }
}