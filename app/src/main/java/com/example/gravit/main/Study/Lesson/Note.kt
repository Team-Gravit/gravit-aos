package com.inuappcenter.gravit.main.Study.Lesson

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppRadius
import com.example.gravit.ui.theme.AppSpacing
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.User.TopBar
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.image.network.NetworkSchemeHandler
data class MarkdownBlock(
    val text: String? = null,
    val imageUrl: String? = null
)

fun parseMarkdownBlocks(markdown: String): List<MarkdownBlock> {
    val imageRegex = Regex(
        """<img[^>]*src=["']([^"']+)["'][^>]*>|!\[[^\]]*]\(([^)]+)\)""",
        RegexOption.IGNORE_CASE
    )

    val blocks = mutableListOf<MarkdownBlock>()
    var lastIndex = 0

    imageRegex.findAll(markdown).forEach { match ->
        val beforeText = markdown.substring(lastIndex, match.range.first)
        if (beforeText.isNotBlank()) {
            blocks.add(MarkdownBlock(text = beforeText))
        }

        val htmlImgUrl = match.groups[1]?.value
        val markdownImgUrl = match.groups[2]?.value
        val imageUrl = htmlImgUrl ?: markdownImgUrl

        if (!imageUrl.isNullOrBlank()) {
            blocks.add(MarkdownBlock(imageUrl = imageUrl))
        }

        lastIndex = match.range.last + 1
    }

    val remainText = markdown.substring(lastIndex)
    if (remainText.isNotBlank()) {
        blocks.add(MarkdownBlock(text = remainText))
    }

    return blocks
}
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
        factory = { context ->
            TextView(context).also { tv ->
                tv.setTextColor(android.graphics.Color.BLACK)
                tv.setHorizontallyScrolling(false)
                tv.setSingleLine(false)

                tv.post {
                    markwon.setMarkdown(tv, content)
                }
            }
        },
        update = { tv ->
            tv.setTextColor(android.graphics.Color.BLACK)
            tv.post {
                markwon.setMarkdown(tv, content)
            }
        }
    )
}@Composable
fun MarkdownContent(content: String) {
    val blocks = remember(content) {
        parseMarkdownBlocks(content)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        blocks.forEach { block ->
            when {
                block.text != null -> {
                    MarkdownText(
                        content = block.text,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                block.imageUrl != null -> {
                    AsyncImage(
                        model = block.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
    }
}

@Composable
fun NotePage(
    title: String,
    unitId: Long,
    navController: NavController,
    onSessionExpired: () -> Unit,
) {
    val context = LocalContext.current
    val vm: NoteVM = viewModel(factory = NoteVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()

    LaunchedEffect(unitId) {
        vm.load(unitId)
    }

    val noteText = (ui as? NoteVM.UiState.Success)?.data
        ?: "개념노트를 불러오지 못했습니다."
    StatusBarStyle(darkIcons = true)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg0)
    ){
        Column{
            TopBar(
                title = "개념노트",
                icon = painterResource(id = R.drawable.close),
                navController = navController
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 20.dp, horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(AppRadius.lg))
                        .background(AppColor.bg2)
                        .padding(AppSpacing.spacing16)
                ){
                    Column {
                        Text(
                            text = title,
                            style = AppTypography.Label1,
                            color = Color(0xFF555555)
                        )
                        Spacer(Modifier.height(16.dp))
                        MarkdownContent(noteText)
                    }
                }
                Spacer(Modifier.height(30.dp))
            }

        }
    }
}