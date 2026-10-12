package com.inuappcenter.gravit.main.Study.Lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gravit.ui.theme.AppColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.share.skeleton.SkeletonBox
import com.inuappcenter.gravit.share.skeleton.SkeletonText

@Composable
fun LessonCompleteSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg1)
    ) {
        Image(
            painter = painterResource(id = R.drawable.results_back),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
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
                        Box(modifier = Modifier.weight(1f) )

                        Spacer(Modifier.width(16.dp))

                        SkeletonBox(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp),
                            shape = RoundedCornerShape(0.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Box(modifier = Modifier.weight(1f) )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun LessonResultSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SkeletonText(
            width = 50.dp,
            height = 20.dp
        )

        Spacer(Modifier.height(6.dp))

        SkeletonText(
            width = 45.dp,
            height = 14.dp
        )
    }
}