package com.example.gravit.main.Study.Problem

import android.os.Build
import androidx.compose.foundation.background
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
    val tips = listOf(
        "문제가 이상이 있는 경우\n우측 상단의 신고 버튼으로 접수할 수 있어요.",
        "북마크하고 싶은 문제가 있다면,\n화면 좌측 상단에 있는 책갈피 모양 아이콘을 눌러\n쉽게 북마크할 수 있어요.",
        "마스코드 그래빗은 원래 흰색 토끼였지만,\n태양풍을 맞아 노랗게 변했답니다.",
        "같은 레슨을 반복해서 풀면,\nXP와 LP는 추가로 지급되지 않으니 참고해주세요.",
        "리그는 매주 일요일 자정에 초기화되어\n새로운 순위 경쟁이 시작돼요."
    )
    val randomTip = remember { tips.random() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
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
            Spacer(Modifier.height(8.dp))
            Text(
                text = randomTip,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = pretendard,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center
            )
        }
    }
}