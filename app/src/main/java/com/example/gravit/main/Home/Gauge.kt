package com.inuappcenter.gravit.main.Home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp

private val levelXpSteps = intArrayOf(0, 100, 200, 400, 700, 1100, 1600, 2200, 2900, 3700)

fun calculateXpProgress(xp: Int): Double {
    val safeXp = xp.coerceAtLeast(0)

    val index = levelXpSteps
        .indexOfLast { safeXp >= it }
        .coerceAtLeast(0)

    val startXp = levelXpSteps[index]
    val endXp = levelXpSteps.getOrNull(index + 1)
        ?: return 100.0

    return ((safeXp - startXp).toDouble() / (endXp - startXp).toDouble() * 100.0)
        .coerceIn(0.0, 100.0)
}

@Composable
fun LeagueGauge(
    progress: Double,
    modifier: Modifier = Modifier.size(32.dp)
) {
    val safeProgress = progress.coerceIn(0.0, 1.0)

    Canvas(modifier = modifier) {
        val strokeWidth = 1.6.dp.toPx()
        val radius = size.minDimension / 2 - strokeWidth / 2

        val arcSize = Size(
            width = radius * 2,
            height = radius * 2
        )

        val topLeft = Offset(
            x = (size.width - radius * 2) / 2,
            y = (size.height - radius * 2) / 2
        )

        //흰색 원
        drawArc(
            color = Color.White,
            startAngle = -90f,
            sweepAngle = -360f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Butt
            ),
            size = arcSize,
            topLeft = topLeft
        )

        //보라색 그라디언트
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFDD00FF),
                    Color(0xFF8100B3)
                )
            ),
            startAngle = -90f,
            sweepAngle = -360f * safeProgress.toFloat(),
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            ),
            size = arcSize,
            topLeft = topLeft
        )
    }
}

@Composable
fun RoundedGauge(
    rate: Double,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    val ratio = (rate.coerceIn(0.0, 100.0) / 100.0).toFloat()

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(color)
    ) {
        if (ratio > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(ratio)
                    .clip(RoundedCornerShape(50))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF8100B3),
                                Color(0xFFDD00FF)
                            )
                        )
                    )
            )
        }
    }
}