package com.example.gravit.login

import androidx.compose.foundation.Image
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
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard

@Composable
fun FinishScreen() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "계정 생성 완료!",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp
                ),
                modifier = Modifier
                    .padding(top = screenHeight * (128f / 740f))
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
                    .padding(top = screenHeight * (20f / 740f))
                    .align(Alignment.CenterHorizontally)
            )

            Image(
                painter = painterResource(id = R.drawable.finish),
                contentDescription = "finish",
                modifier = Modifier
                    .padding(top = screenHeight * (20f / 740f))
                    .size(screenWidth * (216f / 360f))
                    .align(Alignment.CenterHorizontally)
            )

            Button(
                onClick = {
                // 홈으로 넘어가기
                },
                modifier = Modifier
                    .padding(top = screenHeight * (243f / 740f))
                    .size(screenWidth * (325f / 360f), screenHeight * (60f / 740f))
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8100B3),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "홈으로",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    FinishScreen()
}
