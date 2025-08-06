package com.example.gravit.main

import BottomNavigationBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gravit.main.Home.HomeScreen
import com.example.gravit.main.Study.StudyScreen
import com.example.gravit.main.League.LeagueScreen
import com.example.gravit.main.Study.Stage.Earth
import com.example.gravit.main.Study.Stage.Jupiter
import com.example.gravit.main.Study.Stage.Mars
import com.example.gravit.main.Study.Stage.Mercury
import com.example.gravit.main.Study.Stage.Moon
import com.example.gravit.main.Study.Stage.Saturn
import com.example.gravit.main.Study.Stage.Uranus
import com.example.gravit.main.Study.Stage.Venus
import com.example.gravit.main.User.UserScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }

            //study
            composable("study") { StudyScreen(navController) }      // == study/root
            composable("study/earth") { Earth() }
            composable("study/jupiter") { Jupiter() }
            composable("study/mars") { Mars() }
            composable("study/mercury") { Mercury() }
            composable("study/moon") { Moon() }
            composable("study/saturn") { Saturn() }
            composable("study/uranus") { Uranus() }
            composable("study/venus") { Venus() }

            composable("league") { LeagueScreen() }
            composable("user") { UserScreen() }
        }
    }
}