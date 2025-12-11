package com.example.gravit.main.User.Notice

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.NoticeSummaryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoticeListVM(private val repo: NoticeRepository) : ViewModel() {

    data class UiState(
        val items: List<NoticeSummaryItem> = emptyList(),
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun loadAll() {
        val cur = _state.value
        if (cur.loading) return

        _state.value = UiState(loading = true)

        viewModelScope.launch {
            var page = 1
            var allItems: List<NoticeSummaryItem> = emptyList()
            var hasNext = true
            var totalPages = Int.MAX_VALUE

            val result = runCatching {
                while (hasNext && page <= totalPages) {
                    val res = repo.fetchSummaries(page).getOrThrow()

                    val list = res.content ?: emptyList()
                    allItems = allItems + list

                    hasNext = res.hasNext
                    totalPages = res.totalPages

                    Log.d(
                        "NoticeListVM",
                        "loadAll: serverPage=$page, got=${list.size}, hasNext=$hasNext, totalPages=$totalPages"
                    )

                    page += 1
                }
                allItems
            }

            result
                .onSuccess { items ->
                    _state.value = UiState(
                        items = items,
                        loading = false,
                        error = null
                    )
                }
                .onFailure { e ->
                    Log.e("NoticeListVM", "loadAll error: ${e.message}", e)
                    _state.value = UiState(
                        items = emptyList(),
                        loading = false,
                        error = e.message ?: "로드 실패"
                    )
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
