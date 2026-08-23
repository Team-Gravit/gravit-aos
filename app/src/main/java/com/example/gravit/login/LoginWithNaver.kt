package com.inuappcenter.gravit.login

import android.content.Context

import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback

fun loginWithNaver(
    context: Context,
    viewModel: LoginViewModel,
    onError: (String, String) -> Unit
) {

    val nidOAuthCallback = object : NidOAuthCallback {
        override fun onSuccess() {
            val accessToken = NidOAuth.getAccessToken()
            if (accessToken.isNullOrBlank()) {
                onError(
                    "TOKEN_UNAVAILABLE",
                    "Naver access token is unavailable"
                )
                return
            }
            viewModel.sendNaverToken(accessToken)
        }

        override fun onFailure(
            errorCode: String,
            errorDesc: String
        ) {
            onError(errorCode, errorDesc)
        }
    }

    NidOAuth.requestLogin(context, nidOAuthCallback)
}