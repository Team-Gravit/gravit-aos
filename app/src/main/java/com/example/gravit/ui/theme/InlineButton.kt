package com.example.gravit.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class InlineButtonState {
    Default,
    Secondary,
    Stroke,
    Stroke_Color
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
    icon: InlineButtonIcon = InlineButtonIcon.N,
    style: TextStyle,
    color: Color,
    shape: Shape = RoundedCornerShape(8.dp),
    padding: Dp = 8.dp,
    iconAsset: Int? = null,
    iconColor: Color = AppColor.icon_default
) {
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

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(vertical = padding)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if ((icon == InlineButtonIcon.L || icon == InlineButtonIcon.LR) && iconAsset != null){
                Icon(
                    painter = painterResource(iconAsset),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(),
                    tint = iconColor
                )
            }
            Text(
                text = text,
                style = style,
                color = color
            )

            if ((icon == InlineButtonIcon.R || icon == InlineButtonIcon.LR) && iconAsset != null){
                Icon(
                    painter = painterResource(iconAsset),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(),
                    tint = iconColor
                )
            }
        }
    }
}