package com.example.gravit.main.User

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
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
import com.example.gravit.main.User.NoticeListVM
import com.example.gravit.main.User.NoticeListVMFactory
import com.example.gravit.main.User.NoticeTopBar
import com.example.gravit.ui.theme.pretendard

@Composable
fun Notice(navController: NavController) {
    val ctx = LocalContext.current
    val vm: NoticeListVM = viewModel(factory = NoticeListVMFactory(ctx))
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.loadFirst() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        NoticeTopBar(navController)  // 공용 헤더

        if (ui.loading && ui.items.isEmpty()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        LazyColumn(Modifier.fillMaxSize()) {
            itemsIndexed(ui.items) { index, item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("user/notice/detail/${item.id}") }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = item.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = pretendard,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.summary,
                        fontSize = 13.sp,
                        fontFamily = pretendard,
                        color = Color(0xFF666666)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = formatIsoToYmd(item.publishedAt),
                        fontSize = 15.sp,
                        fontFamily = pretendard,
                        color = Color.Black
                    )
                }

                Divider(color = Color.Black.copy(alpha = 0.06f))

                // 마지막 아이템이면 다음 페이지 로드
                if (index == ui.items.lastIndex && ui.hasNext && !ui.loading) {
                    vm.loadNext()
                }
            }

            if (ui.loading && ui.items.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
