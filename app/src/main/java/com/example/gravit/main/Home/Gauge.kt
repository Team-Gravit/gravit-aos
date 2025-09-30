package com.example.gravit.main.Home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.ui.theme.pretendard
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import com.example.gravit.Responsive

@Composable
fun LevelGauge(
    lv: Int,
    xp: Int,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(Responsive.h(30f))
) {
    val levelRanges = listOf(
        0 to 99,
        100 to 199,
        200 to 399,
        400 to 699,
        700 to 1099,
        1100 to 1599,
        1600 to 2199,
        2200 to 2899,
        2900 to 3699,
        3700 to Int.MAX_VALUE // 마지막 레벨은 무한대 처리
    )

    val (startXp, endXp) = levelRanges.getOrNull(lv - 1) ?: (0 to 0)

    val isMaxLevel = lv >= 10

    val progress = when {
        isMaxLevel -> 1f
        endXp > startXp -> {
            val p = (xp - startXp).toFloat() / (endXp - startXp).toFloat()
            p.coerceIn(0f, 1f)
        }
        else -> 0f
    }

    val textColor = if (progress < 0.3f) Color.Black else Color.White
    val shape = RoundedCornerShape(Responsive.w(16f))

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .background(Color.White, shape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF8100B3), Color(0xFFDD00FF))
                    ),
                    shape = shape
                ),
        )

        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(
                    fontWeight = FontWeight.Normal
                )){
                    append("LV")
                }
                withStyle(SpanStyle(
                    fontWeight = FontWeight.Bold
                )){
                    append("${lv}")
                }
            },
            fontSize = Responsive.spH(14f),
            fontFamily = pretendard,
            color = textColor,
            modifier = Modifier
                .padding(start = Responsive.w(15f))
                .align(Alignment.CenterStart),

            )
    }
}

@Composable
fun LeagueGauge(
    xp: Int,
    modifier: Modifier = Modifier.size(64.dp)
) {
    val steps = intArrayOf(0, 100, 200, 400, 700, 1100, 1600, 2200, 2900, 3700)

    val idx = steps.indexOfLast { xp >= it }.coerceAtLeast(0)
    val start = steps[idx]
    val end = steps.getOrNull(idx + 1) ?: Int.MAX_VALUE

    val progress = if (end == Int.MAX_VALUE) {
        1f
    } else {
        ((xp - start).toFloat() / (end - start).toFloat()).coerceIn(0f, 1f)
    }

    Canvas(modifier = modifier) {
        val strokeWidth = 5.dp.toPx()
        val radius = size.minDimension / 2 - strokeWidth / 2

        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFDD00FF), Color(0xFF8100B3)),
            ),
            startAngle = -90f,   // 위에서 시작
            sweepAngle = -360f * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2
            )
        )
    }
}

@Composable
fun RoundedGauge(
    totalUnits: Int?,
    completedUnits: Int?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val ratio =
        if (totalUnits != null && completedUnits != null && totalUnits > 0) {
            if(completedUnits == 0){
                0.05f
            }else{
                completedUnits.toFloat() / totalUnits
            }
    } else {
        0f
    }

    Column(
        modifier = modifier
            .width(width)
            .wrapContentHeight()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFEEEEEE))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width * ratio)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFBA00FF))
            )
        }
        Spacer(modifier = Modifier.height(Responsive.h(3f)))
        Text(
            text = "$completedUnits/$totalUnits",
            fontSize = Responsive.spH(15f),
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}