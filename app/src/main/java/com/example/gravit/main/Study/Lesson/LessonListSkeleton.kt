package com.inuappcenter.gravit.main.Study.Lesson

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.inuappcenter.gravit.share.skeleton.SkeletonBox
import com.inuappcenter.gravit.share.skeleton.TopBarSkeleton
import com.example.gravit.ui.theme.AppColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.R

@Composable
fun LessonListSkeletonUI(
    navController: NavController
) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg0)
    ) {
        TopBarSkeleton(
            navController = navController
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(AppColor.bg1)
        ) {
            Image(
                painter = painterResource(id = R.drawable.unitlesson_back),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 20.dp
                    )
            ) {
                SkeletonBox(
                    modifier = Modifier
                        .width(140.dp)
                        .height(28.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(67.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0),
                    contentAlignment = Alignment.CenterStart
                ){
                    SkeletonBox(
                        Modifier
                            .fillMaxWidth(.5f)
                            .height(30.dp)
                            .padding(start = 16.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(156.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColor.bg0)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ){
                        SkeletonBox(
                            Modifier
                                .fillMaxWidth(.5f)
                                .height(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SkeletonBox(
                            Modifier
                                .fillMaxWidth(.8f)
                                .height(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(156.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColor.bg0)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ){
                        SkeletonBox(
                            Modifier
                                .fillMaxWidth(.5f)
                                .height(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SkeletonBox(
                            Modifier
                                .fillMaxWidth(.8f)
                                .height(10.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
                    ) {
                        item{
                            SkeletonBox(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = .3f)
                                    .height(12.dp)
                            )
                        }
                        items(3) {
                            SkeletonBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(59.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}