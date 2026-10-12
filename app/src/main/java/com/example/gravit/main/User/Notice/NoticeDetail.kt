package com.inuappcenter.gravit.main.User.Notice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.share.LoadFailedDialog
import com.example.gravit.share.StatusBarStyle
import com.inuappcenter.gravit.main.User.TopBar
import com.inuappcenter.gravit.ui.theme.pretendard

@Composable
fun NoticeDetail(
    navController: NavController,
    noticeId: Long
) {
    val ctx = LocalContext.current
    val vm: NoticeDetailVM = viewModel(factory = NoticeDetailVMFactory(ctx))
    val ui by vm.state.collectAsState()

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(noticeId) {
        vm.load(noticeId)
    }

    LaunchedEffect(ui) {
        if (navigated) return@LaunchedEffect

        when (ui) {
            NoticeDetailVM.UiState.SessionExpired -> {
                navigated = true
                vm.resetErrorState()

                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            NoticeDetailVM.UiState.NotFound -> {
                navigated = true
                vm.resetErrorState()

                navController.navigate("error/404") {
                    launchSingleTop = true
                }
            }

            else -> {
                navigated = false
            }
        }
    }

    StatusBarStyle(darkIcons = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(navController, title = "공지사항")

            when (val state = ui) {
                NoticeDetailVM.UiState.Idle,
                NoticeDetailVM.UiState.Loading -> { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
                is NoticeDetailVM.UiState.Success -> {
                    val item = state.item
                    val scroll = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = pretendard,
                            color = Color.Black
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = formatIsoToYmd(item.publishedAt),
                            fontSize = 13.sp,
                            fontFamily = pretendard,
                            color = Color(0xFF666666)
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = item.content,
                            fontSize = 15.sp,
                            fontFamily = pretendard,
                            color = Color(0xFF222222),
                            lineHeight = 22.sp
                        )
                    }
                }

                NoticeDetailVM.UiState.Failed -> Unit

                NoticeDetailVM.UiState.SessionExpired,
                NoticeDetailVM.UiState.NotFound -> Unit
            }
        }

        if (ui == NoticeDetailVM.UiState.Failed) {
            LoadFailedDialog(
                onRetryClick = {
                    vm.load(noticeId)
                }
            )
        }
    }
}