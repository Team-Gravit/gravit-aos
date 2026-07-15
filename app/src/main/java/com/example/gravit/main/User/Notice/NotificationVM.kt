package com.example.gravit.main.User.Notice

import android.content.Context
import android.system.Os.access
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.Notifications
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState{
        data object Idle: UiState
        data object Loading : UiState
        data class Success(val data: List<Notifications>) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.value = UiState.Loading
        val session = AuthPrefs.load(appContext)
        if(session == null){
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }
        runCatching {
            api.getNotifications("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            _state.value = UiState.Success(res)
        }.onFailure{ e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = {_state.value = it },
                unauthorizedState = UiState.SessionExpired,
                notFoundState = UiState.NotFound,
                failedState = UiState.Failed
            )
        }
    }

    sealed interface ActionUiState{
        data object Idle: ActionUiState
        data object Loading : ActionUiState
        data object Success : ActionUiState
        data class Failed(val message: String) : ActionUiState
        data object SessionExpired : ActionUiState
        data object NotFound : ActionUiState
    }

    data class ErrorResponse(
        val error: String,
        val message: String
    )

    private val _stateAction = MutableStateFlow<ActionUiState>(ActionUiState.Idle)
    val stateAction = _stateAction.asStateFlow()

    fun toggleFollow(targetId: Long, actionType: String) = viewModelScope.launch {
        val session = AuthPrefs.load(appContext)
        if(session == null){
            AuthPrefs.clear(appContext)
            _stateAction.value = ActionUiState.SessionExpired
            return@launch
        }
        runCatching {
            if(actionType=="FOLLOW_BACK")
                api.follow("Bearer ${session.accessToken}", targetId)
            else
                api.unfollow("Bearer ${session.accessToken}", targetId)
        }.onSuccess { res ->
            val message = runCatching {
                res.errorBody()?.string()
                    ?.let { Gson().fromJson(it, ErrorResponse::class.java).message }
            }.getOrNull()
            when {
                res.isSuccessful -> {
                    _stateAction.value = ActionUiState.Success
                }
                res.code() == 401 -> {
                    AuthPrefs.clear(appContext)
                    _stateAction.value = ActionUiState.SessionExpired
                }

                res.code() == 400 -> {
                    _stateAction.value = ActionUiState.Failed(
                        message ?: "자기 자신에게 팔로우는 불가능합니다."
                    )
                }

                res.code() == 404 -> {
                    _stateAction.value = ActionUiState.Failed(
                        message ?: "팔로우 내역이 존재하지 않습니다."
                    )
                }

                res.code() == 409 -> {
                    _stateAction.value = ActionUiState.Failed(
                        message ?: "이미 팔로잉을 한 유저입니다."
                    )
                }

                else -> {
                    _stateAction.value = ActionUiState.Failed("오류가 발생했습니다.")
                }
            }
        }.onFailure {
            _stateAction.value = ActionUiState.Failed("오류가 발생했습니다.")
        }
    }

}

@Suppress("UNCHECKED_CAST")
class NotificationVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationVM(api, context.applicationContext) as T
    }
}