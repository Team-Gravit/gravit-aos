package com.inuappcenter.gravit.main.Ai.Home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.InterviewSessionAnswersResponse
import com.inuappcenter.gravit.api.InterviewSessionSummaryResponse
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiFeedbackVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface SummaryUiState {
        data object Idle : SummaryUiState
        data object Loading : SummaryUiState
        data class Success(
            val data: InterviewSessionSummaryResponse
        ) : SummaryUiState
        data object Failed : SummaryUiState
        data object SessionExpired : SummaryUiState
        data object NotFound : SummaryUiState
    }

    sealed interface AnswersUiState {
        data object Idle : AnswersUiState
        data object Loading : AnswersUiState
        data class Success(
            val data: InterviewSessionAnswersResponse
        ) : AnswersUiState
        data object Failed : AnswersUiState
        data object SessionExpired : AnswersUiState
        data object NotFound : AnswersUiState
    }

    private val _summaryState =
        MutableStateFlow<SummaryUiState>(SummaryUiState.Idle)
    val summaryState = _summaryState.asStateFlow()

    private val _answersState =
        MutableStateFlow<AnswersUiState>(AnswersUiState.Idle)
    val answersState = _answersState.asStateFlow()

    fun loadSummary(sessionId: Long) = viewModelScope.launch {
        _summaryState.value = SummaryUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _summaryState.value = SummaryUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getInterviewSessionSummary(
                auth = "Bearer ${session.accessToken}",
                sessionId = sessionId
            )
        }.onSuccess { res ->
            _summaryState.value = SummaryUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _summaryState.value = it },
                unauthorizedState = SummaryUiState.SessionExpired,
                notFoundState = SummaryUiState.NotFound,
                failedState = SummaryUiState.Failed
            )
        }
    }

    fun loadAnswers(sessionId: Long) = viewModelScope.launch {
        _answersState.value = AnswersUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _answersState.value = AnswersUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getInterviewSessionAnswers(
                auth = "Bearer ${session.accessToken}",
                sessionId = sessionId
            )
        }.onSuccess { res ->
            _answersState.value = AnswersUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _answersState.value = it },
                unauthorizedState = AnswersUiState.SessionExpired,
                notFoundState = AnswersUiState.NotFound,
                failedState = AnswersUiState.Failed
            )
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AiFeedbackVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AiFeedbackVM(
            api,
            context.applicationContext
        ) as T
    }
}