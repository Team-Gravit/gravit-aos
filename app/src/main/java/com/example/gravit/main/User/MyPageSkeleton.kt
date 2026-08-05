package com.inuappcenter.gravit.main.User

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inuappcenter.gravit.share.skeleton.SkeletonBox
import com.example.gravit.ui.theme.AppColor
import com.inuappcenter.gravit.R

@Composable
fun MyPageSkeletonUI(
    navController: NavController,
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg2)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 16.dp),
        userScrollEnabled = false
    ) {
        item {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mypage_bg),
                    contentDescription = "main back",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.height(70.dp)) {
                        SkeletonBox(
                            modifier = Modifier.size(70.dp),
                            shape = RoundedCornerShape(100f)
                        )
                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.padding(vertical = 7.5.dp)) {
                            Spacer(Modifier.height(8.dp))
                            SkeletonBox(
                                Modifier.size(60.dp, 20.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            SkeletonBox(
                                Modifier.size(80.dp, 20.dp)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.bell),
                            contentDescription = "bell",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    navController.navigate("user/notification")
                                },
                            tint = AppColor.icon_w
                        )
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.setting),
                            contentDescription = "setting",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    navController.navigate("user/setting")
                                },
                            tint = AppColor.icon_w

                        )
                    }
                    Spacer(Modifier.weight(1f))
                    SkeletonBox(
                        Modifier
                            .fillMaxWidth()
                            .height(37.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                SkeletonBox(
                    Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))
                SummaryTabSkeletonUI()
            }
        }
    }

}

@Composable
fun SummaryTabSkeletonUI(){
    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp),
        ) {
            Column {
                SkeletonBox(
                    modifier = Modifier
                        .width(140.dp)
                        .height(40.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(20.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp),
        ) {
            Column {
                SkeletonBox(
                    modifier = Modifier
                        .width(140.dp)
                        .height(40.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(20.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                )
            }
        }
    }
}