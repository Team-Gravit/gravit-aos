package com.example.gravit.main.User.Setting

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.pretendard

@Composable
fun ToS(navController: NavController){

    Text(
        text = "개인정보 처리약관!!!!!!!",
        fontSize = 20.sp,
        color = Color.Yellow,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard
    )

}