package com.example.gravit.error

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.gravit.R

@Composable
fun UnauthorizedScreen(
    navController: NavController,
    onSessionExpired: () -> Unit
){
    ErrorScreen(
        img = R.drawable.img401,
        title = "접근 권한이 없어요.",
        content = "이 페이지는 로그인한 사용자만\n이용할 수 있어요.\n계속하시려면 로그인 후 다시 시도해 주세요.",
        onClick1 = { onSessionExpired() },
        onClick2 = {navController.popBackStack()}
    )
}