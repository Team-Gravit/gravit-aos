package com.inuappcenter.gravit.main.League

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.R

@Composable
fun SeasonFinish(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "시즌 정보를 집계중이에요!",
            color = AppColor.text1w,
            style = AppTypography.Title3
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "이번 시즌도 수고했어요!",
            style = AppTypography.Label2,
            color = AppColor.text4
        )
        Spacer(Modifier.height(40.dp))
        Image(
            painter = painterResource(id = R.drawable.rabbit),
            contentDescription = null,
            modifier = Modifier.size(163.dp, 216.dp)
        )
    }
}