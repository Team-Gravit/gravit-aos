package com.inuappcenter.gravit.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.ui.theme.DesignSpec
import com.inuappcenter.gravit.ui.theme.LocalDesignSpec
import com.inuappcenter.gravit.ui.theme.Responsive
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R

@Composable
fun ProfileFinish(navController: NavController) {
    CompositionLocalProvider(
        LocalDesignSpec provides DesignSpec(375f, 812f)
    ) {
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "계정 생성 완료!",
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = Responsive.spH(24f)
                    ),
                    modifier = Modifier
                        .padding(top = Responsive.h(171f))
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "그래빗의 일원이 된 걸 환영해요!",
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = Responsive.spH(16f)
                    ),
                    color = Color(0xFF4C4C4C),
                    modifier = Modifier
                        .padding(top = Responsive.h(20f))
                        .align(Alignment.CenterHorizontally)
                )

                Image(
                    painter = painterResource(id = R.drawable.greeting),
                    contentDescription = "finish",
                    modifier = Modifier
                        .padding(top = Responsive.h(60f))
                        .size(Responsive.w(216f))
                        .align(Alignment.CenterHorizontally)
                )
            }

            CustomButton(
                text = "홈으로",
                onClick = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(
                        start = Responsive.w(25f),
                        end = Responsive.w(25f),
                        bottom = Responsive.h(14f)
                    )
                    .height(Responsive.h(60f))
            )
        }
    }
}
