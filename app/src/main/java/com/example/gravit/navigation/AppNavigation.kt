package com.example.gravit.navigation
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gravit.login.LoginScreen
import com.example.gravit.login.LoginViewModel
import com.example.gravit.login.ProfileFinish
import com.example.gravit.login.ProfileSetting
import com.example.gravit.main.MainScreen
import com.example.gravit.splash.SplashScreen

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