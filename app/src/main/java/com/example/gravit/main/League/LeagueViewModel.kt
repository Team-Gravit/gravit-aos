package com.example.gravit.main.League

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.LeagueItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class MyRankUi(
    val rank: Int,
    val level: Int,
    val xp: Int,
    val nickname: String,
    val profileImgNumber: Int,
    val league: String?,
    val lp: Int
)


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

    private val _myRank = MutableStateFlow<MyRankUi?>(null)
    val myRank = _myRank.asStateFlow()
    private var myNickname: String? = null
    private var myEXP: Int = 0
    var myLeague: String? = null
    @Volatile private var myRankPrefetching = false


    fun loadMyUser() = viewModelScope.launch {

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _sessionExpired.value = true
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching { api.getMainPage(auth) }
            .onSuccess { user ->
                myNickname = user.nickname
                myEXP = user.xp
                myLeague = user.league
            }
            .onFailure { e ->
                if ((e as? retrofit2.HttpException)?.code() == 401) {
                    AuthPrefs.clear(appContext); _sessionExpired.value = true
                }
            }
    }

    fun LeagueItem.toMyRankUi(): MyRankUi =
        MyRankUi(
            rank = rank,
            level = level,
            xp = myEXP,
            nickname = nickname,
            profileImgNumber = profileImgNumber,
            league = myLeague,
            lp = lp
        )

    fun prefetchMyRank() = viewModelScope.launch {
        if (myRankPrefetching || _myRank.value != null) return@launch
        myRankPrefetching = true
        try {
            // 닉네임 미로딩 시 먼저 불러옴
            if (myNickname == null) loadMyUser().join()

            val found = fetchMyRankFromUserLeague()   // ← 아래 suspend 함수
            if (found != null) _myRank.value = found.toMyRankUi()
        } finally {
            myRankPrefetching = false
        }
    }

    private suspend fun fetchMyRankFromUserLeague(
        maxPages: Int = 10 // 안전장치: 필요시 늘리거나 제거
    ): LeagueItem? {
        val session = AuthPrefs.load(appContext) ?: run { _sessionExpired.value = true; return null }
        if (AuthPrefs.isExpired(session)) { AuthPrefs.clear(appContext); _sessionExpired.value = true; return null }

        val auth = "Bearer ${session.accessToken}"
        val target = myNickname ?: return null

        var page = 0
        while (page < maxPages) {
            val list = runCatching { api.getLeagues_league(auth, page) }
                .getOrElse { e ->
                    if ((e as? retrofit2.HttpException)?.code() == 401) {
                        AuthPrefs.clear(appContext); _sessionExpired.value = true
                    }
                    return null
                }

            if (list.isEmpty()) break

            val match = list.find { it.nickname == target }
            if (match != null) return match

            page++
        }
        return null
    }

    fun refreshL() {
        _ui.value = PagingUi()
        loadNextL()
    }

    //소스를 내 리그로 전환
    fun selectUserLeague() {
        _source.value = Source.UserLeague
        refreshL()
    }

    //소스를 특정 티어로 전환
    fun selectTier(leagueId: Int) {
        _source.value = Source.Tier(leagueId)
        refreshL()
    }

    //끝 근처에서 다음 페이지 로드
    fun loadNextL() = viewModelScope.launch {
        val curr = _ui.value
        if (curr.isLoading || curr.endReached) return@launch

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _sessionExpired.value = true
            return@launch
        }

        _ui.update { it.copy(isLoading = true, error = null) }

        val auth = "Bearer ${session.accessToken}"
        val nextPage = curr.page

        val list = runCatching {
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

        if (list.isEmpty()) {
            _ui.update { it.copy(isLoading = false, endReached = true) }
        } else {
            _ui.update {
                it.copy(
                    items = it.items + list,
                    page = nextPage + 1,
                    isLoading = false
                )
            }
        }
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
