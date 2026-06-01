package com.example.gravit.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

enum class CipState {
    Default,
    Active,
    Disabled
}


@Composable
fun Cip(
    text: String? = "rkawk",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: CipState = CipState.Default,
    style: TextStyle,
) {

    val containerColor = when {
        state == CipState.Disabled -> PrimitiveColor.Gray0
        state == CipState.Active -> AppColor.CTA
        else -> AppColor.bg1
    }

    val contentColor = when {
        state == CipState.Disabled -> AppColor.text4
        state == CipState.Active -> AppColor.text1w
        else -> AppColor.CTA
    }

    val borderColor = when {
        state == CipState.Disabled -> AppColor.divider2
        state == CipState.Active -> Color.Transparent
        else -> AppColor.CTA
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (text != null) {
            Text(
                text = text,
                style = style
            )
        }
    }
}