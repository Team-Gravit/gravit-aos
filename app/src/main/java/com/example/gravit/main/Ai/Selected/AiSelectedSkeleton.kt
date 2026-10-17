package com.example.gravit.main.Ai.Selected

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppRadius
import com.example.gravit.ui.theme.AppSpacing
import com.inuappcenter.gravit.share.skeleton.SkeletonBox
import com.inuappcenter.gravit.share.skeleton.SkeletonText

@Composable
fun CsTopicsSkeleton() {
    repeat(5) { index ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .clip(RoundedCornerShape(AppRadius.lg))
                .background(AppColor.bg0)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier.size(16.dp),
                shape = RoundedCornerShape(2.dp)
            )

            Spacer(Modifier.width(12.dp))

            SkeletonText(
                width = when (index) {
                    0 -> 92.dp
                    1 -> 124.dp
                    2 -> 105.dp
                    3 -> 136.dp
                    else -> 112.dp
                },
                height = 16.dp
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}
@Composable
fun StackGroupsSkeleton() {
    repeat(4) { index ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppRadius.lg))
                .background(AppColor.bg0)
                .padding(AppSpacing.spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(Modifier.width(AppSpacing.spacing12))

            SkeletonText(
                width = when (index) {
                    0 -> 72.dp
                    1 -> 96.dp
                    2 -> 84.dp
                    else -> 76.dp
                },
                height = 16.dp
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}
@Composable
fun StacksSkeleton() {
    repeat(4) { index ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppRadius.lg))
                .background(AppColor.bg0)
                .padding(AppSpacing.spacing16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(Modifier.width(8.dp))

            SkeletonBox(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(Modifier.width(12.dp))

            SkeletonText(
                width = when (index) {
                    0 -> 116.dp
                    1 -> 138.dp
                    2 -> 104.dp
                    else -> 126.dp
                },
                height = 16.dp
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}