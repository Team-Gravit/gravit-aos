package com.example.gravit.main

import BottomNavigationBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gravit.main.Home.HomeScreen
import com.example.gravit.main.Study.StudyScreen
import com.example.gravit.main.League.LeagueScreen
import com.example.gravit.main.User.UserScreen

@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(bottomNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("study") { StudyScreen() }
            composable("league") { LeagueScreen() }
            composable("user") { UserScreen() }
        }
    }
}