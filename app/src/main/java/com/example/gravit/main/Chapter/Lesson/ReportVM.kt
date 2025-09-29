package com.example.gravit.main.Chapter.Lesson

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.ReportRequest
import com.example.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data object Success : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun submit(reportType: String, content: String, problemId: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading

            val session = AuthPrefs.load(appContext)
            if (session == null || AuthPrefs.isExpired(session)) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
                return@launch
            }

            val auth = "Bearer ${session.accessToken}"
            runCatching {
                api.sendReport(ReportRequest(reportType, content, problemId), auth)
            }.onSuccess {
                AuthPrefs.setOnboarded(appContext, true)
                _state.value = UiState.Success
            }.onFailure { e ->
                handleApiFailure(
                    e = e,
                    appContext = appContext,
                    onStateChange = { _state.value = it },
                    unauthorizedState = UiState.SessionExpired,
                    notFoundState = UiState.NotFound,
                    failedState = UiState.Failed
                )
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class ReportVMFactory(private val api: ApiService, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReportVM(api, context.applicationContext) as T
    }
}
