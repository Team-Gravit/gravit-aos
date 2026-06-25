package com.inuappcenter.gravit.main.User

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.FriendsCount
import com.inuappcenter.gravit.api.MyLeagueHistory
import com.inuappcenter.gravit.api.MyPageBanner
import com.inuappcenter.gravit.api.MyPageLearningInfo
import com.inuappcenter.gravit.api.MyPageSummary
import com.inuappcenter.gravit.api.SocialFeed
import com.inuappcenter.gravit.api.SocialRecommend
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserScreenVM (
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface BannersUiState {
        data object Loading : BannersUiState
        data class Success(val data: MyPageBanner) : BannersUiState
        data object Failed : BannersUiState
        data object SessionExpired : BannersUiState
        data object NotFound : BannersUiState
    }
    sealed interface SummaryUiState {
        data object Loading : SummaryUiState
        data class Success(val data: MyPageSummary) : SummaryUiState
        data object Failed : SummaryUiState
        data object SessionExpired : SummaryUiState
        data object NotFound : SummaryUiState
    }
    sealed interface LearningUiState {
        data object Loading : LearningUiState
        data class Success(val data: MyPageLearningInfo) : LearningUiState
        data object Failed : LearningUiState
        data object SessionExpired : LearningUiState
        data object NotFound : LearningUiState
    }
    sealed interface SocialUiState {
        data object Loading : SocialUiState
        data class Success(val data: Social) : SocialUiState
        data object Failed : SocialUiState
        data object SessionExpired : SocialUiState
        data object NotFound : SocialUiState
    }
    sealed interface LeagueUiState {
        data object Loading : LeagueUiState
        data class Success(val data: MyLeagueHistory) : LeagueUiState
        data object Failed : LeagueUiState
        data object SessionExpired : LeagueUiState
        data object NotFound : LeagueUiState
    }
    sealed interface CongratulateUiState {
        data object Idle : CongratulateUiState
        data object Loading : CongratulateUiState
        data object Success : CongratulateUiState
        data object SessionExpired : CongratulateUiState
        data class Failed(val message: String) : CongratulateUiState
    }

    sealed interface FollowUiState {
        data object Idle : FollowUiState
        data object Loading : FollowUiState
        data object Success : FollowUiState
        data object SessionExpired : FollowUiState
        data class Failed(val message: String) : FollowUiState
    }

    data class ErrorResponse(
        val error: String,
        val message: String
    )
    private val _stateBanners = MutableStateFlow<BannersUiState>(BannersUiState.Loading)
    val stateBanners = _stateBanners.asStateFlow()

    fun loadBanners() = viewModelScope.launch {
        _stateBanners.value = BannersUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateBanners.value = BannersUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getBanners("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _stateBanners.value = BannersUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _stateBanners.value = it },
                unauthorizedState = BannersUiState.SessionExpired,
                notFoundState = BannersUiState.NotFound,
                failedState = BannersUiState.Failed
            )
        }
    }

    private val _stateSummary = MutableStateFlow<SummaryUiState>(SummaryUiState.Loading)
    val stateSummary = _stateSummary.asStateFlow()

    fun loadSummary() = viewModelScope.launch {
        _stateSummary.value = SummaryUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateSummary.value = SummaryUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getSummeries("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _stateSummary.value = SummaryUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _stateSummary.value = it },
                unauthorizedState = SummaryUiState.SessionExpired,
                notFoundState = SummaryUiState.NotFound,
                failedState = SummaryUiState.Failed
            )
        }
    }
    private val _stateLeague = MutableStateFlow<LeagueUiState>(LeagueUiState.Loading)
    val stateLeague = _stateLeague.asStateFlow()

    fun loadLeague() = viewModelScope.launch {
        _stateLeague.value = LeagueUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateLeague.value = LeagueUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getMyLeagueHistory("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _stateLeague.value = LeagueUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _stateLeague.value = it },
                unauthorizedState = LeagueUiState.SessionExpired,
                notFoundState = LeagueUiState.NotFound,
                failedState = LeagueUiState.Failed
            )
        }
    }
    private val _stateLearning = MutableStateFlow<LearningUiState>(LearningUiState.Loading)
    val stateLearning = _stateLearning.asStateFlow()

    fun loadLearning() = viewModelScope.launch {
        _stateLearning.value = LearningUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateLearning.value = LearningUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getMyPageLearning("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _stateLearning.value = LearningUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _stateLearning.value = it },
                unauthorizedState = LearningUiState.SessionExpired,
                notFoundState = LearningUiState.NotFound,
                failedState = LearningUiState.Failed
            )
        }
    }
    private val _stateCongratulate = MutableStateFlow<CongratulateUiState>(CongratulateUiState.Idle)
    val stateCongratulate = _stateCongratulate.asStateFlow()

    fun congratulate(feedId: Long) = viewModelScope.launch {
        _stateCongratulate.value = CongratulateUiState.Loading
        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateCongratulate.value = CongratulateUiState.SessionExpired
            return@launch
        }
        runCatching {
            api.getCongratulate(auth = "Bearer ${session.accessToken}", feedId = feedId)
        }.onSuccess { res ->
            when {
                res.isSuccessful -> {
                    _stateCongratulate.value = CongratulateUiState.Success
                }

                res.code() == 400 -> {
                    val message = runCatching {
                        res.errorBody()?.string()
                            ?.let { Gson().fromJson(it, ErrorResponse::class.java).message }
                    }.getOrNull()

                    _stateCongratulate.value = CongratulateUiState.Failed(message ?: "오늘 축하 횟수를 모두 사용했어요.")
                }

                res.code() == 404 -> {
                    _stateCongratulate.value = CongratulateUiState.Failed("피드를 찾을 수 없습니다.")
                }

                res.code() == 401 -> {
                    AuthPrefs.clear(appContext)
                    _stateCongratulate.value = CongratulateUiState.SessionExpired
                }

                else -> {
                    _stateCongratulate.value = CongratulateUiState.Failed("오류가 발생했습니다.")
                }
            }
        }.onFailure {
            _stateCongratulate.value = CongratulateUiState.Failed("오류가 발생했습니다.")
        }
    }
    data class Social(
        val recommend: List<SocialRecommend>,
        val feed: SocialFeed,
        val count: FriendsCount,
        val loadMoreError: String? = null
    )

    private var page = 0
    private var hasNext = true
    private var isLoading = false


    private val _stateSocial = MutableStateFlow<SocialUiState>(SocialUiState.Loading)
    val stateSocial = _stateSocial.asStateFlow()

    fun loadSocial() = viewModelScope.launch {
        if (isLoading) return@launch
        _stateSocial.value = SocialUiState.Loading
        isLoading = true

        try {
            val session = AuthPrefs.load(appContext)
            if (session == null) {
                AuthPrefs.clear(appContext)
                _stateSocial.value = SocialUiState.SessionExpired
                return@launch
            }
            runCatching {
                coroutineScope {
                    val socialRecommend = async { api.getSocialRecommend("Bearer ${session.accessToken}") }
                    val socialFeed = async { api.getSocialFeed(
                        auth = "Bearer ${session.accessToken}",
                        page = 0
                    )
                    }
                    val count = async { api.getFriendsCount( "Bearer ${session.accessToken}") }
                    Social(
                        recommend = socialRecommend.await(),
                        feed = socialFeed.await(),
                        count = count.await()
                    )
                }
            }.onSuccess { res ->
                page = 0
                hasNext = res.feed.hasNextPage

                _stateSocial.value = SocialUiState.Success(res)
            }.onFailure { e ->
                handleApiFailure(
                    e = e,
                    appContext = appContext,
                    onStateChange = { _stateSocial.value = it },
                    unauthorizedState = SocialUiState.SessionExpired,
                    notFoundState = SocialUiState.NotFound,
                    failedState = SocialUiState.Failed
                )
            }
        } finally {
            isLoading = false
        }
    }
    fun loadMoreSocial() = viewModelScope.launch {
        if (isLoading || !hasNext) return@launch

        val currentState = _stateSocial.value as? SocialUiState.Success ?: return@launch

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateSocial.value = SocialUiState.SessionExpired
            return@launch
        }

        isLoading = true

        try {
            runCatching {
                api.getSocialFeed(
                    auth = "Bearer ${session.accessToken}",
                    page = page + 1
                )
            }.onSuccess { next ->
                page += 1
                hasNext = next.hasNextPage

                _stateSocial.value = currentState.copy(
                    data = currentState.data.copy(
                        feed = currentState.data.feed.copy(
                            contents = currentState.data.feed.contents + next.contents,
                            hasNextPage = next.hasNextPage
                        ),
                        loadMoreError = null
                    )
                )
            }.onFailure {
                _stateSocial.value = currentState.copy(
                    data = currentState.data.copy(
                        loadMoreError = "불러오기에 실패했습니다."
                    )
                )
            }
        } finally {
            isLoading = false
        }
    }

    private val _stateFollow = MutableStateFlow<FollowUiState>(FollowUiState.Idle)

    val stateFollow = _stateFollow.asStateFlow()

    fun followRecommend(targetUserId: Long) = viewModelScope.launch {

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateFollow.value = FollowUiState.SessionExpired
            return@launch
        }
        val currentState = _stateSocial.value as? SocialUiState.Success ?: run {
                    _stateFollow.value = FollowUiState.Idle
                    return@launch
        }

        _stateFollow.value = FollowUiState.Loading

        runCatching {
            api.followSocial(
                auth = "Bearer ${session.accessToken}",
                userId = targetUserId
            )
        }.onSuccess { res ->
            when {
                res.isSuccessful -> {
                    _stateFollow.value = FollowUiState.Success
                    _stateSocial.value = currentState.copy(
                        data = currentState.data.copy(
                            count = currentState.data.count.copy(
                                followingCount = currentState.data.count.followingCount + 1
                            )
                        )
                    )
                }

                res.code() == 400 -> {
                    val message = runCatching {
                        res.errorBody()?.string()
                            ?.let { Gson().fromJson(it, ErrorResponse::class.java).message }
                    }.getOrNull()

                    _stateFollow.value = FollowUiState.Failed(message ?: "자기 자신에게 팔로잉은 불가능합니다.")
                }

                res.code() == 409 -> {
                    _stateFollow.value = FollowUiState.Failed("이미 팔로잉을 한 유저입니다.")
                }

                res.code() == 401 -> {
                    AuthPrefs.clear(appContext)
                    _stateFollow.value = FollowUiState.SessionExpired
                }

                else -> {
                    _stateFollow.value = FollowUiState.Failed("오류가 발생했습니다.")
                }
            }
        }.onFailure { e ->
            _stateFollow.value = FollowUiState.Failed("오류가 발생했습니다.")
        }
    }

    fun clearLoadMoreError() {
        val currentState = _stateSocial.value as? SocialUiState.Success ?: return

        _stateSocial.value = currentState.copy(
            data = currentState.data.copy(
                loadMoreError = null
            )
        )
    }
}

@Suppress("UNCHECKED_CAST")
class UserVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserScreenVM(api, context.applicationContext) as T
    }
}