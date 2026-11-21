package com.inuappcenter.gravit.login

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient


private const val TAG = "KakaoLogin"

fun loginWithKakao(
    context: Context,
    connection: String,
    onSuccess: (String) -> Unit,
    onError: (Throwable) -> Unit = {}
) {
    fun handleToken(token: OAuthToken) {
        val tokenForServer = token.idToken ?: token.accessToken

        Log.i(TAG, "카카오 로그인 성공 tokenForServer=${tokenForServer.take(10)}...")

        if (tokenForServer.isNullOrBlank()) {
            val e = IllegalStateException("카카오 토큰이 비어 있음")
            Log.e(TAG, "토큰 비어 있음", e)
            onError(e)
            return
        }

        onSuccess(tokenForServer)
    }

    val accountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
            onError(error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            handleToken(token)
        }
    }

    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡으로 로그인 실패", error)

                // 사용자가 카카오톡 권한 화면 등에서 취소한 경우 → 그냥 취소로 처리
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onError(error)
                    return@loginWithKakaoTalk
                }

                // 그 외 에러면 카카오계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(context, callback = accountCallback)
            } else if (token != null) {
                Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                handleToken(token)
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(context, callback = accountCallback)
    }
}