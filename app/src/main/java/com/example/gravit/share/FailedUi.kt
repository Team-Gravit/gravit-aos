package com.example.gravit.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography

@Composable
fun LoadFailedDialog(
    onRetryClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .width(328.dp)
                .padding(bottom = 16.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .width(328.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "오류가 발생했습니다",
                    style = AppTypography.Heading1,
                    textAlign = TextAlign.Center,
                    color = AppColor.text1
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "잠시 후 다시 시도해 주세요.",
                    style = AppTypography.Label1,
                    color = AppColor.text4,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.Main1)
                        .clickable {
                            onRetryClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "다시 시도",
                        style = AppTypography.Label1,
                        color = Color.White
                    )
                }
            }
        }
    }
}