package com.example.gravit.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.OnboardingRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val api: ApiService, private val appContext: Context) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data object Success : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun submit(nickname: String, profileNumber: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading

            val session = AuthPrefs.load(appContext) //토큰 확인
            if (session == null || AuthPrefs.isExpired(session)) {
                AuthPrefs.clear(appContext) //토큰 만료거나 없으면 재로그인
                _state.value = UiState.SessionExpired
                return@launch
            }

            val authHeader = "Bearer ${session.accessToken}"
            runCatching {
                api.completeOnboarding(OnboardingRequest(nickname, profileNumber), authHeader)
            }.onSuccess {
                //성공이면 온보딩 true
                AuthPrefs.setOnboarded(appContext, true)
                _state.value = UiState.Success
            }.onFailure { e -> //401 -> 세션 만료 처리
                val is401 = (e as? retrofit2.HttpException)?.code() == 401
                if (is401) {
                    AuthPrefs.clear(appContext)
                    _state.value = UiState.SessionExpired
                } else {
                    _state.value = UiState.Failed
                }
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class OnboardingVMFactory(private val api: ApiService, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T { //Activity를 Application로 바꿔서 넘김
        return OnboardingViewModel(api, context.applicationContext) as T
    }
}