package com.inuappcenter.gravit.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.NoCredentialException
import com.inuappcenter.gravit.R
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.main.User.TopBar
import kotlinx.coroutines.launch

@Composable
fun LoginScreen (
    navController: NavController,
    viewModel: LoginViewModel
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val jwt by viewModel.jwtToken.collectAsState()


    LaunchedEffect(jwt) {
        jwt?.let { token ->
            //저장
            AuthPrefs.save(context, token.accessToken, token.refreshToken,token.isOnboarded)

            val s = AuthPrefs.load(context)
            if (s == null) {
                navController.navigate("login choice") {
                    popUpTo("login choice") { inclusive = true }
                    launchSingleTop = true
                    restoreState = false
                }
                return@LaunchedEffect
            }

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(navController, "로그인", 48.dp, useIcon = false)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(392.dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gravit_login_logo),
                    contentDescription = "login_logo",
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "교육행성에 어서 오세요.\nGravit!",
                    style = AppTypography.Heading1,
                    color = AppColor.text1,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "회원 서비스 이용을 위해 로그인 해주세요.",
                    style = AppTypography.Body1_Nomal,
                    color = AppColor.text3,
                )

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SocialLoginButton(
                    text = "Google로 시작하기",
                    backgroundColor = AppColor.bg1,
                    contentColor = AppColor.CTA_secondary_text,
                    logoResId = R.drawable.google_logo,
                    onClick = {
                        scope.launch {
                            try {
                                val idToken = loginWithGoogle(context)
                                viewModel.sendIdTokenToServer("google", idToken)
                            } catch (e: NoCredentialException) {
                                Log.w("GoogleLogin", "사용 가능한 Google 계정이 없음")
                            } catch (e: Exception) {
                                Log.e("GoogleLogin", "구글 로그인 실패", e)
                            }
                        }
                    },
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = AppColor.divider2,
                        shape = RoundedCornerShape(8.dp)
                    )
                )
                SocialLoginButton(
                    text = "카카오로 시작하기",
                    backgroundColor = Color(0xFFFFE240),
                    contentColor = AppColor.CTA_secondary_text,
                    logoResId = R.drawable.kakao_logo,
                    onClick = {
                        loginWithKakao(
                            context = context,
                            connection = "kakao",
                            onSuccess = { idToken ->
                                viewModel.sendIdTokenToServer("kakao",idToken)
                            },
                            onError = { e ->
                                Log.e("KakaoLogin", "failed", e)
                            }
                        )

                    }
                )
                SocialLoginButton(
                    text = "네이버로 시작하기",
                    backgroundColor = Color(0xFF00B116),
                    contentColor = AppColor.CTA_text,
                    logoResId = R.drawable.naver_logo,
                    onClick = {
                        loginWithNaver(
                            context = context,
                            viewModel = viewModel,
                            onError = { e ->
                                Log.e("NaverLogin", "failed", e)
                            }
                        )
                    }
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    logoResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "$text logo",
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
                    .align(Alignment.CenterStart)
            )
            Text(
                text = text,
                style = AppTypography.Body2_Reading
            )
        }
    }
}