package com.inuappcenter.gravit.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.OnboardingRequest
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
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

    fun submit(nickname: String, profileNumber: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading

            val session = AuthPrefs.load(appContext)
            if (session == null) {
                _state.value = UiState.SessionExpired
                return@launch
            }
            runCatching {
                api.completeOnboarding(OnboardingRequest(nickname, profileNumber), "Bearer ${session.accessToken}")
            }.onSuccess {
                //성공이면 온보딩 true
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
                _event.tryEmit(Event.ShowFailedSnack)
            }
        }
    }

    sealed interface Event {
        data object ShowFailedSnack : Event
    }

    private val _event = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()


    fun testFailed() {
        _state.value = UiState.Failed
        _event.tryEmit(Event.ShowFailedSnack)
    }

}

@Suppress("UNCHECKED_CAST")
class OnboardingVMFactory(private val api: ApiService, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { //Activity를 Application로 바꿔서 넘김
        return OnboardingViewModel(api, context.applicationContext) as T
    }
}