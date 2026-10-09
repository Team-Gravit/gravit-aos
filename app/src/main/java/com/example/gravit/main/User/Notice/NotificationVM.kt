package com.gravit.main.User.Notice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.Notifications
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    sealed interface FollowEvent {
        data class Success(val targetId: Long) : FollowEvent
        data class Failed(
            val targetId: Long,
            val message: String
        ) : FollowEvent

        data object SessionExpired : FollowEvent
    }

    private val _followEvent = MutableSharedFlow<FollowEvent>()
    val followEvent = _followEvent.asSharedFlow()

    private val _followingRequestIds = MutableStateFlow<Set<Long>>(emptySet())
    val followingRequestIds = _followingRequestIds.asStateFlow()
    data class ErrorResponse(
        val error: String,
        val message: String
    )

    fun toggleFollow(targetId: Long, actionType: String) = viewModelScope.launch {
        if (targetId in _followingRequestIds.value) return@launch

        _followingRequestIds.update { it + targetId }

        try {
            val session = AuthPrefs.load(appContext)

            if (session == null) {
                AuthPrefs.clear(appContext)
                _followEvent.emit(FollowEvent.SessionExpired)
                return@launch
            }

            runCatching {
                if (actionType == "FOLLOW_BACK") {
                    api.follow("Bearer ${session.accessToken}", targetId)
                } else {
                    api.unfollow("Bearer ${session.accessToken}", targetId)
                }
            }.onSuccess { res ->
                val message = runCatching {
                    res.errorBody()?.string()
                        ?.let {
                            Gson().fromJson(it, ErrorResponse::class.java).message
                        }
                }.getOrNull()

                when {
                    res.isSuccessful -> {
                        _followEvent.emit(FollowEvent.Success(targetId))
                        load()
                    }

                    res.code() == 401 -> {
                        AuthPrefs.clear(appContext)
                        _followEvent.emit(FollowEvent.SessionExpired)
                    }

                    res.code() == 400 -> {
                        _followEvent.emit(
                            FollowEvent.Failed(
                                targetId,
                                message ?: "자기 자신에게 팔로우는 불가능합니다."
                            )
                        )
                    }

                    res.code() == 404 -> {
                        _followEvent.emit(
                            FollowEvent.Failed(
                                targetId,
                                message ?: "팔로우 내역이 존재하지 않습니다."
                            )
                        )
                    }

                    res.code() == 409 -> {
                        _followEvent.emit(
                            FollowEvent.Failed(
                                targetId,
                                message ?: "이미 팔로잉을 한 유저입니다."
                            )
                        )
                    }

                    else -> {
                        _followEvent.emit(
                            FollowEvent.Failed(
                                targetId,
                                "오류가 발생했습니다."
                            )
                        )
                    }
                }
            }.onFailure {
                _followEvent.emit(
                    FollowEvent.Failed(
                        targetId,
                        "오류가 발생했습니다."
                    )
                )
            }
        } finally {
            _followingRequestIds.update { it - targetId }
        }
    }
    fun markCongratulated(targetId: Long) {
        val currentState = _state.value as? UiState.Success ?: return

        _state.value = UiState.Success(
            currentState.data.map { notification ->
                if (
                    notification.actionType == "CONGRATULATE" &&
                    notification.targetId == targetId
                ) {
                    notification.copy(congratulated = true)
                } else {
                    notification
                }
            }
        )
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