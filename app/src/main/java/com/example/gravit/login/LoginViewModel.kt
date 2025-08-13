package com.example.gravit.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.AuthTokenResponse
import com.example.gravit.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Base64
import com.example.gravit.api.IdTokenRequest
import com.example.gravit.api.RetrofitInstance


fun maskToken(t: String?): String =
    if (t.isNullOrBlank()) "null" else "${t.take(6)}...${t.takeLast(6)} (len=${t.length})"

fun logJwtIfJwt(tag: String, token: String?) {
    if (token.isNullOrBlank()) return
    val parts = token.split(".")
    if (parts.size >= 2) {
        try {
            val payload = Base64.decode(parts[1],
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            Log.d(tag, "payload=${String(payload)}")
        } catch (e: Exception) {
            Log.d(tag, "not a JWT (decode failed)")
        }
    } else {
        Log.d(tag, "not a JWT (opaque token)")
    }
}

class LoginViewModel : ViewModel() {

    private val api: ApiService = RetrofitInstance.api

    private val _jwtToken = MutableStateFlow<AuthTokenResponse?>(null)
    val jwtToken: StateFlow<AuthTokenResponse?> = _jwtToken

    fun sendIdTokenToServer(idToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("AuthFlow", "POST /api/v1/oauth/ = ${maskToken(idToken)}")
            logJwtIfJwt("Auth0.idToken.payload", idToken)

            runCatching {
                api.sendCode(IdTokenRequest(idToken))

            }.onSuccess { res ->
                Log.d("AuthFlow", "Server access = ${maskToken(res.accessToken)}")
                Log.d("AuthFlow", "isOnboarded = ${res.isOnboarded}")
                _jwtToken.value = res //응답 저장
            }.onFailure { e ->
                Log.e("LoginViewModel", "sendAccessToken failed", e)
                _jwtToken.value = null
            }
        }
    }
}
