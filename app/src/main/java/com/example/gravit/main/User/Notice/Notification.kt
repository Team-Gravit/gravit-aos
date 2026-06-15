package com.example.gravit.main.User.Notice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.main.User.FriendsRow
import com.inuappcenter.gravit.main.User.TopBar

@Composable
fun Notice2(
    navController: NavController,
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg2),
    ) {
        item {
            TopBar(navController = navController, title = "알림", useCloseIcon = false, height = 48.dp)
        }
        item {
            Column (
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "친구신청",
                            style = AppTypography.Label2,
                            color = AppColor.text3
                        )
                        FriendsRow()
                        FriendsRow()
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "전체알림",
                            style = AppTypography.Label2,
                            color = AppColor.text3
                        )

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(78.dp)
                                .padding(vertical = 20.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column() {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "연속학습이 꺠져요.",
                                        style = AppTypography.Label1,
                                        color = AppColor.text1
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "2시간 전",
                                        style = AppTypography.Caption1,
                                        color = AppColor.text4
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "다음날까지 2시간 남았어요.",
                                    style = AppTypography.Label2,
                                    color = AppColor.text3
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
                    }
                }
            }
        }

    }
}