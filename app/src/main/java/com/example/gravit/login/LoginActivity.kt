package com.example.gravit.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard
import androidx.navigation.NavController
import com.example.gravit.DesignSpec
import com.example.gravit.LocalDesignSpec
import com.example.gravit.Responsive
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
                if (target == "profile setting") {
                    //뒤로가기 시 복귀
                    popUpTo("login choice") { inclusive = false }
                } else {
                    //온보딩 완료 사용자는 로그인 제거
                    popUpTo("login choice") { inclusive = true }
                }
                launchSingleTop = true
                restoreState = false
            }
        }
    }

    CompositionLocalProvider(
        LocalDesignSpec provides DesignSpec(375f, 812f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            Text(
                text = "로그인",
                modifier = Modifier
                    .padding(top = Responsive.h(40f))
                    .align(Alignment.TopCenter),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = Responsive.spH(20f)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Responsive.h(129f)),
            ) {
                Column(modifier = Modifier.padding(start = Responsive.w(25f))) {
                    Image(
                        painter = painterResource(id = R.drawable.gravit_login_logo),
                        contentDescription = "login_logo",
                        modifier = Modifier.size(Responsive.h(72f))
                    )
                    Spacer(modifier = Modifier.height(Responsive.h(20f)))

                    Text(
                        text = "교육행성에 어서 오세요.\nGravit!",
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = Responsive.spH(24f),
                            color = Color(0xFF030303)
                        )
                    )
                    Spacer(modifier = Modifier.height(Responsive.h(10f)))

                    Text(
                        text = "회원 서비스 이용을 위해 로그인 해주세요.",
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = Responsive.spH(15f),
                            color = Color(0xFF7D7D7D)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(Responsive.h(30f)))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Responsive.w(25f)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Responsive.h(1f))
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
                    Spacer(modifier = Modifier.height(Responsive.h(30f)))

                    SocialLoginButton(
                        text = "Google로 시작하기",
                        backgroundColor = Color(0xFFF2F2F2),
                        contentColor = Color(0xFF4C4C4C),
                        logoResId = R.drawable.goole_login_logo,
                        onClick = {
                            loginWithAuth0(context, "google-oauth2") { idToken ->
                                viewModel.sendIdTokenToServer(idToken)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(Responsive.h(8f)))

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
                    Spacer(modifier = Modifier.height(Responsive.h(8f)))

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

}

@Composable
fun SocialLoginButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    logoResId: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(Responsive.h(50f)),
        shape = RoundedCornerShape(Responsive.h(8f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "$text logo",
                modifier = Modifier.size(Responsive.w(40f))
            )
            Text(
                text = text,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(backgroundColor),
                fontSize = Responsive.spH(16f),
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}