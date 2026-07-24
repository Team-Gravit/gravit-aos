package com.inuappcenter.gravit.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography

@Composable
fun TabBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    tab1: String,
    tab2: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(41.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 16.dp)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(
                text = tab1,
                selected = selectedIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )

            TabItem(
                text = tab2,
                selected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(AppColor.divider1)
        )

        Box(
            modifier = Modifier
                .align(
                    if (selectedIndex == 0) {
                        Alignment.BottomStart
                    } else {
                        Alignment.BottomEnd
                    }
                )
                .padding(horizontal = 16.dp)
                .fillMaxWidth(0.5f)
                .height(2.dp)
                .background(AppColor.Main2)
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.Label1,
            color = if (selected) AppColor.Main2 else AppColor.text4
        )
    }
}