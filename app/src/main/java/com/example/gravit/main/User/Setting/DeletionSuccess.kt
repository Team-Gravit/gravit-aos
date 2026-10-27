package com.inuappcenter.gravit.main.User.Setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.R

@Composable
fun DeletionSuccess(
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.deleted_rabbit),
                contentDescription = null,
                modifier = Modifier.size(250.dp)
            )
            Spacer(Modifier.height(40.dp))

            Text(
                text = "탈퇴를 완료했어요.",
                style = AppTypography.Heading1,
                color = AppColor.text1,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "7일 내에 복구 절차를 진행하지 않을 시,\n계정과 데이터들이 모두 삭제됩니다.",
                style = AppTypography.Body2_Reading,
                color = AppColor.text4,
                textAlign = TextAlign.Center
            )
            /*
                        Text(
                            text = "메인으로",
                            fontFamily = pretendard,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF222222),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onGoMain() }
                        )

             */
        }
    }
}
