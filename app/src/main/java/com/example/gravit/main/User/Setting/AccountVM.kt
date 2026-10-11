package com.inuappcenter.gravit.main.User.Setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.UpdateUserInfoRequest
import com.inuappcenter.gravit.error.mapServerError
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val nickname: String = "",
        val profileId: Int = ProfilePalette.DEFAULT_ID,
        val errorMsg: String? = null,
        val savedOnce: Boolean = false,
        val sessionExpired: Boolean = false,
        val loadFailed: Boolean = false,
        val notFound: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun loadUserInfo() = viewModelScope.launch {
        _state.value = _state.value.copy(
            isLoading = true,
            errorMsg = null,
            loadFailed = false,
            sessionExpired = false,
            notFound = false
        )

        val session = AuthPrefs.load(appContext)

        if (session == null) {
            AuthPrefs.clear(appContext)
            _state.value = _state.value.copy(
                isLoading = false,
                sessionExpired = true
            )
            return@launch
        }

        runCatching {
            api.userInfo("Bearer ${session.accessToken}")
        }.onSuccess { res ->
            when {
                res.isSuccessful -> {
                    val body = res.body()

                    if (body != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            nickname = body.nickname,
                            profileId = body.profileImgNumber,
                            errorMsg = null
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            loadFailed = true
                        )
                    }
                }

                res.code() == 401 -> {
                    AuthPrefs.clear(appContext)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        sessionExpired = true
                    )
                }

                res.code() == 404 -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notFound = true
                    )
                }

                else -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loadFailed = true
                    )
                }
            }
        }.onFailure {
            _state.value = _state.value.copy(
                isLoading = false,
                loadFailed = true
            )
        }
    }
    fun clearError() {
        _state.value = _state.value.copy(
            errorMsg = null
        )
    }

    fun onNicknameChange(newNickname: String) {
        _state.value = _state.value.copy(
            nickname = newNickname,
            savedOnce = false,
            errorMsg = null
        )
    }

    fun onProfileChange(newId: Int) {
        _state.value = _state.value.copy(
            profileId = newId,
            savedOnce = false,
            errorMsg = null
        )
    }

    fun save(onSuccess: () -> Unit = {}) {
        val cur = _state.value

        viewModelScope.launch {
            _state.value = cur.copy(
                isSaving = true,
                errorMsg = null,
                savedOnce = false
            )

            val session = AuthPrefs.load(appContext)

            if (session == null) {
                AuthPrefs.clear(appContext)
                _state.value = _state.value.copy(
                    isSaving = false,
                    sessionExpired = true
                )
                return@launch
            }

            val body = UpdateUserInfoRequest(
                profilePhotoNumber = cur.profileId,
                nickname = cur.nickname.trim()
            )

            runCatching {
                api.updateUserInfo(
                    "Bearer ${session.accessToken}",
                    body
                )
            }.onSuccess { res ->
                when {
                    res.isSuccessful -> {
                        _state.value = _state.value.copy(
                            isSaving = false,
                            savedOnce = true
                        )
                        onSuccess()
                    }

                    res.code() == 401 -> {
                        AuthPrefs.clear(appContext)
                        _state.value = _state.value.copy(
                            isSaving = false,
                            sessionExpired = true
                        )
                    }

                    else -> {
                        _state.value = _state.value.copy(
                            isSaving = false,
                            errorMsg = mapServerError(
                                res.errorBody()?.string()
                            )
                        )
                    }
                }
            }.onFailure {
                _state.value = _state.value.copy(
                    isSaving = false,
                    errorMsg = "오류가 발생했습니다."
                )
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AccountVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountVM(
            api = api,
            appContext = context.applicationContext
        ) as T
    }
}