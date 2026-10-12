package com.inuappcenter.gravit.main.User

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
                    .background(Color(0xFF1D0C58))
            ) {
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
fun LearningTabSkeletonUI() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 일별 완료한 레슨 수
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(430.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp)
        ) {
            Column {
                SkeletonBox(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    repeat(7) { index ->
                        Column(
                            modifier = Modifier
                                .height(160.dp)
                                .width(34.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.weight(1f))

                            SkeletonBox(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(12.dp)
                            )

                            Spacer(Modifier.height(4.dp))

                            SkeletonBox(
                                modifier = Modifier
                                    .width(34.dp)
                                    .height((30 + index * 10).dp),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(Modifier.height(4.dp))

                            SkeletonBox(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(12.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                SkeletonBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(16.dp)
                )

                Spacer(Modifier.height(8.dp))

                SkeletonBox(
                    modifier = Modifier
                        .width(45.dp)
                        .height(24.dp)
                )

                Spacer(Modifier.height(16.dp))

                repeat(3) {
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(4.dp)
                    )

                    if (it != 2) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        // 이번 주 가장 많이 푼 챕터
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp)
        ) {
            Column {
                SkeletonBox(
                    modifier = Modifier
                        .width(150.dp)
                        .height(16.dp)
                )

                Spacer(Modifier.height(8.dp))

                SkeletonBox(
                    modifier = Modifier
                        .width(180.dp)
                        .height(20.dp)
                )

                Spacer(Modifier.height(16.dp))

                repeat(2) {
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (it != 1) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        // 취약 개념 TOP7
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp)
        ) {
            Column {
                SkeletonBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(16.dp)
                )

                Spacer(Modifier.height(16.dp))

                repeat(3) {
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (it != 2) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LeagueTabSkeletonUI() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.bg0)
            .padding(16.dp)
    ) {
        Column {
            SkeletonBox(
                modifier = Modifier
                    .width(130.dp)
                    .height(16.dp)
            )

            Spacer(Modifier.height(8.dp))

            SkeletonBox(
                modifier = Modifier
                    .width(160.dp)
                    .height(20.dp)
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SkeletonBox(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                        )

                        Spacer(Modifier.height(4.dp))

                        SkeletonBox(
                            modifier = Modifier
                                .width(65.dp)
                                .height(12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun SocialTabSkeletonUI() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 팔로우 / 팔로잉
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ){}
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp)
        ) {
            Column {
                SkeletonBox(
                    modifier = Modifier
                        .width(70.dp)
                        .height(16.dp)
                )

                Spacer(Modifier.height(8.dp))

                SkeletonBox(
                    modifier = Modifier
                        .width(180.dp)
                        .height(20.dp)
                )

                Spacer(Modifier.height(16.dp))

                repeat(2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(78.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SkeletonBox(
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(100f)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {
                            SkeletonBox(
                                modifier = Modifier
                                    .width(90.dp)
                                    .height(16.dp)
                            )

                            Spacer(Modifier.height(6.dp))

                            SkeletonBox(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(12.dp)
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        SkeletonBox(
                            modifier = Modifier
                                .width(77.dp)
                                .height(32.dp),
                            shape = RoundedCornerShape(4.dp)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(256.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(vertical = 16.dp)
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    SkeletonBox(
                        modifier = Modifier
                            .width(70.dp)
                            .height(16.dp)
                    )

                    Spacer(Modifier.height(6.dp))

                    SkeletonBox(
                        modifier = Modifier
                            .width(150.dp)
                            .height(20.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .width(144.dp)
                                .height(165.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(AppColor.bg1)
                                .padding(
                                    vertical = 12.dp,
                                    horizontal = 20.dp
                                )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                SkeletonBox(
                                    modifier = Modifier.size(52.dp),
                                    shape = RoundedCornerShape(100f)
                                )

                                Spacer(Modifier.height(8.dp))

                                SkeletonBox(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(16.dp)
                                )

                                Spacer(Modifier.height(4.dp))

                                SkeletonBox(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(12.dp)
                                )

                                Spacer(Modifier.weight(1f))

                                SkeletonBox(
                                    modifier = Modifier
                                        .width(104.dp)
                                        .height(32.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )
                            }
                        }
                    }
                }
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