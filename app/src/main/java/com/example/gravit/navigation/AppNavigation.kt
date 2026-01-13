package com.inuappcenter.gravit.navigation
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.login.LoginScreen
import com.inuappcenter.gravit.login.LoginViewModel
import com.inuappcenter.gravit.login.ProfileFinish
import com.inuappcenter.gravit.login.ProfileSetting
import com.inuappcenter.gravit.splash.SplashScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val rootnavController = rememberNavController()

    NavHost(navController = rootnavController, startDestination = "splash") {

        composable("splash") { SplashScreen(rootnavController) }
        composable("login choice") {
            val loginViewModel = viewModel<LoginViewModel>()
            LoginScreen(rootnavController, loginViewModel)
        }

        composable("profile setting") { ProfileSetting(rootnavController) }
        composable("profile finish") { ProfileFinish(rootnavController) }
        composable("main") { MainScreen(rootnavController) }
    }
}