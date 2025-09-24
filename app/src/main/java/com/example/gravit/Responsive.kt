package com.example.gravit

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.compositionLocalOf


// 디자인 기준 사이즈를 담는 데이터 클래스
data class DesignSpec(val width: Float, val height: Float)

// 기본 아트보드 = 360 x 740
val LocalDesignSpec = compositionLocalOf { DesignSpec(360f, 740f) }

object Responsive {
    @Composable
    fun w(dp: Float): Dp {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val baseW = LocalDesignSpec.current.width
        return (screenWidth / baseW * dp).dp
    }

    @Composable
    fun h(dp: Float): Dp {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val baseH = LocalDesignSpec.current.height
        return (screenHeight / baseH * dp).dp
    }

    @Composable
    fun spW(size: Float): TextUnit {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val baseW = LocalDesignSpec.current.width
        return (screenWidth / baseW * size).sp
    }

    @Composable
    fun spH(size: Float): TextUnit {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val baseH = LocalDesignSpec.current.height
        return (screenHeight / baseH * size).sp
    }
}
