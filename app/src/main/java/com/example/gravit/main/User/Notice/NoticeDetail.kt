package com.inuappcenter.gravit.main.User.Notice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.share.LoadFailedDialog
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.main.Study.Lesson.MarkdownText
import com.inuappcenter.gravit.main.User.TopBar

@Composable
fun NoticeDetail(
    navController: NavController,
    noticeId: Long,
    noticeListVm: NoticeListVM
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val vm: NoticeDetailVM = viewModel(
        factory = NoticeDetailVMFactory(context)
    )

    val ui by vm.state.collectAsState()
    val listUi by noticeListVm.state.collectAsState()

    LaunchedEffect(listUi) {
        if (listUi is NoticeListVM.UiState.Idle) {
            noticeListVm.loadAll()
        }
    }
    var navigated by remember { mutableStateOf(false) }

    val noticeItems = when (val state = listUi) {
        is NoticeListVM.UiState.Success -> state.items
        else -> emptyList()
    }

    val currentIndex = noticeItems.indexOfFirst {
        it.id == noticeId
    }

    val previousNotice = if (currentIndex > 0) {
        noticeItems.getOrNull(currentIndex - 1)
    } else {
        null
    }

    val nextNotice = if (
        currentIndex >= 0 &&
        currentIndex < noticeItems.lastIndex
    ) {
        noticeItems.getOrNull(currentIndex + 1)
    } else {
        null
    }

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

            when (val state = ui) {
                NoticeDetailVM.UiState.Idle,
                NoticeDetailVM.UiState.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is NoticeDetailVM.UiState.Success -> {
                    val item = state.item
                    val scroll = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppColor.bg1)
                            .verticalScroll(scroll)
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColor.bg0)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = AppTypography.Title3,
                                color = AppColor.text1
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatIsoToYmd(item.publishedAt),
                                style = AppTypography.Caption1,
                                color = AppColor.text4
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = AppColor.icon_default
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            MarkdownText(item.content)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (previousNotice != null) {
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                navController.navigate(
                                                    "user/notice/detail/${previousNotice.id}"
                                                ) {
                                                    popUpTo("user/notice/detail/{noticeId}") {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                            .padding(vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = R.drawable.chevron_left
                                            ),
                                            contentDescription = "이전 공지",
                                            tint = AppColor.icon_default,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = previousNotice.title,
                                            style = AppTypography.Body2_Reading,
                                            color = AppColor.text1,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (nextNotice != null) {
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                navController.navigate(
                                                    "user/notice/detail/${nextNotice.id}"
                                                ) {
                                                    popUpTo("user/notice/detail/{noticeId}") {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                            .padding(vertical = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = nextNotice.title,
                                            style = AppTypography.Body2_Reading,
                                            color = AppColor.text1,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Icon(
                                            painter = painterResource(
                                                id = R.drawable.chevron_right
                                            ),
                                            contentDescription = "다음 공지",
                                            tint = AppColor.icon_default,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
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