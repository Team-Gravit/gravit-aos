package com.example.gravit.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gravit.login.LoginScreen
import com.example.gravit.login.ProfileFinish
import com.example.gravit.login.ProfileSetting
import com.example.gravit.main.MainScreen
import com.example.gravit.splash.SplashScreen

@Composable
fun AppNavigation() {
    val rootnavController = rememberNavController()

    NavHost(navController = rootnavController, startDestination = "main") {
        composable("splash") { SplashScreen(rootnavController) }
        composable("login choice") { LoginScreen(rootnavController) }
        composable("profile setting") { ProfileSetting(rootnavController) }
        composable("profile finish") { ProfileFinish(rootnavController) }
        composable("main") { MainScreen(rootnavController) }
    }
}