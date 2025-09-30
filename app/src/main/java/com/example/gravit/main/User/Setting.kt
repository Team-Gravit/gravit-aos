package com.example.gravit.main.User

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    val context = LocalContext.current

    val logoutVM: LogoutViewModel = viewModel(factory = LogoutVMFactory(context))

    val deleteVM: DeleteAccountVM = viewModel(
        factory = DeleteAccountVMFactory(RetrofitInstance.api, context)
    )
    val deleteUi by deleteVM.state.collectAsState()
    val deleteState by deleteVM.state.collectAsState()

    var showDeleteSheet by remember { mutableStateOf(false) }
    var showSentDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var notificationsEnabled by remember { mutableStateOf(areNotificationsEnabled(context)) }

    val requestNotifPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            notificationsEnabled = areNotificationsEnabled(context)
            if (!notificationsEnabled) openAppNotificationSettings(context)
        }

    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) {
                notificationsEnabled = areNotificationsEnabled(context)
                deleteVM.checkIfDeleted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            DeleteAccountVM.DeletionState.Confirmed -> {
                navController.navigate("user/deletion-complete") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            DeleteAccountVM.DeletionState.SessionExpired -> {
                navController.navigate("error/401") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true; restoreState = false
                }
            }
            else -> Unit
        }
    }

    var sending by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(Color.White)
    ) {
        Column {
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(IntrinsicSize.Min),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    RowItem(title = "사용자 설정", titleColor = 0xCC222222)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "앱 알림",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )

                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { wantEnable ->
                                if (wantEnable) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val hasPerm = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                        if (!hasPerm) {
                                            requestNotifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            if (!areNotificationsEnabled(context)) {
                                                openAppNotificationSettings(context)
                                            }
                                        }
                                    } else {
                                        if (!areNotificationsEnabled(context)) {
                                            openAppNotificationSettings(context)
                                        }
                                    }
                                } else {
                                    openAppNotificationSettings(context)
                                }
                            },
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
                    RowNavigableItem(
                        title = "로그아웃",
                        onClick = { logoutVM.logout { onLogout() } }
                    )

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
                cancelText = "탈퇴하기",
                onConfirm = {
                    showDeleteSheet = false
                },
                onCancel = {
                        deleteVM.requestDeletionMail(dest = "ANDROID") {
                            showDeleteSheet = false
                            showSentDialog = true
                    }
                }
            )
        }

        if (showSentDialog) {
            WithdrawalSentDialog(
                onConfirm = {
                    showSentDialog = false
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
private fun areNotificationsEnabled(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

private fun openAppNotificationSettings(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        } else {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }
    } catch (_: Exception) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }
}
