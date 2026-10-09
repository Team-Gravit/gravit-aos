package com.example.gravit.share

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography

@Composable
fun PolicyHeading(text: String) {
    Text(
        text = text,
        style = AppTypography.Heading2,
        color = AppColor.text2
    )
    Spacer(Modifier.height(16.dp))
}

@Composable
fun PolicyHeadline(text: String) {
    Text(
        text = text,
        style = AppTypography.Headline2,
        color = AppColor.text2
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
fun PolicyBody(text: String) {
    Text(
        text = text,
        style = AppTypography.Body2_Reading,
        color = AppColor.text2
    )
}

@Composable
fun PolicySectionSpace() {
    Spacer(Modifier.height(20.dp))
}