package com.example.gravit.main.User.Setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.main.Home.HomeViewModel.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class DeleteAccountVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    private val prefs = appContext.getSharedPreferences("acct_del", Context.MODE_PRIVATE)
    private fun isPending(): Boolean = prefs.getBoolean("pending", false)
    private fun markPending() { prefs.edit().putBoolean("pending", true).apply() }
    private fun clearPending() { prefs.edit().putBoolean("pending", false).apply() }

    // 2) sealed class 올바른 선언
    sealed class DeletionState {
        data object Idle : DeletionState()
        data object Loading : DeletionState()
        data object Pending : DeletionState()
        data object Confirmed : DeletionState()
        data object SessionExpired : DeletionState()
    }

    private val _state = MutableStateFlow<DeletionState>(
        if (isPending()) DeletionState.Pending else DeletionState.Idle
    )
    val state = _state.asStateFlow()

    fun requestDeletionMail(dest: String, onDone: () -> Unit) = viewModelScope.launch {
        _state.value = DeletionState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = DeletionState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"

        val resp = runCatching { api.requestDeletionMail(auth, dest) }
            .getOrElse { e ->
                val code = (e as? retrofit2.HttpException)?.code()
                if (code == 401) {
                    AuthPrefs.clear(appContext)
                    _state.value = DeletionState.SessionExpired
                } else {
                    _state.value = DeletionState.Idle
                }
                return@launch
            }

        // 202 Accepted일 때만 pending 처리 + 콜백 호출
        if (resp.isSuccessful && resp.code() == 202) {
            markPending()
            _state.value = DeletionState.Pending
            onDone()
        } else {
            if (resp.code() == 401) {
                AuthPrefs.clear(appContext)
                _state.value = DeletionState.SessionExpired
            } else {
                _state.value = DeletionState.Idle
            }
        }
    }

    fun checkIfDeleted() = viewModelScope.launch {
        if (!isPending()) return@launch
        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext); _state.value = DeletionState.SessionExpired; return@launch
        }

        runCatching { api.getUser("Bearer ${session.accessToken}") }
            .onSuccess { _state.value = DeletionState.Pending } // 아직 존재
            .onFailure { e ->
                val http = e as? retrofit2.HttpException
                val code = http?.code()
                val body = http?.response()?.errorBody()?.string().orEmpty()
                val deleted = (code == 404 && body.contains("\"error\":\"USER_4041\"")) || code == 410
                when {
                    deleted -> { AuthPrefs.clear(appContext); clearPending(); _state.value = DeletionState.Confirmed }
                    code == 401 -> { AuthPrefs.clear(appContext); _state.value = DeletionState.SessionExpired }
                    else -> _state.value = DeletionState.Pending
                }
            }
    }
}

@Suppress("UNCHECKED_CAST")
class DeleteAccountVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeleteAccountVM(api, context.applicationContext) as T
    }
}
