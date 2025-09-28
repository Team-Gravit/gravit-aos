package com.example.gravit.main.User

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.NoticeSummaryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class NoticeListVM(private val repo: NoticeRepository) : ViewModel() {

    data class UiState(
        val items: List<NoticeSummaryItem> = emptyList(),
        val page: Int = 1,
        val hasNext: Boolean = true,
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun loadFirst() {
        if (_state.value.loading) return
        _state.value = UiState(loading = true)
        viewModelScope.launch {
            runCatching { repo.fetchSummaries(1) }
                .onSuccess { res ->
                    val list = res.content ?: emptyList()
                    Log.d("NoticeListVM", "loadFirst: size=${list.size}, hasNext=${res.hasNext}")
                    _state.value = _state.value.copy(
                        items = list,
                        page = 1,
                        hasNext = res.hasNext,
                        loading = false,
                        error = null
                    )
                }
                .onFailure { e ->
                    Log.e("NoticeListVM", "loadFirst error: ${e.message}", e)
                    _state.value = _state.value.copy(
                        loading = false,
                        error = e.message ?: "로드 실패"
                    )
                }
        }
    }

    fun loadNext() {
        val cur = _state.value
        if (cur.loading || !cur.hasNext) return
        _state.value = cur.copy(loading = true)
        viewModelScope.launch {
            runCatching {
                repo.fetchSummaries(1)
            }.onSuccess { res ->
                _state.value = _state.value.copy(
                    items = res.content,
                    page = 1,
                    hasNext = res.hasNext,
                    loading = false,
                    error = null
                )
                android.util.Log.d("NoticeListVM", "loadFirst: size=${res.content.size}, hasNext=${res.hasNext}")
            }.onFailure { e ->
                _state.value = _state.value.copy(loading = false, error = e.message ?: "로드 실패")
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class NoticeListVMFactory(context: Context) : ViewModelProvider.Factory {
    private val repo = NoticeRepository(context.applicationContext)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoticeListVM(repo) as T
    }
}
