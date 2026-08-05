package com.inuappcenter.gravit.main.Home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.inuappcenter.gravit.share.skeleton.SkeletonBox
import com.inuappcenter.gravit.share.skeleton.SkeletonText
import com.example.gravit.ui.theme.AppColor
import com.inuappcenter.gravit.R

@Composable
fun HomeSkeletonUI() {

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(AppColor.bg2)
    ) {

        item {

            Box(
                Modifier
                    .fillMaxSize()
                    .background(AppColor.bg2)
            ) {

                Image(
                    painter = painterResource(R.drawable.main_back),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Column(
                    Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {
                    Row {

                        Box(
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )

                        Spacer(Modifier.width(10.dp))

                        SkeletonBox(
                            Modifier
                                .height(18.dp)
                                .width(40.dp)
                        )

                        Spacer(Modifier.width(24.dp))

                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )

                        Spacer(Modifier.width(10.dp))

                        SkeletonBox(
                            Modifier
                                .height(18.dp)
                                .width(70.dp)
                        )
                    }

                    Spacer(Modifier.height(90.dp))

                    Box(
                        Modifier
                            .height(30.dp)
                            .fillMaxWidth(.6f)
                    )


                    Spacer(Modifier.height(10.dp))

                    Box(
                        Modifier
                            .height(15.dp)
                            .fillMaxWidth(.5f)
                    )

                    Spacer(Modifier.height(20.dp))

                    SkeletonStreakCard()

                    Spacer(Modifier.height(16.dp))

                    Row {

                        SkeletonMissionCard(
                            Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(16.dp))

                        SkeletonMissionCard(
                            Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    SkeletonPreviousCard()

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun SkeletonStreakCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.bg0)
            .padding(16.dp)
    ) {
        SkeletonText(
            width = 80.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        SkeletonText(
            width = 100.dp,
            height = 30.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(7) {
                SkeletonBox(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(6.dp)
                )
            }
        }
    }
}
@Composable
private fun SkeletonMissionCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(156.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.bg0)
            .padding(16.dp)
    ) {
        SkeletonText(
            width = 60.dp
        )

        Spacer(modifier = Modifier.height(10.dp))

        SkeletonBox(
            modifier = Modifier
                .height(22.dp)
                .fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SkeletonText(width = 70.dp)

        Spacer(modifier = Modifier.weight(1f))

        SkeletonBox(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(4.dp)
        )
    }
}
@Composable
private fun SkeletonPreviousCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.bg0)
            .padding(16.dp)
    ) {
        SkeletonText(
            width = 120.dp,
            height = 16.dp
        )

        Spacer(modifier = Modifier.height(18.dp))

        SkeletonBox(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(4.dp)
        )
    }
}