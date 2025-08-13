package com.example.gravit.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gravit.login.LoginScreen
import com.example.gravit.login.ProfileFinish
import com.example.gravit.login.ProfileSetting
import com.example.gravit.main.MainScreen
import com.example.gravit.main.Study.ShortAnswer
import com.example.gravit.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("splash") { SplashScreen(navController) }
        composable("login choice") { LoginScreen(navController) }
        composable("profile setting") { ProfileSetting(navController) }
        composable("profile finish") { ProfileFinish(navController) }
        composable("main") { MainScreen() }
        composable("short") {ShortAnswer()}
    }
}