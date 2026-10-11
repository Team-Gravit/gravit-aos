package com.inuappcenter.gravit.main.User.Notice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.NoticeDetailResponse
import com.inuappcenter.gravit.api.RetrofitInstance.api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoticeDetailVM(
    private val appContext: Context
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val item: NoticeDetailResponse? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun load(id: Long) {
        _state.value = UiState(loading = true)

        viewModelScope.launch {
            val session = AuthPrefs.load(appContext)

            if (session == null) {
                AuthPrefs.clear(appContext)
                _state.value = UiState(
                    error = "오류가 발생했습니다."
                )
                return@launch
            }

            runCatching {
                api.getNoticeDetail(auth = "Bearer ${session.accessToken}", noticeId = id)
            }.onSuccess { detail ->
                _state.value = UiState(
                    item = detail
                )
            }.onFailure {
                _state.value = UiState(
                    error = "오류가 발생했습니다."
                )
            }
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