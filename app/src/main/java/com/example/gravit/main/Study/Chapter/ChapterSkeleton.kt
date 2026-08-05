package com.inuappcenter.gravit.main.Study.Chapter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gravit.share.skeleton.SkeletonBox

@Composable
fun ChapterSkeletonUI() {

    Column(
        modifier = Modifier
            .background(Color(0xFFF2F2F2))
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        repeat(4) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                ChapterSkeletonCard(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(160f / 166f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                ChapterSkeletonCard(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(160f / 166f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ChapterSkeletonCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        SkeletonBox(
            modifier = Modifier
                .width(60.dp)
                .height(25.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}