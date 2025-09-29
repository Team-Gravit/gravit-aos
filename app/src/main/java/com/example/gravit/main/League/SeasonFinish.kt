package com.example.gravit.main.League

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.ui.theme.mbc1961
import com.example.gravit.ui.theme.pretendard
import com.example.gravit.R

@Composable
fun SeasonFinish(
    seasonName: String?,
){
    val shadow = with(LocalDensity.current) {
        androidx.compose.ui.graphics.Shadow(
            color = Color.Black.copy(alpha = 0.25f),
            offset = Offset(0f, 1.46.dp.toPx()),
            blurRadius = 1.46.dp.toPx()
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${seasonName} 종료",
                    color = Color(0xFF8A00B8),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = mbc1961,
                        shadow = shadow
                    ),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "시즌 정보를 집계중이에요!",
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF6D6D6D)
                )
            }
        }
        Spacer(Modifier.height(25.dp))
        Image(
            painter = painterResource(id = R.drawable.rabbit),
            contentDescription = null,
        )
    }
}