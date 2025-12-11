package com.example.gravit.main.Study.Unit

import android.content.Context
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.UnitPageResponse
import com.example.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UnitListVM(
    private val api: ApiService,
    private val appContext: Context,
    private val chapterId: Int
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val data: UnitPageResponse) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading

            val session = AuthPrefs.load(appContext)
            if (session == null) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
                return@launch
            }

            runCatching {
                api.getUnitPage(
                    auth = "Bearer ${session.accessToken}",
                    chapterId = chapterId
                )
            }
                .onSuccess { data ->
                    _state.value = UiState.Success(data)
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

class UnitListVMFactory(
    private val api: ApiService,
    private val appContext: Context,
    private val chapterId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UnitListVM(api, appContext, chapterId) as T
    }
}