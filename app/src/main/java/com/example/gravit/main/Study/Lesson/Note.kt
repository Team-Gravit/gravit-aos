package com.example.gravit.main.Study.Lesson

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.pretendard
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.image.network.NetworkSchemeHandler

@Composable
fun MarkdownText(content: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val httpLogger = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }
    val ok = okhttp3.OkHttpClient.Builder()
        .addInterceptor(httpLogger)
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .header("Accept", "image/png,image/jpeg,*/*")
                .build()
            chain.proceed(req)
        }
        .build()

    val imageLoader = ImageLoader.Builder(context)
        .okHttpClient(ok)
        .logger(coil.util.DebugLogger())
        .allowHardware(false)
        .components {
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                add(coil.decode.ImageDecoderDecoder.Factory())
            } else {
                add(coil.decode.GifDecoder.Factory())
            }
            add(coil.decode.SvgDecoder.Factory())
        }
        .build()

    val markwon = Markwon.builder(context)
        .usePlugin(HtmlPlugin.create())
        .usePlugin(
            ImagesPlugin.create { p ->
                p.addSchemeHandler(NetworkSchemeHandler.create())
            }
        )
        .usePlugin(CoilImagesPlugin.create(context, imageLoader))
        .usePlugin(TablePlugin.create(context))
        .build()

    AndroidView(
        modifier = modifier,
        factory = { TextView(it).also { tv ->
            tv.setTextColor(android.graphics.Color.BLACK)
            markwon.setMarkdown(tv, content)
            } },
        update = { view ->
            view.setTextColor(android.graphics.Color.BLACK)
            markwon.setMarkdown(view, content)
        }
    )
}

private fun replaceEng(chapter: String): String = when(chapter){
    "자료구조" -> "data-structure"
    "알고리즘" -> "algorithm"
    "네트워크" -> "network"
    else -> "unknown"
}
enum class SheetLevel { Hidden, Half, Full }
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteSheetM2(
    scaffoldState: BottomSheetScaffoldState,
    onDismiss: () -> Unit,
    chapter: String,
    unit: String,
    title: String,
) {
    val context = LocalContext.current
    val vm: NoteVM = viewModel(
        factory = NoteVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.load(replaceEng(chapter), unit.lowercase().replace(" ", ""))
    }

    val noteText = (ui as? NoteVM.UiState.Success)?.data
        ?: "개념노트를 불러오지 못했습니다."

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current

    val maxSheetHeightDp = screenHeight * 0.82f
    val maxSheetHeightPx = with(density) { maxSheetHeightDp.toPx() }

    val swipeableState = rememberSwipeableState(initialValue = SheetLevel.Half)

    val hiddenOffsetPx = maxSheetHeightPx
    val halfOffsetPx = maxSheetHeightPx / 2.8f
    val fullOffsetPx = 0f

    val anchors = mapOf(
        hiddenOffsetPx to SheetLevel.Hidden,
        halfOffsetPx to SheetLevel.Half,
        fullOffsetPx to SheetLevel.Full
    )

    LaunchedEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue == SheetLevel.Hidden) {
            onDismiss()
        }
    }
    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue == BottomSheetValue.Expanded) {
            swipeableState.snapTo(SheetLevel.Half)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 바텀시트
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxSheetHeightDp)
                .align(Alignment.BottomCenter)
                .offset {
                    IntOffset(
                        x = 0,
                        y = swipeableState.offset.value.toInt()
                    )
                }
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Vertical,
                        reverseDirection = false
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFC4C4C4))
                )
            }

            Text(
                text = "개념노트",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = pretendard,
                color = Color(0xFF222124),
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                color = Color(0xFF6D6D6D),
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(
                color = Color(0xFFA8A8A8),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                MarkdownText(noteText)
            }
        }
    }
}