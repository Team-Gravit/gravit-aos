package com.inuappcenter.gravit.main.User.Setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.UpdateUserInfoRequest
import com.inuappcenter.gravit.api.UserInfoResponse
import com.inuappcenter.gravit.ui.theme.ProfilePalette
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

fun mapServerError(code: Int, raw: String?): String {
    if (raw.isNullOrBlank()) {
        return "요청 실패 ($code)"
    }

    return try {
        val json = JsonParser.parseString(raw).asJsonObject
        val message = json.get("message")

        when {
            message == null || message.isJsonNull -> {
                "요청 실패 ($code)"
            }

            message.isJsonArray -> {
                message.asJsonArray
                    .map { it.asString }
                    .joinToString("\n")
            }

            message.isJsonPrimitive -> {
                message.asString
            }

            else -> {
                "요청 실패 ($code)"
            }
        }
    } catch (e: Exception) {
        "요청 실패 ($code)"
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
