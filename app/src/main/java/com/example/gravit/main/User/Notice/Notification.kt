package com.example.gravit.main.User.Notice

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.InlineButton
import com.example.gravit.ui.theme.InlineButtonState
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.main.User.TopBar
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
    data class NotificationItem(
        val id: Int,
        val type: String,
        val message: String,
        val actionType: String,
        val targetId: Int,
        val read: Boolean,
        val createdAt: String
    )

    val notification = listOf(
        NotificationItem(
            id = 101,
            type = "FOLLOW",
            message = "홍길동님이 나를 팔로우했어요! 👀",
            actionType = "FOLLOW_BACK",
            targetId = 42,
            read = false,
            createdAt = "2025-09-25T10:00:00"
        ),
        NotificationItem(
            id = 100,
            type = "FOLLOW",
            message = "김철수님이 나를 팔로우했어요! 👀",
            actionType = "UNFOLLOW",
            targetId = 37,
            read = true,
            createdAt = "2025-09-24T08:30:00"
        ),
        NotificationItem(
            id = 99,
            type = "FRIEND_ACTIVITY",
            message = "이영희님이 OS행성을 정복했어요! 🌍",
            actionType = "CONGRATULATE",
            targetId = 55,
            read = false,
            createdAt = "2025-09-23T20:15:00"
        )
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg2),
    ) {
        item {
            TopBar(navController = navController, title = "알림", useCloseIcon = false, height = 48.dp)
        }
        item {
            Text(
                text = dateText,
                style = AppTypography.Label2,
                color = PrimitiveColor.Gray500,
                modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
        items(notification) { notification ->
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .height(114.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, shape = RoundedCornerShape(8.dp), color = AppColor.divider1)
                    .padding(16.dp)
            ){
                Column(
                ) {
                    Text(
                        text = notification.message,
                        style = AppTypography.Label1,
                        color = AppColor.text1
                    )
                    Row(

                    ) {

                    }
                    Spacer(Modifier.weight(1f))
                    if(notification.actionType == "UNFOLLOW"){
                        InlineButton(
                            text = "팔로우 취소",
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            state = InlineButtonState.Stroke_Color,
                            style = AppTypography.Label2,
                            color = AppColor.CTA
                        )
                    }
                    else {
                        InlineButton(
                            text = if(notification.actionType == "FOLLOW_BACK") "맞팔로우" else "축하하기",
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            style = AppTypography.Label2,
                            color = AppColor.CTA_text
                        )
                    }

                }
            }
        }

    }
}