package com.example.gravit.main.User

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.Badges
import com.example.gravit.api.UserPageResponse
import com.example.gravit.error.handleApiFailure
import com.example.gravit.main.Home.HomeViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserScreenVM (
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val data: User) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }
    data class User(
        val user: UserPageResponse,
        val badges: Badges
    )

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching {
            coroutineScope {
                val u = async { api.getUser(auth) }
                val b = async { api.getBadges(auth) }
                User(u.await(), b.await())
            }
        }.onSuccess { res ->
            _state.value = UiState.Success(res)
        }.onFailure { e ->
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
class UserVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserScreenVM(api, context.applicationContext) as T
    }
}