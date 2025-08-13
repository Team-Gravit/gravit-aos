package com.example.gravit.login

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.CustomTabsOptions
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials



fun loginWithAuth0(
    context: Context,
    connection: String,
    onSuccess: (String) -> Unit
) {
    val activity = context as ComponentActivity
    val auth0 = Auth0.getInstance("fq1pF2twsWNYG0JORVHFuJNzteGPtyTe", "dev-fl5wpn5srn5xay26.us.auth0.com")

    val ctOptions = CustomTabsOptions.newBuilder()
        .showTitle(true)
        .build()

    WebAuthProvider.login(auth0)
        .withCustomTabsOptions(ctOptions)
        .withScheme("gravit")
        .withConnection(connection)
        .withScope("openid profile email")
        .withParameters(
            buildMap {
                when (connection.lowercase()) {
                "naver" -> put("connection_scope", "name email nickname")
                "kakao" -> put("connection_scope", "profile_nickname account_email")
                }
            }
        )
        .start(activity, object : Callback<Credentials, AuthenticationException> {
            override fun onSuccess(result: Credentials) {

                val idToken = result.idToken
                if (idToken.isBlank()) {
                    Log.e("Auth0", "No idToken")
                    return
                }

                // 로그
                Log.d("Auth0", "idToken = ${maskToken(idToken)}")
                logJwtIfJwt("Auth0.idToken.payload", idToken)


                onSuccess(idToken) //다음 단계로 전달 (LoginActivity)
            }
            override fun onFailure(error: AuthenticationException) {
                Log.e("Auth0", "Login failed: ${error.getDescription()}")
            }
        })

}

