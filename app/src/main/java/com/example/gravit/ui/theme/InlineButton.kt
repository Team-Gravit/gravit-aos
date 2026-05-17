package com.example.gravit.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.inuappcenter.gravit.R

enum class InlineButtonState {
    Default,
    Secondary,
    Stroke,
    Stroke_Color
}

enum class InlineButtonSize {
    XS,
    S,
    Default,
    L
}

enum class InlineButtonIcon {
    L,
    R,
    LR,
    N
}

@Composable
fun InlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: InlineButtonState = InlineButtonState.Default,
    size: InlineButtonSize = InlineButtonSize.Default,
    icon: InlineButtonIcon = InlineButtonIcon.N
) {
    val height = when(size) {
        InlineButtonSize.Default -> 37.dp
        InlineButtonSize.XS -> 29.dp
        InlineButtonSize.S -> 37.dp
        InlineButtonSize.L -> 46.dp

    }

    val containerColor = when {
        state == InlineButtonState.Default -> AppColor.CTA
        state == InlineButtonState.Stroke -> AppColor.bg1
        state == InlineButtonState.Stroke_Color -> PrimitiveColor.Gray0
        state == InlineButtonState.Secondary -> AppColor.CTA_secondary
        else -> AppColor.CTA
    }

    val contentColor = when {
        state == InlineButtonState.Default -> AppColor.CTA_text
        state == InlineButtonState.Stroke -> AppColor.text3
        state == InlineButtonState.Stroke_Color -> AppColor.CTA
        state == InlineButtonState.Secondary -> AppColor.text3
        else -> AppColor.CTA_text
    }

    val borderColor = when {
        state == InlineButtonState.Stroke -> AppColor.divider2
        state == InlineButtonState.Stroke_Color -> AppColor.CTA
        else -> Color.Transparent
    }
    val style = when(size) {
       InlineButtonSize.XS -> AppTypography.App_Caption1
       InlineButtonSize.S -> AppTypography.App_Btn2
       InlineButtonSize.Default -> AppTypography.App_Btn1
       InlineButtonSize.L -> AppTypography.Web_Btn_L
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (icon) {
                InlineButtonIcon.L,
                InlineButtonIcon.LR -> {
                    Icon(
                        painter = painterResource(R.drawable.add_plus_lg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                else -> Unit
            }

            Text(
                text = text,
                style = style
            )

            when (icon) {
                InlineButtonIcon.R,
                InlineButtonIcon.LR -> {
                    Icon(
                        painter = painterResource(R.drawable.chevron_right),
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                else -> Unit
            }
        }
    }
}