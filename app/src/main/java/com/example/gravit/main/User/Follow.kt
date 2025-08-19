package com.example.gravit.main.User

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.FriendItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FollowViewModel(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel()  {

    sealed interface UiState{
        data object Loading : UiState
        data class Success(val data: List<FriendItem>) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state = _state.asStateFlow()

    private val _followerCount = MutableStateFlow(0)
    val followerCount = _followerCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount = _followingCount.asStateFlow()
    fun loadFollower() = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.getFollower(auth)
        }.onSuccess { res ->
            _followerCount.value = res.size
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

    fun loadFollowing() = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.getFollowing(auth)
        }.onSuccess { res ->
            _followingCount.value = res.size
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
class FollowVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FollowViewModel(api, context.applicationContext) as T
    }
}