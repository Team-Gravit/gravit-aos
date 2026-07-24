package com.example.gravit.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

enum class ButtonState {
    Default,
    Secondary,
    Stroke,
    Stroke_Secondary
}

@Composable
fun BlockButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: ButtonState = ButtonState.Default,
    style: TextStyle = AppTypography.Headline2,
){
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val containerColor = when {
        state == ButtonState.Default && isPressed -> AppColor.CTA_hover
        state == ButtonState.Default -> AppColor.CTA
        state == ButtonState.Secondary && isPressed -> AppColor.CTA_secondary_hover
        state == ButtonState.Secondary -> AppColor.CTA_secondary
        state == ButtonState.Stroke -> AppColor.bg0
        state == ButtonState.Stroke_Secondary -> AppColor.bg0
        else -> AppColor.CTA
    }

    val contentColor = when {
        state == ButtonState.Secondary -> AppColor.CTA_secondary_text
        state == ButtonState.Stroke -> AppColor.Main1
        state == ButtonState.Stroke_Secondary -> AppColor.text2
        else -> AppColor.CTA_text
    }
    val border = when {
        state == ButtonState.Stroke -> AppColor.divider1
        state == ButtonState.Stroke_Secondary -> AppColor.divider1
        else -> Color.Transparent
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = contentColor,
            containerColor = containerColor,
            disabledContainerColor = AppColor.CTA_disabled,
            disabledContentColor = AppColor.CTA_text
        ),
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = text,
            style = style,
        )
    }
}