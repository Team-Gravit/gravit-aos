package com.inuappcenter.gravit.main.User.Notice

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.NoticeDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoticeDetailVM(private val repo: NoticeRepository): ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val item: NoticeDetailResponse? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun load(id: Long) {
        _state.value = UiState(loading = true)

        viewModelScope.launch {
            repo.fetchDetail(id)
                .onSuccess { detail ->
                    Log.d(
                        "NoticeDetailVM",
                        "detail loaded: id=${detail.id}, title=${detail.title}"
                    )
                    _state.value = UiState(
                        loading = false,
                        item = detail,
                        error = null
                    )
                }
                .onFailure { e ->
                    Log.e("NoticeDetailVM", "detail error: ${e.message}", e)
                    _state.value = UiState(
                        loading = false,
                        item = null,
                        error = e.message ?: "로드 실패"
                    )
                }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class NoticeDetailVMFactory(context: Context) : ViewModelProvider.Factory {
    private val repo = NoticeRepository(context.applicationContext)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoticeDetailVM(repo) as T
    }
}
