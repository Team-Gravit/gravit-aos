package com.example.gravit.main.User.Notice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.NoticeDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

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
            runCatching { repo.fetchDetail(id) }
                .onSuccess { detail ->
                    Log.d("NoticeDetailVM", "detail loaded: id=${detail.id}, title=${detail.title}")
                    _state.value = UiState(loading = false, item = detail)
                }
                .onFailure { e ->
                    Log.e("NoticeDetailVM", "detail error: ${e.message}", e)
                    _state.value = UiState(loading = false, error = e.message ?: "로드 실패")
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