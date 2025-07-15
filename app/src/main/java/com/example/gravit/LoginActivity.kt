package com.example.gravit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.ui.theme.pretendard

@Composable
fun LoginScreen (navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "로그인",
            modifier = Modifier
                .padding(top = 51.dp)
                .align(Alignment.TopCenter),
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        )
        Column(
            modifier = Modifier
                .padding(start = 25.dp, top = 129.dp)

        ) {
            Image(
                painter = painterResource(id=R.drawable.gravit_login_logo),
                contentDescription = "login_logo",
                modifier = Modifier
                    .size(72.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "교육행성에 어서 오세요.\nGravit!",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF030303)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "회원 서비스 이용을 위해 로그인 해주세요.",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF7D7D7D)
                )
            )
            Spacer(modifier = Modifier.height(30.dp))

            Canvas(
                modifier = Modifier
                    .width(325.dp)
                    .height(1.dp)
                    .align(Alignment.CenterHorizontally)

            ) {
                drawLine(
                    color = Color(0xffC3C3C3),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)),
                    cap = StrokeCap.Butt

                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 367.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SocialLoginButton(
                text = "Google로 시작하기",
                backgroundColor = Color(0xFFF2F2F2),
                contentColor = Color(0xFF4C4C4C),
                logoResId = R.drawable.goole_login_logo,
                modifier = Modifier.width(325.dp),
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            SocialLoginButton(
                text = "카카오로 시작하기",
                backgroundColor = Color(0xFFFEE500),
                contentColor = Color(0xFF4C4C4C),
                logoResId = R.drawable.kakao_login_logo,
                modifier = Modifier.width(325.dp),
                onClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SocialLoginButton(
                text = "네이버로 시작하기",
                backgroundColor = Color(0xFF03C75A),
                contentColor = Color.White,
                logoResId = R.drawable.naver_login_logo,
                modifier = Modifier.width(325.dp),
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    logoResId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "$text logo",
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = text,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(backgroundColor),
                fontSize = 16.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChoiceScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController)
}

/*** 점선 사이즈 체크 **/