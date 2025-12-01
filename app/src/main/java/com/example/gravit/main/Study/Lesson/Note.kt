package com.example.gravit.main.Study.Lesson

import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch

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
        factory = { TextView(it).also { tv -> markwon.setMarkdown(tv, content) } },
        update = { view -> markwon.setMarkdown(view, content) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSheet(
    onDismiss: () -> Unit,
    title: String,
){
    val context = LocalContext.current
    val vm: NoteVM = viewModel(
        factory = NoteVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    val noteText = (ui as? NoteVM.UiState.Success)
        ?.data
        ?: "개념노트를 불러오지 못했습니다."

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        val isExpanded = sheetState.currentValue == SheetValue.Expanded

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = LocalConfiguration.current.screenHeightDp.dp * 0.9f)
        ) {
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
                    .let {
                        if (isExpanded) {
                            it
                                .fillMaxHeight(0.8f)
                        } else {
                            it
                                .height(252.dp)
                        }
                    }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent(PointerEventPass.Initial)
                            }
                        }
                    }
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                MarkdownText(noteText)
            }

        }
    }
}