package com.example.gravit.main.Chapter.Lesson

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard

@Composable
fun LoadingScreen(){
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(R.drawable.loading)
                .build(),
            imageLoader = imageLoader,
            contentDescription = "Loading GIF",
            modifier = Modifier.padding(bottom = 131.dp)
        )
        Column (
            modifier = Modifier.padding(top = 320.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Loading....",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = pretendard,
                color = Color(0xFF383838),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(61.dp))
            Text(
                text = "알고 게셨나요?",
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                fontSize = 15.sp,
                color = Color(0xFF494949),
            )
            Text(
                text ="문제가 이상이 있는 경우\n우측 상단의 신고 버튼으로 접수할 수 있어요.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = pretendard,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center
            )
        }
    }
}