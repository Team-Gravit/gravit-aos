package com.example.gravit.main

import BottomNavigationBar
import android.accounts.Account
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
import com.example.gravit.main.User.AddFriend
import com.example.gravit.main.User.Setting
import com.example.gravit.main.User.UserScreen

@Composable
fun MainScreen(rootNavController: NavController) {
    val innerNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(innerNavController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }

            // study
            composable("study") { StudyScreen(innerNavController) }
            composable("study/earth") { Earth(innerNavController) }
            composable("study/jupiter") { Jupiter() }
            composable("study/mars") { Mars() }
            composable("study/mercury") { Mercury() }
            composable("study/moon") { Moon() }
            composable("study/saturn") { Saturn() }
            composable("study/uranus") { Uranus() }
            composable("study/venus") { Venus() }

            composable("league") { LeagueScreen() }

            composable("user") { UserScreen(innerNavController) }
            composable("setting") { Setting(innerNavController) }
            composable("account") { com.example.gravit.main.User.Account(innerNavController)}
        //Account로 하면 안되고 저렇게 하면 되더라ㅠ
            composable("addfriend") { AddFriend(innerNavController) }
        }
    }
}
