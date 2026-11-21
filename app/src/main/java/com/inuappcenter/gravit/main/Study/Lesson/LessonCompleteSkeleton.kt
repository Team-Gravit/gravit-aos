package com.inuappcenter.gravit.main.Study.Lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.inuappcenter.gravit.ui.theme.AppColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.share.skeleton.SkeletonBox
import com.inuappcenter.gravit.share.skeleton.SkeletonText

@Composable
fun LessonCompleteSkeleton() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg1)
            .navigationBarsPadding()
    ) {
        val designWidth = 420.dp
        val designHeight = 740.dp
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val widthScale = screenWidth / designWidth
        val heightScale = screenHeight / designHeight
        val contentScale = minOf(widthScale, heightScale).coerceIn(1f, 1.5f)
        val scaledWidth = designWidth * contentScale

        Image(
            painter = painterResource(R.drawable.lesson_result_bg_mobile),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(scaledWidth)
                    .height(maxOf(screenHeight, designHeight * contentScale))
            ) {
                Image(
                    painter = painterResource(R.drawable.lesson_result_rabbit),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = 1.5f
                            scaleY = 1.5f
                            translationY = 24.dp.toPx()
                        },
                    contentScale = ContentScale.FillWidth
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.statusBars.asPaddingValues())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.weight(1f))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(4.dp))

                        SkeletonBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            shape = RoundedCornerShape(4.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColor.bg0),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f))
                                Spacer(Modifier.width(16.dp))
                                SkeletonBox(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(0.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Box(modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            SkeletonBox(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(45.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            SkeletonBox(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(45.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonResultSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SkeletonText(width = 50.dp, height = 20.dp)
        Spacer(Modifier.height(6.dp))
        SkeletonText(width = 45.dp, height = 14.dp)
    }
}
