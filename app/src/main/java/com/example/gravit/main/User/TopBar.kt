package com.inuappcenter.gravit.main.User

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R

@Composable
fun TopBar(
    navController: NavController,
    title: String,
    height: Dp = 80.dp,
    useCloseIcon: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .height(height)
            .background(Color.White)
    ) {

        if(useCloseIcon){
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .clickable {
                        navController.popBackStack()
                    },
                tint = Color(0xFF4D4D4D)
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.chevron_left),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .size(24.dp)
                    .clickable {
                        navController.popBackStack()
                    },
                tint = Color.Black
            )
        }

        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pretendard
                ),
                color = Color(0xFF222222),
            )
        }
    }

    HorizontalDivider(
        color = Color.Black.copy(alpha = 0.1f),
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
}