package com.example.gravit.main.User.Setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard

@Composable
fun DeletionComplete(
    navController: NavController,
    onGoMain: () -> Unit = {
        navController.navigate("home") {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.check),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(24.dp))

            Text(
                text = "탈퇴가 완료되었어요.",
                fontFamily = pretendard,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = "가입하신 이메일로 메일을 통해\n탈퇴처리를 완료했어요.",
                fontFamily = pretendard,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF868686),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            Text(
                text = "메인으로",
                fontFamily = pretendard,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF222222),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onGoMain() }
            )
        }
    }
}
