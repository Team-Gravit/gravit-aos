package com.example.gravit.main.User

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.ConfirmBottomSheet
import com.example.gravit.main.User.Setting.DeleteAccountVM
import com.example.gravit.main.User.Setting.DeleteAccountVMFactory
import com.example.gravit.main.User.Setting.LogoutVMFactory
import com.example.gravit.main.User.Setting.LogoutViewModel
import com.example.gravit.ui.theme.pretendard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setting(
    navController: NavController,
    onLogout: () -> Unit
) {
    var isChecked by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // 로그아웃 VM
    val logoutVM: LogoutViewModel = viewModel(factory = LogoutVMFactory(context))

    // 탈퇴 메일 요청 VM
    val deleteVM: DeleteAccountVM = viewModel(
        factory = DeleteAccountVMFactory(RetrofitInstance.api, context)
    )
    val deleteUi by deleteVM.state.collectAsState()

    // 모달/다이얼로그 표시 상태
    var showDeleteSheet by remember { mutableStateOf(false) }
    var showSentDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(Color.White)
    ) {
        Column {
            // 상단바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() },
                    tint = Color(0xFF4D4D4D)
                )
            }

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // 계정 정보 섹션
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(100.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    RowItem(title = "계정 정보", titleColor = 0xCC222222)

                    RowNavigableItem(
                        title = "내 정보",
                        onClick = { navController.navigate("user/account") }
                    )
                }
            }

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // 사용자 설정 섹션
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(140.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    RowItem(title = "사용자 설정", titleColor = 0xCC222222)

                    // 마케팅/이벤트 알림 스위치
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "마케팅/ 이벤트 알림",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )

                        Switch(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .scale(0.8f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFBA00FF),
                                checkedTrackColor = Color(0xFFEDD7FF),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFD9D9D9),
                                checkedBorderColor = Color.Transparent,
                                uncheckedBorderColor = Color.Transparent,
                                disabledCheckedBorderColor = Color.Transparent,
                                disabledUncheckedBorderColor = Color.Transparent
                            )
                        )
                    }

                    RowNavigableItem(
                        title = "개인정보 처리방침",
                        onClick = { navController.navigate("user/privacypolicy") }
                    )
                }
            }

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // 로그아웃/탈퇴 섹션
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(100.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // 로그아웃
                    RowNavigableItem(
                        title = "로그아웃",
                        onClick = { logoutVM.logout { onLogout() } }
                    )

                    // 탈퇴하기
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "탈퇴하기",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    // 모달만 띄운다 (여기서 바로 API 호출하지 않음)
                                    showDeleteSheet = true
                                },
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }

        if (showDeleteSheet) {
            ConfirmBottomSheet(
                onDismiss = { showDeleteSheet = false },
                imageRes = R.drawable.study_popup,
                titleText = "정말 탈퇴하실건가요?",
                descriptionText = "계정을 삭제하면 저장된\n 모든 데이터가 사라져요.\n 정말로 계정을 삭제하실건가요?",
                confirmButtonText = "돌아가기",
                cancelText = if (deleteUi.requesting) "전송 중..." else "탈퇴하기",
                onConfirm = {
                    showDeleteSheet = false
                },
                onCancel = {
                    if (!deleteUi.requesting) {
                        deleteVM.requestDeletionMail(dest = "ANDROID") {
                            showDeleteSheet = false
                            showSentDialog = true
                        }
                    }
                }
            )
        }

        if (showSentDialog) {
            WithdrawalSentDialog(
                onConfirm = {
                    showSentDialog = false
                    navController.navigate("user/deletion-complete") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun RowItem(title: String, titleColor: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard
            ),
            color = Color(titleColor)
        )
    }
}

@Composable
private fun RowNavigableItem(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard
            ),
            color = Color(0xFF222222),
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.CenterEnd)
                .clickable { onClick() },
            tint = Color.Unspecified
        )
    }
}

@Composable
fun WithdrawalSentDialog(
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onConfirm) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(20.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.check),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "탈퇴하신다니 정말 아쉬워요.",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    fontFamily = pretendard,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "가입하신 이메일로 메일을\n전송해드렸으니 메일을 확인해주시고\n절차를 따라주세요.",
                    fontFamily = pretendard,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF868686),
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .navigationBarsPadding(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8100B3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ) {
                Text(
                    text = "확인",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    fontFamily = pretendard
                )
            }
        }
    }
}