package com.example.gravit.main.Home

import android.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.ui.theme.pretendard

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



