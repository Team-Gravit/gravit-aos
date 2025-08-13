package com.example.gravit.login

import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard
import androidx.navigation.NavController
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import androidx.core.content.edit
import com.example.gravit.api.AuthPrefs

@Composable
fun LoginScreen (
    navController: NavController,
    viewModel: LoginViewModel
) {

    val context = LocalContext.current
    val jwt by viewModel.jwtToken.collectAsState()


    LaunchedEffect(jwt) {
        jwt?.let { token ->
            //저장
            AuthPrefs.save(context, token.accessToken, token.isOnboarded)

            val s = AuthPrefs.load(context) //저장이 실패됐는지 다시 확인
            if (s == null) {
                navController.navigate("login choice") {
                    popUpTo("login choice") { inclusive = true }
                    launchSingleTop = true
                    restoreState = false
                }
                return@LaunchedEffect
            }

            //온보딩 여부 확인
            val target = if (s.isOnboarded) "main" else "profile setting"
            navController.navigate(target) {
                popUpTo("login choice") { inclusive = true }
                launchSingleTop = true
                restoreState = false
            }
        }
    }


    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    CompositionLocalProvider(
        LocalScreenWidth provides screenWidth,
        LocalScreenHeight provides screenHeight
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "로그인",
                modifier = Modifier
                    .padding(top = screenHeight * (51f / 812f))
                    .align(Alignment.TopCenter),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = screenHeight * (129f / 812f)),

                ) {
                Column (modifier = Modifier.padding(start = screenWidth * (25f / 375f))){
                    Image(
                        painter = painterResource(id = R.drawable.gravit_login_logo),
                        contentDescription = "login_logo",
                        modifier = Modifier.size(screenWidth * (72f / 375f))

                    )
                    Spacer(modifier = Modifier.height(screenHeight * (20f / 812f)))

                    Text(
                        text = "교육행성에 어서 오세요.\nGravit!",
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF030303)
                        )
                    )
                    Spacer(modifier = Modifier.height(screenHeight * (10f / 812f)))

                    Text(
                        text = "회원 서비스 이용을 위해 로그인 해주세요.",
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color(0xFF7D7D7D)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(screenHeight * (30f / 812f)))

                Canvas(
                    modifier = Modifier
                        .width(screenWidth * (325f / 375f))
                        .height(screenHeight * (1f / 812f))
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
                    .padding(top = screenHeight * (367f / 812f)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SocialLoginButton(
                    text = "Google로 시작하기",
                    backgroundColor = Color(0xFFF2F2F2),
                    contentColor = Color(0xFF4C4C4C),
                    logoResId = R.drawable.goole_login_logo,
                    onClick = {
                        loginWithAuth0(context, "google-oauth2") { idToken -> //onSuccess에서 넘긴 값
                            viewModel.sendIdTokenToServer(idToken) //ViewModel 호출
                        }
                    }
                )
                Spacer(modifier = Modifier.height(screenHeight * (8f / 812f)))

                SocialLoginButton(
                    text = "카카오로 시작하기",
                    backgroundColor = Color(0xFFFEE500),
                    contentColor = Color(0xFF4C4C4C),
                    logoResId = R.drawable.kakao_login_logo,
                    onClick = {
                        loginWithAuth0(context, "kakao") { idToken ->
                            viewModel.sendIdTokenToServer(idToken)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(screenHeight * (8f / 812f)))

                SocialLoginButton(
                    text = "네이버로 시작하기",
                    backgroundColor = Color(0xFF03C75A),
                    contentColor = Color.White,
                    logoResId = R.drawable.naver_login_logo,
                    onClick = {
                        loginWithAuth0(context, "Naver") { idToken ->
                            viewModel.sendIdTokenToServer(idToken)
                        }
                    }
                )
            }
        }

    }
}

@Composable
fun SocialLoginButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    logoResId: Int,
    onClick: () -> Unit
) {
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    Button(
        onClick = onClick,
        modifier = Modifier
            .width(screenWidth * (325f / 375f))
            .height(screenHeight * (50f / 812f)),
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
                modifier = Modifier.size(screenWidth * (40f / 375f))
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
