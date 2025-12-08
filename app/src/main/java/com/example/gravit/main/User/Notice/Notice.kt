package com.example.gravit.main.User.Notice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.TopBar
import com.example.gravit.ui.theme.pretendard
import kotlin.math.max
import kotlin.math.min

@Composable
fun Notice(navController: NavController) {
    val ctx = LocalContext.current
    val vm: NoticeListVM = viewModel(factory = NoticeListVMFactory(ctx))
    val ui by vm.state.collectAsState()
    val pageSize = 4
    var currentPage by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        vm.loadFirst()
    }

    val totalItems = ui.items.size
    val totalPages = max(1, (totalItems + pageSize - 1) / pageSize)

    LaunchedEffect(totalPages) {
        if (currentPage > totalPages - 1) {
            currentPage = totalPages - 1
        }
    }

    val startIndex = currentPage * pageSize
    val endIndex = min(startIndex + pageSize, totalItems)
    val pageItems = if (startIndex < endIndex) {
        ui.items.subList(startIndex, endIndex)
    } else {
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar(navController, title = "공지사항")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(pageItems) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("user/notice/detail/${item.id}")
                        }
                        .padding(horizontal = 20.dp, vertical = 30.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = item.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = pretendard,
                            color = Color.Black
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_right),
                            contentDescription = "다음 페이지",
                            tint = Color(0xFF222222),
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = formatIsoToYmd(item.publishedAt),
                        fontSize = 15.sp,
                        fontFamily = pretendard,
                        color = Color(0xFF000000).copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.summary,
                        fontSize = 13.sp,
                        fontFamily = pretendard,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Divider(color = Color.Black.copy(alpha = 0.06f))
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (totalPages > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "이전 페이지",
                    tint = if (currentPage > 0) Color.Black else Color.LightGray,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(20.dp)
                        .clickable(enabled = currentPage > 0) {
                            if (currentPage > 0) {
                                currentPage -= 1
                            }
                        }
                )
                for (page in 0 until totalPages) {
                    val isSelected = page == currentPage
                    Text(
                        text = (page + 1).toString(),
                        fontSize = 20.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = pretendard,
                        color = if (isSelected) Color(0xFFBA00FF) else Color(0xFF333333).copy(alpha = 0.7f),
                        textDecoration = if (isSelected) TextDecoration.Underline else TextDecoration.None,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable {
                                currentPage = page
                            }
                    )
                }
                val isLastLocalPage = currentPage >= totalPages - 1
                val canLoadMoreFromServer = ui.hasNext

                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = "다음 페이지",
                    tint = if (isLastLocalPage && !canLoadMoreFromServer) Color.LightGray else Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(20.dp)
                        .clickable(enabled = !ui.loading) {
                            when {
                                currentPage < totalPages - 1 -> {
                                    currentPage += 1
                                }
                                currentPage == totalPages - 1 && canLoadMoreFromServer -> {
                                    vm.loadNext()
                                }
                            }
                        }
                )
            }
        }
    }
}
