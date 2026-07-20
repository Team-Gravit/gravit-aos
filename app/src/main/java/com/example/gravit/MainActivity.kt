package com.inuappcenter.gravit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.auth0.android.provider.WebAuthProvider
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.fcm.FCMVMFactory
import com.inuappcenter.gravit.fcm.FcmViewModel
import com.inuappcenter.gravit.login.maskToken
import com.inuappcenter.gravit.navigation.AppNavigation
import com.inuappcenter.gravit.ui.theme.GravitTheme
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NidOAuth

class MainActivity : ComponentActivity() {
    private val fcmViewModel: FcmViewModel by viewModels {
        FCMVMFactory(
            api = RetrofitInstance.api,
            context = applicationContext
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("NaverInit", "CLIENT_ID=${BuildConfig.OAUTH_CLIENT_ID}")
        Log.d("NaverInit", "CLIENT_SECRET=${maskToken(BuildConfig.OAUTH_CLIENT_SECRET)}")
        Log.d("NaverInit", "CLIENT_NAME=${BuildConfig.OAUTH_CLIENT_NAME}")

        WebAuthProvider.resume(intent)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        RetrofitInstance.init(applicationContext)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        NidOAuth.setLogEnabled(true)
        NidOAuth.initialize(
            context = applicationContext,
            clientId = BuildConfig.OAUTH_CLIENT_ID,
            clientSecret = BuildConfig.OAUTH_CLIENT_SECRET,
            clientName = BuildConfig.OAUTH_CLIENT_NAME,
        )
        setContent {
            GravitTheme {
                AppNavigation(
                    fcmViewModel = fcmViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        WebAuthProvider.resume(intent)
    }
}