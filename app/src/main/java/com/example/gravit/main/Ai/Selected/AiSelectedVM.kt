package com.inuappcenter.gravit.main.Ai.Selected

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.InterviewSessionCreateRequest
import com.inuappcenter.gravit.api.InterviewStack
import com.inuappcenter.gravit.api.InterviewStackGroupResponse
import com.inuappcenter.gravit.api.InterviewTopic
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiSelectedVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface TopicsUiState {
        data object Idle : TopicsUiState
        data object Loading : TopicsUiState
        data class Success(val data: List<InterviewTopic>) : TopicsUiState
        data object Failed : TopicsUiState
        data object SessionExpired : TopicsUiState
        data object NotFound : TopicsUiState
    }

    sealed interface StackGroupsUiState {
        data object Idle : StackGroupsUiState
        data object Loading : StackGroupsUiState
        data class Success(val data: List<InterviewStackGroupResponse>) : StackGroupsUiState
        data object Failed : StackGroupsUiState
        data object SessionExpired : StackGroupsUiState
        data object NotFound : StackGroupsUiState
    }

    sealed interface StacksUiState {
        data object Idle : StacksUiState
        data object Loading : StacksUiState
        data class Success(val data: List<InterviewStack>) : StacksUiState
        data object Failed : StacksUiState
        data object SessionExpired : StacksUiState
        data object NotFound : StacksUiState
    }

    private val _topicsState = MutableStateFlow<TopicsUiState>(TopicsUiState.Idle)
    val topicsState = _topicsState.asStateFlow()

    private val _stackGroupsState = MutableStateFlow<StackGroupsUiState>(StackGroupsUiState.Idle)
    val stackGroupsState = _stackGroupsState.asStateFlow()

    private val _stacksState = MutableStateFlow<StacksUiState>(StacksUiState.Idle)
    val stacksState = _stacksState.asStateFlow()

    fun loadTopics() = viewModelScope.launch {
        _topicsState.value = TopicsUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _topicsState.value = TopicsUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getInterviewTopics(
                auth = "Bearer ${session.accessToken}"
            )
        }.onSuccess { res ->
            _topicsState.value = TopicsUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _topicsState.value = it },
                unauthorizedState = TopicsUiState.SessionExpired,
                notFoundState = TopicsUiState.NotFound,
                failedState = TopicsUiState.Failed
            )
        }
    }

    fun loadStackGroups() = viewModelScope.launch {
        _stackGroupsState.value = StackGroupsUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stackGroupsState.value = StackGroupsUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getInterviewStackGroups(
                auth = "Bearer ${session.accessToken}"
            )
        }.onSuccess { res ->
            _stackGroupsState.value = StackGroupsUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _stackGroupsState.value = it },
                unauthorizedState = StackGroupsUiState.SessionExpired,
                notFoundState = StackGroupsUiState.NotFound,
                failedState = StackGroupsUiState.Failed
            )
        }
    }

    fun loadStacks(stackGroup: String) = viewModelScope.launch {
        _stacksState.value = StacksUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _stacksState.value = StacksUiState.SessionExpired
            return@launch
        }

        runCatching {
            api.getInterviewStacks(
                auth = "Bearer ${session.accessToken}",
                stackGroup = stackGroup
            )
        }.onSuccess { res ->
            _stacksState.value = StacksUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _stacksState.value = it },
                unauthorizedState = StacksUiState.SessionExpired,
                notFoundState = StacksUiState.NotFound,
                failedState = StacksUiState.Failed
            )
        }
    }
    fun resetStates() {
        _topicsState.value = TopicsUiState.Idle
        _stackGroupsState.value = StackGroupsUiState.Idle
        _stacksState.value = StacksUiState.Idle
    }

}

@Suppress("UNCHECKED_CAST")
class AiSelectedVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AiSelectedVM(
            api = api,
            appContext = context.applicationContext
        ) as T
    }
}