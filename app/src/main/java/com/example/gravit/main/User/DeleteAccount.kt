package com.example.gravit.main.User

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.AuthPrefs
import kotlinx.coroutines.launch

class LogoutViewModel(
    private val appContext: Context
): ViewModel() {

    fun logout(onDone: () -> Unit = {}) {
        viewModelScope.launch() {
            //로컬 토큰 삭제
            AuthPrefs.clear(appContext)
            onDone()
        }
    }
}

@Suppress("UNCHECKED_CAST")
class LogoutVMFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LogoutViewModel(context.applicationContext) as T
    }
}