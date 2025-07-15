package com.example.gravit

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.ui.theme.pretendard
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen() {
    val dateFormat = remember { SimpleDateFormat("yyyy. MM. dd (E)", Locale.KOREAN) }
    val today = remember { dateFormat.format(Date()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF2F2F2))
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_back),
            contentDescription = "main back",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
        )

        Column {
            Image(
                painter = painterResource(id = R.drawable.gravit_typo),
                contentDescription = "gravit typo",
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(360.dp, 70.dp)
            )
            Spacer(modifier = Modifier.height(60.dp))

            Box (
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "어서오세요,\n땅콩님!",
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Text(
                    text = today,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.BottomEnd),
                    color = Color.White
                )
            }
        }

    }
}



@Preview(showBackground = true)
@Composable
fun eview() {
   MainScreen()
}