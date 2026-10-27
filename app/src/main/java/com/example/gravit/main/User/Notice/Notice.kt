package com.inuappcenter.gravit.main.User.Notice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.share.LoadFailedDialog
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.main.User.TopBar

@Composable
fun Notice(
    navController: NavController,
    vm: NoticeListVM
) {
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        if (ui == NoticeListVM.UiState.Idle) {
            vm.loadAll()
        }
    }

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(ui) {
        if (navigated) return@LaunchedEffect

        when (ui) {
            NoticeListVM.UiState.SessionExpired -> {
                navigated = true
                vm.resetErrorState()

                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            NoticeListVM.UiState.NotFound -> {
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

    val noticeItems = when (val state = ui) {
        is NoticeListVM.UiState.Success -> state.items
        else -> emptyList()
    }

    StatusBarStyle(darkIcons = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg0)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(
                navController = navController,
                title = "공지사항"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColor.bg1),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 18.dp
                )
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppColor.bg0)
                    ) {
                        noticeItems.forEachIndexed { index, item ->
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
                                    verticalAlignment = Alignment.Top
                                ) {
                                    if (item.pinned) {
                                        Image(
                                            painter = painterResource(id = R.drawable.pin),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp, 22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = item.title,
                                        style = AppTypography.Headline2,
                                        color = AppColor.text1,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Icon(
                                        painter = painterResource(id = R.drawable.chevron_right),
                                        contentDescription = "공지사항 상세",
                                        tint = AppColor.icon_disabled,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = formatIsoToYmd(item.publishedAt),
                                    style = AppTypography.Caption1,
                                    color = AppColor.text4
                                )
                            }

                            if (index != noticeItems.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp,
                                    color = AppColor.divider1
                                )
                            }
                        }
                    }
                }
            }
        }

        if (ui == NoticeListVM.UiState.Failed) {
            LoadFailedDialog(
                onRetryClick = {
                    vm.loadAll()
                }
            )
        }
    }
}