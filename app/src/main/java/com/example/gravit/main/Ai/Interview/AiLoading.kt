package com.inuappcenter.gravit.main.Ai.Interview

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.inuappcenter.gravit.R
import kotlinx.coroutines.delay

@Composable
fun AiLoading1(){
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
                text = "답변을 텍스트로 바꾸고 있어요",
                style = AppTypography.Heading1,
                color = AppColor.text1
            )
        }
    }
}

@Composable
fun AiLoading2() {
    val configuration = LocalConfiguration.current

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
            Image(
                painter = painterResource(id = R.drawable.ai_loading_2),
                contentDescription = null,
                modifier = if (
                    configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                ) {
                    Modifier.height(175.dp)
                } else {
                    Modifier.height(260.dp)
                },
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(20.dp))

            InterviewLoadingMessage()
        }
    }
}
@Composable
fun InterviewLoadingMessage() {
    val messages = remember {
        listOf(
            "면접관이 알 수 없는 표정을 짓고 있습니다",
            "면접관이 답변을 두 번 다시 읽고 있습니다",
            "면접관이 메모지에 뭔가 적고 있습니다",
            "면접관이 고개를 갸웃거리고 있습니다",
            "면접관이 팔짱을 끼고 생각 중입니다"
        )
    }

    var messageIndex by remember { mutableIntStateOf(messages.indices.random()) }
    var dotCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)

            if (dotCount < 3) {
                dotCount++
            } else {
                var nextIndex: Int

                do {
                    nextIndex = messages.indices.random()
                } while (nextIndex == messageIndex)

                messageIndex = nextIndex
                dotCount = 0
            }
        }
    }

    Text(
        text = messages[messageIndex] + ".".repeat(dotCount),
        style = AppTypography.Heading1,
        color = AppColor.text1,
        textAlign = TextAlign.Center,
        softWrap = true,
        maxLines = Int.MAX_VALUE,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    )
}