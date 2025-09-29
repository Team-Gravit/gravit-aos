package com.example.gravit.main.User.Friend

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.FriendItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FollowListVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    enum class Tab { Followers, Following }

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val data: List<FriendItem>) : UiState
        data class Failed(val message: String? = null) : UiState
        data object SessionExpired : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state = _state.asStateFlow()

    private val _followerCount = MutableStateFlow(0)
    val followerCount = _followerCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount = _followingCount.asStateFlow()

    fun loadFollower(page: Int = 0) = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.getFollower(auth = auth, page = page)
        }.onSuccess { res ->
            val list = res.contents
            _followerCount.value = list.size
            _state.value = UiState.Success(list)
        }.onFailure { e ->
            val code = (e as? HttpException)?.code()
            if (code == 401) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
            } else {
                _state.value = UiState.Failed(e.message)
            }
        }
    }

    fun loadFollowing(page: Int = 0) = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.getFollowing(auth = auth, page = page)
        }.onSuccess { res ->
            val list = res.contents
            _followingCount.value = list.size
            _state.value = UiState.Success(list)
        }.onFailure { e ->
            val code = (e as? HttpException)?.code()
            if (code == 401) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
            } else {
                _state.value = UiState.Failed(e.message)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FollowListVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FollowListVM(api, context.applicationContext) as T
    }
}
