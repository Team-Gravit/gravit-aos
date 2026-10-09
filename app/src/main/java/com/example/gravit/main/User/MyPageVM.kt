package com.inuappcenter.gravit.main.User

import android.R.id.message
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.FriendsCount
import com.inuappcenter.gravit.api.MyLeagueHistory
import com.inuappcenter.gravit.api.MyPageBanner
import com.inuappcenter.gravit.api.MyPageHistory
import com.inuappcenter.gravit.api.MyPageLearningSummary
import com.inuappcenter.gravit.api.MyPageTopChapter
import com.inuappcenter.gravit.api.MyPageWeakConcept
import com.inuappcenter.gravit.api.MyPageWeeklyReport
import com.inuappcenter.gravit.api.SocialFeed
import com.inuappcenter.gravit.api.SocialRecommend
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.jvm.java

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
        data class Success(val data: MyPageLearning) : LearningUiState
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
    sealed interface CongratulateEvent {
        data class Success(
            val targetId: Long
        ) : CongratulateEvent
        data class Failed(
            val targetId: Long,
            val message: String
        ) : CongratulateEvent
        data object SessionExpired : CongratulateEvent
    }

    sealed interface FollowEvent {
        data class Success(
            val userId: Long,
            val isFollowing: Boolean
        ) : FollowEvent
        data class Failed(
            val userId: Long,
            val message: String
        ) : FollowEvent
        data object SessionExpired : FollowEvent
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
            api.getMyPageBanners("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _stateBanners.value = BannersUiState.Success(res)
        }.onFailure { e ->
            Log.e("MYPAGE_SUMMARY", "loadSummary failed", e)
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

    data class MyPageSummary(
        val history: MyPageHistory,
        val summaries: MyPageLearningSummary
    )
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
            coroutineScope {
                val history = async { api.getMyPageHistory("Bearer ${session.accessToken}",  _selectedYear.value) }
                val summaries = async { api.getMyPageSummaries(auth = "Bearer ${session.accessToken}") }
                MyPageSummary(
                    history = history.await(),
                    summaries = summaries.await()
                )
            }
        }.onSuccess { res ->
            _stateSummary.value = SummaryUiState.Success(res)
        }.onFailure { e ->
            Log.e("MYPAGE_SUMMARY", "loadSummary failed", e)
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

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear = _selectedYear.asStateFlow()
    private var selectYearJob: Job? = null

    private val _yearChangeError = MutableStateFlow<String?>(null)
    val yearChangeError = _yearChangeError.asStateFlow()

    fun clearYearChangeError() {
        _yearChangeError.value = null
    }
    fun selectYear(year: Int) = viewModelScope.launch {
        if (_selectedYear.value == year) return@launch
        selectYearJob?.cancel()

        selectYearJob = viewModelScope.launch {
            val currentState =
                _stateSummary.value as? SummaryUiState.Success ?: return@launch

            val session = AuthPrefs.load(appContext)
            if (session == null) {
                AuthPrefs.clear(appContext)
                _stateSummary.value = SummaryUiState.SessionExpired
                return@launch
            }

            runCatching {
                api.getMyPageHistory("Bearer ${session.accessToken}", year)
            }.onSuccess { history ->
                _selectedYear.value = year
                _stateSummary.value =
                    SummaryUiState.Success(currentState.data.copy(history = history))
            }.onFailure { e ->
                Log.e("MYPAGE_HISTORY", "load history failed: year=$year", e)

                handleApiFailure(
                    e = e,
                    appContext = appContext,
                    onStateChange = { state ->
                        when (state) {
                            SummaryUiState.Failed -> {
                                _yearChangeError.value = "오류가 발생했습니다."
                            }

                            else -> {
                                _stateSummary.value = state
                            }
                        }
                    },
                    unauthorizedState = SummaryUiState.SessionExpired,
                    notFoundState = SummaryUiState.NotFound,
                    failedState = SummaryUiState.Failed
                )
            }
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
    data class MyPageLearning(
        val weeklyReport: MyPageWeeklyReport,
        val weakConcepts: List<MyPageWeakConcept>,
        val topChapters: List<MyPageTopChapter>
    )
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
            coroutineScope {
                val weeklyReport = async { api.getMyPageWeeklyReport("Bearer ${session.accessToken}")}
                val weakConcept = async { api.getMyPageWeakConcepts("Bearer ${session.accessToken}")}
                val topChapter = async { api.getMyPageTopChapters("Bearer ${session.accessToken}")}
                MyPageLearning(
                    weeklyReport = weeklyReport.await(),
                    weakConcepts = weakConcept.await(),
                    topChapters = topChapter.await()
                )
            }
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
    private val _congratulateEvent = MutableSharedFlow<CongratulateEvent>(extraBufferCapacity = 16)
    val congratulateEvent = _congratulateEvent.asSharedFlow()
    private val _congratulatingIds = MutableStateFlow<Set<Long>>(emptySet())
    val congratulatingIds = _congratulatingIds.asStateFlow()

    fun congratulate(feedId: Long) = viewModelScope.launch {
        if (feedId in _congratulatingIds.value) return@launch

        _congratulatingIds.update { it + feedId }

        try {
            val session = AuthPrefs.load(appContext)
            if (session == null) {
                AuthPrefs.clear(appContext)
                _congratulateEvent.emit(
                    CongratulateEvent.SessionExpired
                )
                return@launch
            }
            runCatching {
            api.getCongratulate(auth = "Bearer ${session.accessToken}", feedId = feedId)
        }.onSuccess { res ->
            when {
                res.isSuccessful -> {
                    _stateSocial.update { state ->
                        val success = state as? SocialUiState.Success
                            ?: return@update state

                        success.copy(
                            data = success.data.copy(
                                feed = success.data.feed.copy(
                                    contents = success.data.feed.contents.map { feed ->
                                        if (feed.feedId == feedId) {
                                            feed.copy(congratulated = true)
                                        } else {
                                            feed
                                        }
                                    }
                                )
                            )
                        )
                    }
                    _congratulateEvent.emit(
                        CongratulateEvent.Success(feedId)
                    )
                }

                res.code() == 400 -> {
                    val message = runCatching {
                        res.errorBody()?.string()
                            ?.let { Gson().fromJson(it, ErrorResponse::class.java).message }
                    }.getOrNull()

                    _congratulateEvent.emit(
                        CongratulateEvent.Failed(
                            targetId = feedId,
                            message = message ?: "오늘 축하 횟수를 모두 사용했어요."
                        )
                    )
                }

                res.code() == 404 -> {
                    _congratulateEvent.emit(
                        CongratulateEvent.Failed(
                            targetId = feedId,
                            message = "피드를 찾을 수 없습니다."
                        )
                    )
                }

                res.code() == 401 -> {
                    AuthPrefs.clear(appContext)
                    _congratulateEvent.emit(
                        CongratulateEvent.SessionExpired
                    )
                }
                res.code() == 409 -> {
                    val message = runCatching {
                        res.errorBody()?.string()
                            ?.let {
                                Gson().fromJson(
                                    it,
                                    ErrorResponse::class.java
                                ).message
                            }
                    }.getOrNull()

                    _congratulateEvent.emit(
                        CongratulateEvent.Failed(
                            targetId = feedId,
                            message = message ?: "이미 축하한 피드입니다."
                        )
                    )
                }
                else -> {
                    _congratulateEvent.emit(
                        CongratulateEvent.Failed(
                            targetId = feedId,
                            message = "오류가 발생했습니다."
                        )
                    )
                }
            }
        }.onFailure {
            _congratulateEvent.emit(
                CongratulateEvent.Failed(
                    targetId = feedId,
                    message = "오류가 발생했습니다."
                )
            )
        }
        } finally {
            _congratulatingIds.update { it - feedId }
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

    private val _followEvent = MutableSharedFlow<FollowEvent>(extraBufferCapacity = 16)
    val followEvent = _followEvent.asSharedFlow()

    private val _followingStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val followingStates = _followingStates.asStateFlow()
    private val _followingRequestIds = MutableStateFlow<Set<Long>>(emptySet())
    val followingRequestIds = _followingRequestIds.asStateFlow()

    fun followRecommend(targetUserId: Long) = viewModelScope.launch {
        if (targetUserId in _followingRequestIds.value) return@launch

        _followingRequestIds.update { it + targetUserId }

        try {
            val session = AuthPrefs.load(appContext)

            if (session == null) {
                AuthPrefs.clear(appContext)
                _followEvent.emit(FollowEvent.SessionExpired)
                return@launch
            }

            runCatching {
                api.followSocial(
                    auth = "Bearer ${session.accessToken}",
                    userId = targetUserId
                )
            }.onSuccess { res ->
                when {
                    res.isSuccessful -> {
                        _stateSocial.update { state ->
                            val success = state as? SocialUiState.Success
                                ?: return@update state

                            success.copy(
                                data = success.data.copy(
                                    count = success.data.count.copy(
                                        followingCount =
                                            success.data.count.followingCount + 1
                                    )
                                )
                            )
                        }

                        _followingStates.update {
                            it + (targetUserId to true)
                        }

                        _followEvent.emit(
                            FollowEvent.Success(
                                userId = targetUserId,
                                isFollowing = true
                            )
                        )
                    }

                    res.code() == 400 -> {
                        val message = runCatching {
                            res.errorBody()?.string()
                                ?.let {
                                    Gson().fromJson(
                                        it,
                                        ErrorResponse::class.java
                                    ).message
                                }
                        }.getOrNull()

                        _followEvent.emit(
                            FollowEvent.Failed(
                                userId = targetUserId,
                                message = message
                                    ?: "자기 자신에게 팔로잉은 불가능합니다."
                            )
                        )
                    }

                    res.code() == 409 -> {
                        _followEvent.emit(
                            FollowEvent.Failed(
                                userId = targetUserId,
                                message = "이미 팔로잉을 한 유저입니다."
                            )
                        )
                    }

                    res.code() == 401 -> {
                        AuthPrefs.clear(appContext)
                        _followEvent.emit(FollowEvent.SessionExpired)
                    }

                    else -> {
                        _followEvent.emit(
                            FollowEvent.Failed(
                                userId = targetUserId,
                                message = "오류가 발생했습니다."
                            )
                        )
                    }
                }
            }.onFailure {
                _followEvent.emit(
                    FollowEvent.Failed(
                        userId = targetUserId,
                        message = "오류가 발생했습니다."
                    )
                )
            }
        } finally {
            _followingRequestIds.update { it - targetUserId }
        }
    }
    fun unfollowRecommend(targetUserId: Long) = viewModelScope.launch {
        if (targetUserId in _followingRequestIds.value) return@launch

        _followingRequestIds.update { it + targetUserId }

        try {
            val session = AuthPrefs.load(appContext)

            if (session == null) {
                AuthPrefs.clear(appContext)
                _followEvent.emit(FollowEvent.SessionExpired)
                return@launch
            }

            runCatching {
                api.unfollow(
                    auth = "Bearer ${session.accessToken}",
                    followeeId = targetUserId
                )
            }.onSuccess { res ->
                when {
                    res.isSuccessful -> {
                        _stateSocial.update { state ->
                            val success = state as? SocialUiState.Success
                                ?: return@update state

                            success.copy(
                                data = success.data.copy(
                                    count = success.data.count.copy(
                                        followingCount =
                                            (success.data.count.followingCount - 1)
                                                .coerceAtLeast(0)
                                    )
                                )
                            )
                        }

                        _followingStates.update {
                            it + (targetUserId to false)
                        }

                        _followEvent.emit(
                            FollowEvent.Success(
                                userId = targetUserId,
                                isFollowing = false
                            )
                        )
                    }

                    res.code() == 401 -> {
                        AuthPrefs.clear(appContext)
                        _followEvent.emit(FollowEvent.SessionExpired)
                    }

                    else -> {
                        _followEvent.emit(
                            FollowEvent.Failed(
                                userId = targetUserId,
                                message = "팔로우 취소에 실패했습니다."
                            )
                        )
                    }
                }
        }.onFailure {
            _followEvent.emit(
                FollowEvent.Failed(
                    userId = targetUserId,
                    message = "오류가 발생했습니다."
                )
            )
        }
        } finally {
            _followingRequestIds.update { it - targetUserId }
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