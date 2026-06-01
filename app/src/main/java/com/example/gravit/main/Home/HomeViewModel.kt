package com.inuappcenter.gravit.main.Home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.MainPageResponse
import com.inuappcenter.gravit.api.UnitDetailResponses
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val data: MainPageResponse,
            val units: List<UnitDetailResponses> = emptyList()
        ) : UiState

        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"

        runCatching {
            val mainData = api.getMainPage(auth)

            val recentChapterId = mainData.learningDetailResponse.recentSolvedChapterId

            val units =
                if (recentChapterId != 0) {
                    api.getUnitPage(
                        auth = auth,
                        chapterId = recentChapterId
                    ).unitDetailResponses
                } else {
                    emptyList()
                }

            UiState.Success(
                data = mainData,
                units = units
            )
        }.onSuccess { successState ->
            _state.value = successState

        }.onFailure { e ->
            Log.e(
                "HomeVM",
                "load failed: ${e::class.simpleName}, ${e.message}",
                e
            )

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

@Suppress("UNCHECKED_CAST")
class HomeVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(api, context.applicationContext) as T
    }
}