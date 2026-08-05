package com.inuappcenter.gravit.share.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.inuappcenter.gravit.R

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmer()
    )
}
@Composable
fun SkeletonCircle(
    modifier: Modifier = Modifier
) {
    SkeletonBox(
        modifier = modifier,
        shape = CircleShape
    )
}
@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    width: Dp,
    height: Dp = 14.dp
) {
    SkeletonBox(
        modifier = modifier
            .width(width)
            .height(height),
        shape = RoundedCornerShape(4.dp)
    )
}

@Composable
fun TopBarSkeleton(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .height(48.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.chevron_left),
            contentDescription = "뒤로가기",
            tint = AppColor.icon_default,
            modifier = Modifier.clickable{navController.popBackStack()}
        )

    }
}
@Composable
fun Modifier.shimmer(): Modifier {
    val transition = rememberInfiniteTransition(label = "")

    val translateAnim = transition.animateFloat(
        initialValue = -300f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            )
        ),
        label = ""
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE5E5E5),
            Color(0xFFF5F5F5),
            Color(0xFFE5E5E5)
        ),
        start = Offset(translateAnim.value, 0f),
        end = Offset(translateAnim.value + 300f, 300f)
    )

    return this.background(brush)
}