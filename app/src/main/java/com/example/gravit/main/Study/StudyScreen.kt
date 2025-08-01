package com.example.gravit.main.Study

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.login.LoginScreen

@Composable
fun StudyScreen(navController: NavController){
    BoxWithConstraints (
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        Column (
            modifier = Modifier.fillMaxSize()
        ){
            Button(onClick = { navController.navigate("study/earth")}) {
                Text("earth")
            }
            Button(onClick = { navController.navigate("study/jupiter")}) {
                Text("jupiter")
            }
            Button(onClick = { navController.navigate("study/mars")}) {
                Text("mars")
            }
            Button(onClick = { navController.navigate("study/mercury")}) {
                Text("mercury")
            }
            Button(onClick = { navController.navigate("study/moon")}) {
                Text("moon")
            }
            Button(onClick = { navController.navigate("study/saturn")}) {
                Text("saturn")
            }
            Button(onClick = { navController.navigate("study/uranus")}) {
                Text("uranus")
            }
            Button(onClick = { navController.navigate("study/venus")}) {
                Text("venus")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    val navController = rememberNavController()
    StudyScreen(navController)
}
