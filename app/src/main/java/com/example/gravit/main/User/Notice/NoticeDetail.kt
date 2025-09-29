package com.example.gravit.main.User.Notice

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
import com.example.gravit.ui.theme.pretendard

@Composable
fun NoticeDetail(
    navController: NavController,
    noticeId: Long
) {
    val ctx = LocalContext.current
    val vm: NoticeDetailVM = viewModel(factory = NoticeDetailVMFactory(ctx))
    val ui by vm.state.collectAsState()

    LaunchedEffect(noticeId) { vm.load(noticeId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        NoticeTopBar(navController)

        when {
            ui.loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            ui.item != null -> {
                val item = ui.item!!
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
            else -> {
                Text(
                    text = ui.error ?: "내용을 불러오지 못했습니다.",
                    modifier = Modifier.padding(16.dp),
                    fontFamily = pretendard,
                    color = Color.Red
                )
            }
        }
    }
}
