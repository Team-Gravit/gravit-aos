package com.example.gravit.main.League

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.LeagueItem
import com.example.gravit.api.MyLeague
import com.example.gravit.api.SeasonPopupResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class LeagueViewModel(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed class Source {
        data object UserLeague : Source()
        data class Tier(val leagueId: Int) : Source()
    }

    private val _source = MutableStateFlow<Source>(Source.UserLeague)
    val source = _source.asStateFlow()

    data class PagingUi(
        val items: List<LeagueItem> = emptyList(),
        val page: Int = 0,
        val isLoading: Boolean = false,
        val endReached: Boolean = false,
        val error: String? = null
    )
    private val _ui = MutableStateFlow(PagingUi())
    val state = _ui.asStateFlow()
    private val _sessionExpired = MutableStateFlow(false)
    val sessionExpired = _sessionExpired.asStateFlow()
    sealed class MyLeagueState{
        data object Idle : MyLeagueState()
        data object Loading : MyLeagueState()
        data object SessionExpired : MyLeagueState()
        data class Success(val data: MyLeague) : MyLeagueState()
        data object Failed : MyLeagueState()
    }
    private val _myLeague = MutableStateFlow<MyLeagueState>(MyLeagueState.Idle)
    val myLeague = _myLeague.asStateFlow()

    fun loadMyLeague() = viewModelScope.launch {
        _myLeague.value = MyLeagueState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _myLeague.value = MyLeagueState.SessionExpired
            _sessionExpired.value = true
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.getMyLeague(auth)
        }.onSuccess { res ->
            _myLeague.value = MyLeagueState.Success(res)
        }.onFailure { e ->
                val code = (e as? retrofit2.HttpException)?.code()
                if (code == 401) {
                    AuthPrefs.clear(appContext)
                    _myLeague.value = MyLeagueState.SessionExpired
                    _sessionExpired.value = true
                } else {
                    _myLeague.value = MyLeagueState.Failed
                }
            }
    }

    fun refreshL() {
        _ui.value = PagingUi()
        loadNextL()
    }

    fun selectUserLeague() {
        _source.value = Source.UserLeague
        refreshL()
    }

    fun selectTier(leagueId: Int) {
        _source.value = Source.Tier(leagueId)
        refreshL()
    }

    // 페이징 로드 (끝 근처에서 호출)
    fun loadNextL() = viewModelScope.launch {
        val curr = _ui.value
        if (curr.isLoading || curr.endReached) return@launch

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext); _sessionExpired.value = true; return@launch
        }

        _ui.update { it.copy(isLoading = true, error = null) }

        val auth = "Bearer ${session.accessToken}"
        val nextPage = curr.page

        val pageResp = runCatching {
            when (val s = _source.value) {
                Source.UserLeague -> api.getLeagues_league(auth, nextPage)
                is Source.Tier -> api.getLeagues_tier(auth, s.leagueId, nextPage)
            }
        }.getOrElse { e ->
            val code = (e as? retrofit2.HttpException)?.code()
            if (code == 401) {
                AuthPrefs.clear(appContext)
                _sessionExpired.value = true
            } else {
                _ui.update { it.copy(isLoading = false, error = e.message ?: "불러오기 실패") }
            }
            return@launch
        }

        val newItems = pageResp.contents
        val hasNext  = pageResp.hasNextPage

        _ui.update {
            it.copy(
                items = it.items + newItems,
                page = if (hasNext) nextPage + 1 else nextPage, // 다음 페이지 있을 때만 증가
                isLoading = false,
                endReached = !hasNext
            )
        }
    }

    sealed class SeasonPopupState {
        data object Loading : SeasonPopupState()
        data object SessionExpired : SeasonPopupState()
        data class Ready(
            val data: SeasonPopupResponse,
            val show: Boolean
        ) : SeasonPopupState()
        data class Failed(val message: String? = null) : SeasonPopupState()
    }

    private val _seasonPopup = MutableStateFlow<SeasonPopupState>(SeasonPopupState.Loading)
    val seasonPopup = _seasonPopup.asStateFlow()

    //시즌 팝업
    private val seasonPrefs by lazy {
        appContext.getSharedPreferences("season_popup", Context.MODE_PRIVATE)
    }
    private fun lastShownSeason(): String? =
        seasonPrefs.getString("last_shown_season", null)

    private fun markShownSeason(seasonId: String) {
        seasonPrefs.edit().putString("last_shown_season", seasonId).apply()
    }
    fun confirmSeasonPopup(seasonId: String) {
        markShownSeason(seasonId)
        val cur = _seasonPopup.value
        if (cur is SeasonPopupState.Ready) {
            _seasonPopup.value = cur.copy(show = false)
        }
    }
    fun loadSeasonPopup() = viewModelScope.launch {
        _seasonPopup.value = SeasonPopupState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _sessionExpired.value = true
            _seasonPopup.value = SeasonPopupState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        val res = runCatching { api.getLeagueHome(auth) }
            .getOrElse { e ->
                val code = (e as? retrofit2.HttpException)?.code()
                if (code == 401) {
                    AuthPrefs.clear(appContext)
                    _sessionExpired.value = true
                    _seasonPopup.value = SeasonPopupState.SessionExpired
                } else {
                    _seasonPopup.value = SeasonPopupState.Failed(e.message)
                }
                return@launch
            }
        val seasonId = res.currentSeason.nowSeason
        val shouldShow = res.containsPopup && lastShownSeason() != seasonId
        _seasonPopup.value = SeasonPopupState.Ready(
            data = res,
            show = shouldShow
        )
    }
}

@Suppress("UNCHECKED_CAST")
class LeagueVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LeagueViewModel(api, context.applicationContext) as T
    }
}
