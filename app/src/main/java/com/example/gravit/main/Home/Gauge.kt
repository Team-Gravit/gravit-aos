package com.example.gravit.main.Home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
@Composable
fun LevelGauge(
    lv: Int,
    xp: Int,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(30.dp)
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
    val shape = RoundedCornerShape(16.dp)

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
            fontSize = 14.sp,
            fontFamily = pretendard,
            color = textColor,
            modifier = Modifier
                .padding(start = 15.dp)
                .align(Alignment.CenterStart),

            )
    }
}

@Composable
fun LeagueGauge(
    leagueId: Int,
    lp: Int,
    modifier: Modifier = Modifier.size(64.dp) // 프로필 크기에 맞게 조절
) {
    val leagueRanges = listOf(
        0 to 100,
        101 to 200,
        201 to 320,
        321 to 460,
        461 to 620,
        621 to 800,
        801 to 1000,
        1001 to 1220,
        1221 to 1460,
        1461 to 1720,
        1721 to 2000,
        2001 to 2300,
        2301 to 2620,
        2621 to 2960,
        2961 to Int.MAX_VALUE // 마지막은 만렙
    )

    val (start, end) = leagueRanges.getOrNull(leagueId - 1) ?: (0 to 0)
    val progress = when {
        end == Int.MAX_VALUE -> 1f
        end > start -> ((lp - start).toFloat() / (end - start).toFloat()).coerceIn(0f, 1f)
        else -> 0f
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