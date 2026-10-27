package com.inuappcenter.gravit.main.League

import android.content.Context
import android.os.Build
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.LeagueItem
import com.inuappcenter.gravit.api.MyLeague
import com.inuappcenter.gravit.api.SeasonPopupResponse
import com.inuappcenter.gravit.error.handleApiFailure
import com.inuappcenter.gravit.error.mapServerError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LeagueViewModel(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {
    private val _checkLeague = MutableStateFlow(false)
    val checkLeague = _checkLeague.asStateFlow()

    private fun isCheckLeague(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val now = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"))
        if (now.dayOfWeek != java.time.DayOfWeek.MONDAY) return false
        val time = now.toLocalTime()
        return !time.isBefore(java.time.LocalTime.of(0, 0)) &&
                time.isBefore(java.time.LocalTime.of(0, 5))
    }

    sealed class Source {
        data object UserLeague : Source()
        data class Tier(val leagueId: Long) : Source()
    }

    private val _source = MutableStateFlow<Source>(Source.UserLeague)
    val source = _source.asStateFlow()

    data class PagingUi(
        val items: List<LeagueItem> = emptyList(),
        val minPage: Int = 0,
        val maxPage: Int = 0,
        val initialized: Boolean = false,
        val isLoadingPrevious: Boolean = false,
        val isLoadingNext: Boolean = false,
        val endReachedPrevious: Boolean = true,
        val endReachedNext: Boolean = false,
        val errorPrevious: String? = null,
        val errorNext: String? = null,
        val scrollToMe: Boolean = false,
        val generation: Int = 0
    )

    private val _ui = MutableStateFlow(PagingUi())
    val state = _ui.asStateFlow()
    private var pagingGeneration = 0

    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired = _sessionExpired.asStateFlow()
    private val _notFound = MutableStateFlow(false)
    val notFound = _notFound.asStateFlow()

    sealed class MyLeagueState {
        data object Idle : MyLeagueState()
        data object Loading : MyLeagueState()
        data object SessionExpired : MyLeagueState()
        data class Success(val data: MyLeague) : MyLeagueState()
        data object Failed : MyLeagueState()
        data object NotFound : MyLeagueState()
    }

    private val _myLeague = MutableStateFlow<MyLeagueState>(MyLeagueState.Idle)
    val myLeague = _myLeague.asStateFlow()

    fun loadMyLeague() = viewModelScope.launch {
        _myLeague.value = MyLeagueState.Loading
        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _myLeague.value = MyLeagueState.SessionExpired
            return@launch
        }
        runCatching { api.getMyLeague("Bearer ${session.accessToken}") }
            .onSuccess { _myLeague.value = MyLeagueState.Success(it) }
            .onFailure { e ->
                handleApiFailure(
                    e = e,
                    appContext = appContext,
                    onStateChange = { _myLeague.value = it },
                    unauthorizedState = MyLeagueState.SessionExpired,
                    notFoundState = MyLeagueState.NotFound,
                    failedState = MyLeagueState.Failed
                )
            }
    }

    private enum class Direction { INITIAL, PREVIOUS, NEXT }

    fun selectUserLeague(my: MyLeague) {
        val page = ((my.rank - 1).coerceAtLeast(0)) / 10
        pagingGeneration++
        _source.value = Source.UserLeague
        _ui.value = PagingUi(
            minPage = page,
            maxPage = page,
            endReachedPrevious = page == 0,
            scrollToMe = true,
            generation = pagingGeneration
        )
        loadPage(page, Direction.INITIAL)
    }

    fun selectTier(leagueId: Long) {
        if (_source.value == Source.Tier(leagueId)) return
        pagingGeneration++
        _source.value = Source.Tier(leagueId)
        _ui.value = PagingUi(generation = pagingGeneration)
        loadPage(0, Direction.INITIAL)
    }

    fun loadPreviousL() {
        val ui = _ui.value
        if (!ui.initialized || ui.isLoadingPrevious || ui.isLoadingNext || ui.endReachedPrevious) return
        loadPage(ui.minPage - 1, Direction.PREVIOUS)
    }

    fun loadNextL() {
        val ui = _ui.value

        if (ui.isLoadingPrevious || ui.isLoadingNext) return

        if (!ui.initialized) {
            loadPage(ui.minPage, Direction.INITIAL)
            return
        }

        if (ui.endReachedNext) return

        loadPage(ui.maxPage + 1, Direction.NEXT)
    }

    fun markInitialScrollCompleted() {
        _ui.update { it.copy(scrollToMe = false) }
    }

    private fun loadPage(page: Int, direction: Direction) = viewModelScope.launch {
        val generation = pagingGeneration
        val requestedSource = _source.value
        if (isCheckLeague()) {
            _checkLeague.value = true
            return@launch
        }
        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _myLeague.value = MyLeagueState.SessionExpired
            return@launch
        }
        _ui.update {
            when (direction) {
                Direction.PREVIOUS -> it.copy(isLoadingPrevious = true, errorPrevious = null)
                else -> it.copy(isLoadingNext = true, errorNext = null)
            }
        }
        val result = runCatching {
            when (requestedSource) {
                Source.UserLeague -> api.getLeagues_league("Bearer ${session.accessToken}", page)
                is Source.Tier -> api.getLeagues_tier("Bearer ${session.accessToken}", requestedSource.leagueId, page)
            }
        }
        if (generation != pagingGeneration || requestedSource != _source.value) return@launch
        val response = result.getOrElse { e ->
            if (e is HttpException && e.code() == 401) {
                AuthPrefs.clear(appContext)
                _sessionExpired.value = true
            } else if (e is HttpException && e.code() == 404) {
                _notFound.value = true
            }
            val message = if (e is HttpException) {
                mapServerError(e.response()?.errorBody()?.string())
            } else "오류가 발생했습니다."
            _ui.update {
                when (direction) {
                    Direction.PREVIOUS -> it.copy(isLoadingPrevious = false, errorPrevious = message)
                    else -> it.copy(isLoadingNext = false, errorNext = message)
                }
            }
            return@launch
        }
        _ui.update { current ->
            when (direction) {
                Direction.INITIAL -> current.copy(
                    items = response.contents.distinctBy { it.userId ?: Any() },
                    initialized = true,
                    isLoadingNext = false,
                    endReachedNext = !response.hasNextPage,
                    endReachedPrevious = page == 0
                )
                Direction.PREVIOUS -> current.copy(
                    items = (response.contents + current.items)
                        .distinctBy { it.userId ?: Any() },
                    minPage = page,
                    isLoadingPrevious = false,
                    endReachedPrevious = page == 0
                )

                Direction.NEXT -> current.copy(
                    items = (current.items + response.contents)
                        .distinctBy { it.userId ?: Any() },
                    maxPage = page,
                    isLoadingNext = false,
                    endReachedNext = !response.hasNextPage
                )
            }
        }
        if (
            direction == Direction.INITIAL &&
            response.hasNextPage &&
            response.contents.isNotEmpty()
        ) {
            loadNextL()
        }

    }

    sealed class SeasonPopupState {
        data object Loading : SeasonPopupState()
        data object SessionExpired : SeasonPopupState()
        data class Ready(val data: SeasonPopupResponse, val show: Boolean) : SeasonPopupState()
        data object Failed : SeasonPopupState()
        data object NotFound : SeasonPopupState()
    }

    private val _seasonPopup = MutableStateFlow<SeasonPopupState>(SeasonPopupState.Loading)
    val seasonPopup = _seasonPopup.asStateFlow()
    private val seasonPrefs by lazy {
        appContext.getSharedPreferences("season_popup", Context.MODE_PRIVATE)
    }
    private fun lastShownSeason(): String? = seasonPrefs.getString("last_shown_season", null)
    private fun markShownSeason(seasonId: String) {
        seasonPrefs.edit { putString("last_shown_season", seasonId) }
    }
    fun confirmSeasonPopup(seasonId: String) {
        markShownSeason(seasonId)
        val cur = _seasonPopup.value
        if (cur is SeasonPopupState.Ready) _seasonPopup.value = cur.copy(show = false)
    }
    fun loadSeasonPopup() = viewModelScope.launch {
        _seasonPopup.value = SeasonPopupState.Loading
        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _seasonPopup.value = SeasonPopupState.SessionExpired
            return@launch
        }
        runCatching { api.getLeagueHome("Bearer ${session.accessToken}") }
            .onSuccess { res ->
                val seasonId = res.currentSeason.nowSeason
                _seasonPopup.value = SeasonPopupState.Ready(
                    data = res,
                    show = res.containsPopup && lastShownSeason() != seasonId
                )
            }
            .onFailure { e ->
                handleApiFailure(
                    e = e,
                    appContext = appContext,
                    onStateChange = { _seasonPopup.value = it },
                    unauthorizedState = SeasonPopupState.SessionExpired,
                    notFoundState = SeasonPopupState.NotFound,
                    failedState = SeasonPopupState.Failed
                )
            }
    }
}

@Suppress("UNCHECKED_CAST")
class LeagueVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        LeagueViewModel(api, context.applicationContext) as T
}
