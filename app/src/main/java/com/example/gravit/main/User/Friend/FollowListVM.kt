package com.example.gravit.main.User.Friend

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.FriendSliceResponse
import com.example.gravit.api.FriendUserSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response

enum class FriendTab {
    Follower, Following
}

class FriendListVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    data class UiState(
        val selectedTab: FriendTab = FriendTab.Follower,
        val followerItems: List<FriendUserSummary> = emptyList(),
        val followingItems: List<FriendUserSummary> = emptyList(),
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

    // --- 카운트 ------------------------------------------------------------

    private suspend fun loadCount() {
        val auth = getAuth() ?: return

        val res: Response<com.example.gravit.api.FriendCountResponse>
        try {
            res = api.getFriendCount(auth)
        } catch (e: Exception) {
            return
        }

        if (!res.isSuccessful) return
        val body = res.body() ?: return

        _state.update {
            it.copy(
                followerCount = body.followerCount,
                followingCount = body.followingCount
            )
        }
    }

    private fun loadFollower(reset: Boolean) {
        viewModelScope.launch {
            val auth = getAuth() ?: return@launch

            val targetPage = if (reset) 0 else state.value.followerPage + 1

            _state.update { it.copy(loading = true, error = null) }

            val res: Response<FriendSliceResponse>
            try {
                res = api.getFollowerList(auth = auth, page = targetPage)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        loading = false,
                        error = "팔로워 목록을 불러오지 못했어요."
                    )
                }
                return@launch
            }

            handleFollowerResponse(res, reset, targetPage)
        }
    }

    fun loadFollowerNext() {
        val cur = state.value
        if (cur.loading || !cur.followerHasNext) return
        loadFollower(reset = false)
    }

    private fun handleFollowerResponse(
        res: Response<FriendSliceResponse>,
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

            val res: Response<FriendSliceResponse>
            try {
                res = api.getFollowingList(auth = auth, page = targetPage)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        loading = false,
                        error = "팔로잉 목록을 불러오지 못했어요."
                    )
                }
                return@launch
            }

            handleFollowingResponse(res, reset, targetPage)
        }
    }

    fun loadFollowingNext() {
        val cur = state.value
        if (cur.loading || !cur.followingHasNext) return
        loadFollowing(reset = false)
    }

    private fun handleFollowingResponse(
        res: Response<FriendSliceResponse>,
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

    fun rejectFollower(followerId: Long) {
        viewModelScope.launch {
            val auth = getAuth() ?: return@launch

            val res: Response<Unit>
            try {
                res = api.rejectFollowing(auth = auth, followerId = followerId)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "팔로워를 거절하지 못했어요.")
                }
                return@launch
            }

            if (res.code() == 401) {
                _state.update { it.copy(sessionExpired = true) }
                return@launch
            }

            if (!res.isSuccessful) {
                _state.update {
                    it.copy(error = "팔로워를 거절하지 못했어요. (${res.code()})")
                }
                return@launch
            }

            _state.update { prev ->
                prev.copy(
                    followerItems = prev.followerItems.filterNot { it.id == followerId },
                    followerCount = (prev.followerCount - 1).coerceAtLeast(0)
                )
            }
        }
    }

    fun unfollowFromFollowing(followeeId: Long) {
        viewModelScope.launch {
            val auth = getAuth() ?: return@launch

            val res: Response<Unit>
            try {
                res = api.unfollow(auth = auth, followeeId = followeeId)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "팔로우를 취소하지 못했어요.")
                }
                return@launch
            }

            if (res.code() == 401) {
                _state.update { it.copy(sessionExpired = true) }
                return@launch
            }

            if (!res.isSuccessful) {
                _state.update {
                    it.copy(error = "팔로우를 취소하지 못했어요. (${res.code()})")
                }
                return@launch
            }

            _state.update { prev ->
                prev.copy(
                    followingItems = prev.followingItems.filterNot { it.id == followeeId },
                    followingCount = (prev.followingCount - 1).coerceAtLeast(0)
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
