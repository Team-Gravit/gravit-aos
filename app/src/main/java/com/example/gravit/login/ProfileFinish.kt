package com.inuappcenter.gravit.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.R

@Composable
fun ProfileFinish(navController: NavController) {

    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !isDarkMode
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "계정 생성 완료!",
                    style = AppTypography.Heading1,
                    color = AppColor.text1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "그래빗의 일원이 된 걸 환영해요!",
                    style = AppTypography.Label2,
                    color = AppColor.text4,
                )
                Spacer(Modifier.height(40.dp))
                Image(
                    painter = painterResource(id = R.drawable.greeting),
                    contentDescription = "finish",
                    modifier = Modifier.size(210.dp, 176.dp)
                )
            }

            BlockButton(
                text = "홈으로",
                onClick = {
                    navController.navigate("main") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                style = AppTypography.Headline2,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            )
        }
    }
}
