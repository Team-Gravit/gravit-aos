package com.inuappcenter.gravit.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import com.inuappcenter.gravit.ui.theme.pretendard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBottomSheet(
    onDismiss: () -> Unit,
    imageRes: Int? = null,
    titleText: String,
    descriptionText: String,
    confirmButtonText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        dragHandle = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 옵션 이미지
            imageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 20.dp)
                        .size(145.dp, 164.dp)
                )
            }

            // 제목
            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                textAlign = TextAlign.Center,
                color = Color(0xFF222222)
            )

            Spacer(Modifier.height(8.dp))

            // 설명
            Text(
                text = descriptionText,
                fontSize = 16.sp,
                fontFamily = pretendard,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // 보라색 버튼
            Button(
                onClick = {
                    onConfirm()
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8100B3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    confirmButtonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pretendard
                )
            }
            // 회색 텍스트 버튼
            Text(
                text = cancelText,
                color = Color(0xFF6D6D6D),
                fontFamily = pretendard,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCancel()
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                    .padding(vertical = 16.dp),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}
