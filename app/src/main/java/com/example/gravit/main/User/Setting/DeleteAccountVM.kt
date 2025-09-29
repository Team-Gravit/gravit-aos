package com.example.gravit.main.User.Setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class DeleteAccountVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    data class UiState(
        val requesting: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private fun bearerOrNull(): String? =
        AuthPrefs.load(appContext)?.accessToken?.let { "Bearer $it" }

    /**
     * 메일 전송 요청 (202 Accepted 기대)
     */
    fun requestDeletionMail(dest: String = "ANDROID", onAccepted: () -> Unit) {
        val token = bearerOrNull()
        if (token == null) {
            _state.value = UiState(requesting = false, error = "세션이 만료되었습니다.")
            return
        }

        viewModelScope.launch {
            _state.value = UiState(requesting = true, error = null)
            try {
                val res: Response<Unit> = api.requestDeletionMail(token, dest)
                if (res.code() == 202) {
                    _state.value = UiState(requesting = false, error = null)
                    onAccepted()
                } else {
                    val raw = res.errorBody()?.string().orEmpty()
                    val msg = when {
                        "USER_4041" in raw -> "존재하지 않는 유저입니다."
                        "MAIL_4002" in raw -> "메일 전송에 실패했습니다."
                        "GLOBAL_5001" in raw -> "예기치 못한 오류가 발생했습니다."
                        res.code() == 401 -> "세션이 만료되었습니다."
                        else -> "요청 실패 (${res.code()})"
                    }
                    _state.value = UiState(requesting = false, error = msg)
                }
            } catch (e: Exception) {
                _state.value = UiState(requesting = false, error = e.message ?: "네트워크 오류")
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class DeleteAccountVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeleteAccountVM(api, context.applicationContext) as T
    }
}
