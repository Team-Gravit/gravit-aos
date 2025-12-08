package com.example.gravit.api

import android.content.Context
import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Response
import okhttp3.Route

interface TokenProvider {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveTokens(accessToken: String, refreshToken: String)
    fun clear()
}

class AuthPrefsTokenProvider(
    private val context: Context
) : TokenProvider {

    override fun getAccessToken(): String? =
        AuthPrefs.load(context)?.accessToken

    override fun getRefreshToken(): String? =
        AuthPrefs.load(context)?.refreshToken

    override fun saveTokens(accessToken: String, refreshToken: String) {
        val current = AuthPrefs.load(context)
        val isOnboarded = current?.isOnboarded ?: false
        AuthPrefs.save(context, accessToken, refreshToken, isOnboarded)
    }

    override fun clear() {
        AuthPrefs.clear(context)
    }
}


class TokenAuthenticator(
    private val context: Context,
    private val tokenProvider: TokenProvider,
    private val refreshApi: ApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): okhttp3.Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = tokenProvider.getRefreshToken() ?: return null

        val newAccessToken = runBlocking {
            try {
                val res = refreshApi.sendRefreshToken(
                    RefreshTokenRequest(refreshToken)
                )
                tokenProvider.saveTokens(res.accessToken, refreshToken)
                res.accessToken
            } catch (e: Exception) {
                Log.e("TokenAuthenticator", "refresh failed", e)
                null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}