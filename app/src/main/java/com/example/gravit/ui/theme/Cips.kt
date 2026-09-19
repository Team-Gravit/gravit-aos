package com.example.gravit.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    if(onClick == {}){
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(60.dp))
                .background(containerColor)
                .border(1.dp, borderColor, RoundedCornerShape(60.dp))
                .clickable(enabled, onClick = onClick)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (text != null) {
                Text(
                    text = text,
                    color = contentColor,
                    style = style
                )
            }
        }
    }else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(60.dp))
                .background(containerColor)
                .border(1.dp, borderColor, RoundedCornerShape(60.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (text != null) {
                Text(
                    text = text,
                    color = contentColor,
                    style = style
                )
            }
        }
    }

}