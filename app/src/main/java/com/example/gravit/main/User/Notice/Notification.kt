package com.example.gravit.main.User.Notice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.InlineButton
import com.example.gravit.ui.theme.InlineButtonState
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Study.Problem.CustomSnackBar
import com.inuappcenter.gravit.main.User.TopBar
import com.inuappcenter.gravit.main.User.UserScreenVM
import com.inuappcenter.gravit.main.User.UserVMFactory
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Notice2(
    navController: NavController,
){
    val date = LocalDate.now()

    val dateText = date.format(
        DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN)
    )
    val context = LocalContext.current
    val notificationVM: NotificationVM = viewModel(factory = NotificationVMFactory(RetrofitInstance.api, context))
    val notificationUi by notificationVM.state.collectAsState()

    val congratulateVM: UserScreenVM = viewModel(factory = UserVMFactory(RetrofitInstance.api, context))
    val congratulateUi by congratulateVM.stateCongratulate.collectAsState()


    var navigated by remember { mutableStateOf(false) }
    var showSnackBar by remember { mutableStateOf(false) }
    var snackBarText by remember { mutableStateOf("") }
    val isLoading = notificationUi == NotificationVM.UiState.Loading
    LaunchedEffect(Unit) {
        notificationVM.load()
    }

    LaunchedEffect(notificationUi) {
        if (navigated) return@LaunchedEffect

        when (notificationUi) {
            NotificationVM.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            NotificationVM.UiState.NotFound -> {
                navigated = true
                navController.navigate("error/404"){
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val total = listState.layoutInfo.totalItemsCount
            if (last != null) last to total else null
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (lastVisible >= total - 3) {
                    notificationVM.loadMore()
                }
            }
    }
    val notification = (notificationUi as? NotificationVM.UiState.Success)?.data

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg2),
        ) {
            item {
                TopBar(
                    navController = navController,
                    title = "알림",
                    useCloseIcon = false,
                    height = 48.dp
                )
            }
            item {
                Text(
                    text = dateText,
                    style = AppTypography.Label2,
                    color = PrimitiveColor.Gray500,
                    modifier = Modifier.padding(
                        top = 20.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                )
            }
            items(notification?.contents ?: emptyList()) { notification ->
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                        .fillMaxWidth()
                        .height(114.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0)
                        .border(1.dp, shape = RoundedCornerShape(8.dp), color = AppColor.divider1)
                        .padding(16.dp)
                ) {
                    Column(
                    ) {
                        if(notification.actionType == "FOLLOW_BACK" || notification.actionType == "UNFOLLOW") {
                            Row() {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(ProfilePalette.idToColor(notification.actor?.profileImgNumber
                                            ?: 1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.profile_logo),
                                        contentDescription = "profile logo",
                                        modifier = Modifier.size(18.dp, 20.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column() {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notification.actor?.nickname ?: "",
                                            style = AppTypography.Label1,
                                            color = AppColor.text1
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = notification.timeAgo,
                                            style = AppTypography.Caption1,
                                            color = AppColor.text4
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = notification.message,
                                        style = AppTypography.Label2,
                                        color = AppColor.text3
                                    )
                                }
                            }
                        }else{
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = notification.message,
                                    style = AppTypography.Label1,
                                    color = AppColor.text1
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = notification.timeAgo,
                                    style = AppTypography.Caption1,
                                    color = AppColor.text4
                                )
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        if(notification.actionType != "NONE"){
                            InlineButton(
                                text =
                                    when (notification.actionType) {
                                        "FOLLOW_BACK" -> "맞팔로우"
                                        "GO_TO_LEARNING" -> "학습하러 가기"
                                        "GO_TO_NOTICE" -> "공지사항 바로가기"
                                        "UNFOLLOW" -> "팔로우 취소"
                                        "CONGRATULATE" -> "축하하기"
                                        "GO_TO_INQUIRY" -> "문의사항 바로가기"
                                        else -> ""
                                    },
                                onClick = {
                                    when (notification.actionType) {
                                        "FOLLOW_BACK" -> {notificationVM.toggleFollow(notification.targetId?: 0, notification.actionType)}
                                        "GO_TO_LEARNING" -> {
                                            if(notification.targetId == null)
                                                navController.navigate("chapter")
                                            else navController.navigate("")
                                        }
                                        "GO_TO_NOTICE" -> { navController.navigate("")}
                                        "UNFOLLOW" -> {notificationVM.toggleFollow(notification.targetId?: 0, notification.actionType)}
                                        "CONGRATULATE" -> {congratulateVM.congratulate(notification.targetId?: 0)}
                                        "GO_TO_INQUIRY" -> {navController.navigate("")}
                                        else -> ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp),
                                style = AppTypography.Label2,
                                color = if(notification.actionType == "UNFOLLOW") AppColor.CTA else AppColor.CTA_text,
                                state = if(notification.actionType == "UNFOLLOW") InlineButtonState.Stroke_Color else InlineButtonState.Default
                            )
                        }
                    }
                }
            }
        }
        if(isLoading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        if (showSnackBar) {
            CustomSnackBar(
                text = snackBarText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            )

            LaunchedEffect(snackBarText) {
                delay(2000)
                showSnackBar = false
            }
        }
    }
}