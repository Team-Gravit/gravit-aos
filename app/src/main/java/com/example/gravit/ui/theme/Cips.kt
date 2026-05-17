package com.example.gravit.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class CipState {
    Default,
    Active,
}

enum class CipSize {
    XS,
    S,
    Default
}

@Composable
fun Cip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: CipState = CipState.Default,
    size: CipSize = CipSize.Default,
) {
    val height = when(size) {
        CipSize.Default -> 32.dp
        CipSize.S -> 29.dp
        CipSize.XS -> 22.dp
    }

    val containerColor = when {
        !enabled -> PrimitiveColor.Gray0
        state == CipState.Active -> AppColor.CTA
        else -> AppColor.bg1
    }

    val contentColor = when {
        !enabled -> AppColor.text4
        state == CipState.Active -> AppColor.text1w
        else -> AppColor.CTA
    }

    val borderColor = when {
        !enabled -> AppColor.divider2
        state == CipState.Active -> Color.Transparent
        else -> AppColor.CTA
    }
    val style = when(size) {
        CipSize.S -> AppTypography.Web_Caption2
        CipSize.XS -> AppTypography.App_Caption2
        CipSize.Default -> AppTypography.Web_Btn_S_Caption1
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        enabled = enabled,
        shape = RoundedCornerShape(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor),
    ) {
        Text(
            text = text,
            style = style
        )
    }
}