package com.example.gravit.main.Chapter.Unit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.UnitPageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UnitViewModel(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState{
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val data: List<UnitPageResponse>) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(chapterId: Int) = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.getUnitPage(auth, chapterId)
        }.onSuccess { res ->
            _state.value = UiState.Success(res)
        }.onFailure { e ->
            val code = (e as? retrofit2.HttpException)?.code()
            if (code == 401) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
            } else {
                _state.value = UiState.Failed
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class UnitVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UnitViewModel(api, context.applicationContext) as T
    }
}