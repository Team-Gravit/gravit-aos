package com.inuappcenter.gravit.main.User

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.FriendsCount
import com.inuappcenter.gravit.api.MyLeagueHistory
import com.inuappcenter.gravit.api.MyPageBanner
import com.inuappcenter.gravit.api.MyPageLearningInfo
import com.inuappcenter.gravit.api.MyPageSummary
import com.inuappcenter.gravit.api.RetrofitInstance.api
import com.inuappcenter.gravit.api.SocialFeed
import com.inuappcenter.gravit.api.SocialRecommend
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response

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
    sealed interface LeargueUiState {
        data object Loading : LeargueUiState
        data class Success(val data: MyLeagueHistory) : LeargueUiState
        data object Failed : LeargueUiState
        data object SessionExpired : LeargueUiState
        data object NotFound : LeargueUiState
    }


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
    private val _stateLeague = MutableStateFlow<LeargueUiState>(LeargueUiState.Loading)
    val stateLeague = _stateLeague.asStateFlow()

    fun loadLeague() = viewModelScope.launch {
        _stateLeague.value = LeargueUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateLeague.value = LeargueUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getMyLeagueHistory("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _stateLeague.value = LeargueUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _stateLeague.value = it },
                unauthorizedState = LeargueUiState.SessionExpired,
                notFoundState = LeargueUiState.NotFound,
                failedState = LeargueUiState.Failed
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

    data class Social(
        val recommend: List<SocialRecommend>,
        val feed: SocialFeed,
        val count: FriendsCount
    )

    private var page = 0
    private var hasNext = true
    private var isLoading = false


    private val _stateSocial = MutableStateFlow<SocialUiState>(SocialUiState.Loading)
    val stateSocial = _stateSocial.asStateFlow()

    fun loadSocial() = viewModelScope.launch {
        _stateSocial.value = SocialUiState.Loading

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
            Log.d("SOCIAL", "SUCCESS = $res")

            page = 0
            hasNext = res.feed.hasNextPage

            _stateSocial.value = SocialUiState.Success(res)
        }.onFailure { e ->
            Log.e("SOCIAL", "FAIL", e)
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _stateSocial.value = it },
                unauthorizedState = SocialUiState.SessionExpired,
                notFoundState = SocialUiState.NotFound,
                failedState = SocialUiState.Failed
            )
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
                        )
                    )
                )
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
    fun followRecommend(targetUserId: Long) = viewModelScope.launch {
        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stateSocial.value = SocialUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.followSocial(
                auth = "Bearer ${session.accessToken}",
                userId = targetUserId
            )
        }.onSuccess {
            val currentState = _stateSocial.value as? SocialUiState.Success ?: return@onSuccess

            _stateSocial.value = currentState.copy(
                data = currentState.data.copy(
                    count = currentState.data.count.copy(
                        followingCount = currentState.data.count.followingCount + 1
                    )
                )
            )
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