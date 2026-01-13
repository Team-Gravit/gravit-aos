package com.inuappcenter.gravit.login

import android.content.Context
import android.util.Log
import com.inuappcenter.gravit.api.NaverUserInfo
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import com.navercorp.nid.profile.domain.vo.NidProfileMap
import com.navercorp.nid.profile.util.NidProfileCallback

private const val NAVER_TAG = "NaverLogin"

fun loginWithNaver(
    context: Context,
    viewModel: LoginViewModel,
    onError: (Throwable) -> Unit = {}
) {
    val callback: NidOAuthCallback = object : NidOAuthCallback {

        override fun onSuccess() {
            NidOAuth.getUserProfileMap(object : NidProfileCallback<NidProfileMap> {
                override fun onSuccess(result: NidProfileMap) {
                    val map = result.profile

                    val providerId = map["id"] as? String ?: ""
                    val email = map["email"] as? String ?: ""
                    val nickname = map["nickname"] as? String ?: ""

                    val dto = NaverUserInfo(
                        email = email,
                        providerId = providerId,
                        nickname = nickname
                    )
                    viewModel.sendNaverInfo(dto)
                }

                override fun onFailure(errorCode: String, errorDesc: String) {
                    Log.e("Naver", "profile error: $errorCode, $errorDesc")
                }
            })
        }
        override fun onFailure(errorCode: String, errorDesc: String) {
            val e = RuntimeException("네이버 로그인 실패($errorCode): $errorDesc")
            Log.e(NAVER_TAG, "로그인 실패: $errorCode / $errorDesc", e)
            onError(e)
        }
    }

    NidOAuth.requestLogin(context, callback)
}