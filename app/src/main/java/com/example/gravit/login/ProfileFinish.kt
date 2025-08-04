package com.example.gravit.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard

@Composable
fun ProfileFinish(navController: NavController) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        Box (modifier = Modifier.fillMaxSize()) {
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
                        fontSize = 24.sp
                    ),
                    modifier = Modifier
                        .padding(top = screenHeight * (171f / 812f))
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "그래빗의 일원이 된 걸 환영해요!",
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    ),
                    color = Color(0xFF4C4C4C),
                    modifier = Modifier
                        .padding(top = screenHeight * (20f / 812f))
                        .align(Alignment.CenterHorizontally)
                )

                Image(
                    painter = painterResource(id = R.drawable.finish),
                    contentDescription = "finish",
                    modifier = Modifier
                        .padding(top = screenHeight * (20f / 812f))
                        .size(screenWidth * (216f / 375f))
                        .align(Alignment.CenterHorizontally)
                )

            }
        }

        CustomButton(
            text = "홈으로",
            onClick = { navController.navigate("main") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = screenHeight * (34f / 812f)) // 하단 여백 조절
                .size(screenWidth * (325f / 375f), screenHeight * (60f / 812f))
        )

    }
}

