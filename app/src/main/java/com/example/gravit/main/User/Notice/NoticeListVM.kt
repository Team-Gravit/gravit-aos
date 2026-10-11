package com.inuappcenter.gravit.main.User.Notice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.NoticeSummaryItem
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoticeListVM(
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val items: List<NoticeSummaryItem>) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun loadAll() {
        if (_state.value == UiState.Loading) return

        _state.value = UiState.Loading

        viewModelScope.launch {
            val session = AuthPrefs.load(appContext)

            if (session == null) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
                return@launch
            }

            val result = runCatching {
                var page = 1
                var hasNext = true
                var totalPages = Int.MAX_VALUE
                val allItems = mutableListOf<NoticeSummaryItem>()

                while (hasNext && page <= totalPages) {
                    val response = RetrofitInstance.api.getNoticeSummaries(
                        auth = "Bearer ${session.accessToken}",
                        page = page
                    )

                    allItems += response.content.orEmpty()

                    hasNext = response.hasNext
                    totalPages = response.totalPages
                    page++
                }

                allItems.toList()
            }

            result
                .onSuccess { items ->
                    _state.value = UiState.Success(items)
                }
                .onFailure { e ->
                    handleApiFailure(
                        e = e,
                        appContext = appContext,
                        onStateChange = { _state.value = it },
                        unauthorizedState = UiState.SessionExpired,
                        notFoundState = UiState.NotFound,
                        failedState = UiState.Failed
                    )
                }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class NoticeListVMFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoticeListVM(appContext) as T
    }
}