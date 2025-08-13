package com.example.gravit.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gravit.R
import com.example.gravit.api.AuthPrefs
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp.dp
    val heightDp = configuration.screenHeightDp.dp

    // dp → px 변환
    val density = LocalDensity.current
    val widthPx = with(density) { widthDp.toPx() }
    val heightPx = with(density) { heightDp.toPx() }

    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF8100B3), Color(0xFFDD00FF)),
        start = Offset(0f, heightPx),
        end = Offset(widthPx, 0f)
    )

    val context = LocalContext.current

    LaunchedEffect(Unit) {

        delay(300)

        val session = AuthPrefs.load(context)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(context)
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        } else {
            if (session.isOnboarded) {
                navController.navigate("main") {
                    popUpTo(0)
                    launchSingleTop = true
                    restoreState = false
                }
            } else {
                navController.navigate("profile setting") {
                    popUpTo(0)
                    launchSingleTop = true
                    restoreState = false
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_gravit_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
    }

}
