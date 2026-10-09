package com.inuappcenter.gravit.main.Study.Problem

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.R

@Composable
fun LoadingScreen(){
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

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
    val randomTip = rememberSaveable { tips.random() }
    StatusBarStyle(darkIcons = true)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest
                    .Builder(context)
                    .data(R.drawable.loading)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Loading GIF",
                modifier = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Modifier.height(140.dp)
                } else {
                    Modifier.height(330.dp)
                },
                contentScale = ContentScale.Fit
            )

            Spacer(
                Modifier.height(
                    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                        8.dp
                    else
                        16.dp
                )
            )

            Text(
                text = "로딩중...",
                style = AppTypography.Heading1,
                color = AppColor.text1
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = randomTip,
                style = AppTypography.Body2_Normal,
                color = AppColor.text4,
                textAlign = TextAlign.Center
            )
        }
    }
}