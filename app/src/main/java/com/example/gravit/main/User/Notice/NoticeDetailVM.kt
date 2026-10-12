package com.inuappcenter.gravit.main.User.Notice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.NoticeDetailResponse
import com.inuappcenter.gravit.api.RetrofitInstance.api
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoticeDetailVM(
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val item: NoticeDetailResponse) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(id: Long) {
        if (_state.value == UiState.Loading) return

        _state.value = UiState.Loading

        viewModelScope.launch {
            val session = AuthPrefs.load(appContext)

            if (session == null) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
                return@launch
            }

            runCatching {
                api.getNoticeDetail(
                    auth = "Bearer ${session.accessToken}",
                    noticeId = id
                )
            }.onSuccess { detail ->
                _state.value = UiState.Success(detail)
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

    fun resetErrorState() {
        if (
            _state.value == UiState.SessionExpired ||
            _state.value == UiState.NotFound
        ) {
            _state.value = UiState.Idle
        }
    }
}

@Suppress("UNCHECKED_CAST")
class NoticeDetailVMFactory(
    context: Context
) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoticeDetailVM(appContext) as T
    }
}