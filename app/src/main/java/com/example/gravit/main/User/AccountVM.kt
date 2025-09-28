package com.example.gravit.main.User.Setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.UpdateUserInfoRequest
import com.example.gravit.api.UserInfoResponse
import com.example.gravit.ui.theme.ProfilePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

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
        val savedOnce: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private fun bearerOrNull(): String? {
        val token = AuthPrefs.load(appContext)?.accessToken ?: return null
        return "Bearer $token"
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMsg = null)
            try {
                val bearer = bearerOrNull() ?: run {
                    _state.value = _state.value.copy(isLoading = false, errorMsg = "세션 만료")
                    return@launch
                }
                val res: Response<UserInfoResponse> = api.userInfo(bearer)
                if (res.isSuccessful) {
                    val body = res.body()
                    if (body != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            nickname = body.nickname,
                            profileId = body.profileImgNumber,
                            errorMsg = null
                        )
                    } else {
                        _state.value = _state.value.copy(isLoading = false, errorMsg = "응답 바디가 비었습니다.")
                    }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMsg = mapServerError(res.code(), res.errorBody()?.string())
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMsg = e.message ?: "알 수 없는 오류")
            }
        }
    }

    fun onNicknameChange(newNickname: String) {
        _state.value = _state.value.copy(nickname = newNickname, savedOnce = false, errorMsg = null)
    }

    fun onProfileChange(newId: Int) {
        _state.value = _state.value.copy(profileId = newId, savedOnce = false, errorMsg = null)
    }


    fun save(onSuccess: () -> Unit = {}) {
        val cur = _state.value
        viewModelScope.launch {
            _state.value = cur.copy(isSaving = true, errorMsg = null, savedOnce = false)
            try {
                val bearer = bearerOrNull() ?: run {
                    _state.value = _state.value.copy(isSaving = false, errorMsg = "세션 만료")
                    return@launch
                }

                val body = UpdateUserInfoRequest(
                    profilePhotoNumber = cur.profileId,
                    nickname = cur.nickname.trim()
                )

                val res = api.updateUserInfo(bearer, body)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(isSaving = false, savedOnce = true)
                    onSuccess()
                } else {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMsg = mapServerError(res.code(), res.errorBody()?.string())
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, errorMsg = e.message ?: "알 수 없는 오류")
            }
        }
    }
}

private fun mapServerError(code: Int, raw: String?): String {
    val text = raw.orEmpty()
    return when {
        "USER_4041" in text -> "존재하지 않는 유저입니다."
        "GLOBAL_4001" in text -> "유효성 검사에 실패했습니다."
        "GLOBAL_5001" in text -> "서버 오류가 발생했습니다."
        else -> "요청 실패 ($code)"
    }
}

@Suppress("UNCHECKED_CAST")
class AccountVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountVM(api, context.applicationContext) as T
    }
}
