package com.inuappcenter.gravit.main.Ai.Home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.InterviewDashboardResponse
import com.inuappcenter.gravit.api.InterviewSessionHistorySliceResponse
import com.inuappcenter.gravit.api.WeakestTopic
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiHomeVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface DashboardUiState {
        data object Loading : DashboardUiState
        data class Success(
            val data: InterviewDashboardResponse
        ) : DashboardUiState
        data object Failed : DashboardUiState
        data object SessionExpired : DashboardUiState
        data object NotFound : DashboardUiState
    }

    sealed interface HistoryUiState {
        data object Loading : HistoryUiState
        data class Success(
            val data: InterviewSessionHistorySliceResponse
        ) : HistoryUiState
        data object Failed : HistoryUiState
        data object SessionExpired : HistoryUiState
        data object NotFound : HistoryUiState
    }

    sealed interface WeakTopicsUiState {
        data object Loading : WeakTopicsUiState

        data class Success(
            val data: List<WeakestTopic>
        ) : WeakTopicsUiState

        data object Failed : WeakTopicsUiState
        data object SessionExpired : WeakTopicsUiState
        data object NotFound : WeakTopicsUiState
    }

    private val _dashboardState =
        MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val dashboardState = _dashboardState.asStateFlow()

    private val _historyState =
        MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val historyState = _historyState.asStateFlow()

    private val _weakTopicsState =
        MutableStateFlow<WeakTopicsUiState>(WeakTopicsUiState.Loading)
    val weakTopicsState = _weakTopicsState.asStateFlow()

    fun loadDashboard() = viewModelScope.launch {
        _dashboardState.value = DashboardUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _dashboardState.value = DashboardUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getInterviewDashboard("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _dashboardState.value = DashboardUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _dashboardState.value = it },
                unauthorizedState = DashboardUiState.SessionExpired,
                notFoundState = DashboardUiState.NotFound,
                failedState = DashboardUiState.Failed
            )
        }
    }

    fun loadHistory(
        page: Int = 0,
        sort: String = "LATEST"
    ) = viewModelScope.launch {
        _historyState.value = HistoryUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _historyState.value = HistoryUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getInterviewSessions(
                auth = "Bearer ${session.accessToken}",
                page = page,
                sort = sort
            )
        }.onSuccess { res ->
            _historyState.value = HistoryUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _historyState.value = it },
                unauthorizedState = HistoryUiState.SessionExpired,
                notFoundState = HistoryUiState.NotFound,
                failedState = HistoryUiState.Failed
            )
        }
    }

    fun loadWeakTopics() = viewModelScope.launch {
        _weakTopicsState.value = WeakTopicsUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _weakTopicsState.value = WeakTopicsUiState.SessionExpired
            return@launch
        }
        runCatching {
            api.getInterviewWeakTopics("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _weakTopicsState.value = WeakTopicsUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _weakTopicsState.value = it },
                unauthorizedState = WeakTopicsUiState.SessionExpired,
                notFoundState = WeakTopicsUiState.NotFound,
                failedState = WeakTopicsUiState.Failed
            )
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AiHomeVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AiHomeVM(
            api,
            context.applicationContext
        ) as T
    }
}