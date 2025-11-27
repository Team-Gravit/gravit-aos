package com.example.gravit.main.Study.Chapter

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.mbc1961
import com.example.gravit.ui.theme.pretendard
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.example.gravit.api.ChapterPageResponse
import com.example.gravit.ui.theme.Responsive
import com.example.gravit.main.Home.RoundedGauge
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*


@Composable
fun ChapterScreen(
    navController: NavController,
    onSessionExpired: () -> Unit
){
    val context = LocalContext.current
    val vm: ChapterViewModel = viewModel(
        factory = ChapterVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.load() }
    when (ui) {
        ChapterViewModel.UiState.SessionExpired -> {
            navigated = true
            navController.navigate("error/401") {
                popUpTo(0); launchSingleTop = true; restoreState = false
            }
        }
        ChapterViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        ChapterViewModel.UiState.NotFound -> {
            navigated = true
            navController.navigate("error/404") {
                popUpTo(0); launchSingleTop = true; restoreState = false
            }
        }
        ChapterViewModel.UiState.Failed -> {
            navigated = true
            onSessionExpired()
        }
        else -> Unit
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Responsive.h(70f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "학습",
                    fontFamily = pretendard,
                    fontSize = Responsive.spH(20f),
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF030303),
                    modifier = Modifier
                        .padding(start = Responsive.w(20f))
                        .align(Alignment.CenterStart)
                )
            }

            val chapters: List<ChapterPageResponse> = (ui as? ChapterViewModel.UiState.Success)?.data ?: emptyList()
            val buttons = remember(chapters) { mapToButtons(chapters) }

            Column(
                modifier = Modifier
                    .background(Color(0xFFF2F2F2))
                    .padding(
                        start = Responsive.w(16f),
                        end = Responsive.w(16f),
                        top = Responsive.h(16f)
                    )
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                buttons.chunked(2).forEach { pair ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        pair.forEachIndexed { index, data ->
                            ChapterButton(
                                description = data.description,
                                text = data.title,
                                planet = data.planetRes,
                                rate = data.rate,
                                onClick = {
                                    navController.navigate("unit/${data.chapterId}")
                                          },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(160f / 166f)
                                    .shadow(Responsive.h(4f), RoundedCornerShape(Responsive.h(10f))),
                                isRight = (index == 1)
                            )
                            if (index == 0 && pair.size > 1) {
                                Spacer(modifier = Modifier.width(Responsive.w(8f)))
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.width(Responsive.w(8f)))
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(160f / 166f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Responsive.h(8f)))
                }
            }
        }
    }
}

data class ChapterButtonUI(
    val chapterId: Int,
    val title: String,
    val description: String,
    val rate: Float,
    val planetRes: Int,
)

private val csChapterName = mapOf(
    1 to "data-structure",
    2 to "algorithm",
    3 to "network"
)
private fun toChaterName(id: Int): String{
    return csChapterName[id] ?: ""
}
private val planetById = mapOf(
    1 to R.drawable.data_structure_chapter,
    2 to R.drawable.algorithm_chapter,
    3 to R.drawable.computer_network_chapter,
    4 to R.drawable.opreating_system_chapter,
    5 to R.drawable.database_chapter,
    6 to R.drawable.computer_security_chapter,
    7 to R.drawable.sofftware_engineering_chapter,
    8 to R.drawable.programming_language_chapter,
)
private fun resolvePlanetRes(id: Int): Int {
    return planetById[id] ?: error("알 수 없는 chapterId: $id")
}
fun mapToButtons(chapters: List<ChapterPageResponse>): List<ChapterButtonUI> {
    return chapters.map { c ->
        ChapterButtonUI(
            chapterId = c.chapterSummary.chapterId,
            title = c.chapterSummary.title,
            description = c.chapterSummary.description,
            rate = c.chapterProgressRate?.toFloatOrNull() ?: 0f,
            planetRes = resolvePlanetRes(c.chapterSummary.chapterId),
        )
    }
}
@Composable
fun ChapterButton(
    description: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    planet: Int,
    isRight: Boolean,
    rate: Float
) {
    var showTooltip by remember { mutableStateOf(false) }

    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(Responsive.h(10f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = planet),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = Responsive.h(15f))
            ) {
                Column(modifier = Modifier.padding(horizontal = Responsive.w(10f))) {
                    Row(
                        modifier = Modifier.size(
                            Responsive.w(140f),
                            Responsive.h(24f)
                        )
                    ) {
                        Text(
                            text = text,
                            fontSize = Responsive.spH(20f),
                            fontWeight = FontWeight.Medium,
                            fontFamily = mbc1961,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f),
                            color = Color.White
                        )
                        Box {
                            Icon(
                                painter = painterResource(R.drawable.info),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(Responsive.w(24f))
                                    .clickable { showTooltip = !showTooltip },
                                tint = Color.White
                            )
                            Popup(
                                isShowing = showTooltip,
                                text = description,
                                isRight = isRight,
                                onDismiss = { showTooltip = false },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Responsive.h(12f)))
                    RoundedGauge(
                        rate = rate,
                        width = Responsive.w(140f),
                        height = Responsive.h(10f)
                    )

                }
            }
        }

    }
}
@Composable
fun Popup(
    isShowing: Boolean,
    text: String,
    isRight: Boolean,
    onDismiss: () -> Unit,
) {
    if(isShowing) {

        val density = LocalDensity.current
        val sideMarginPx = with(density) { 8.dp.roundToPx() }
        val bottomMarginPx = with(density) { 8.dp.roundToPx() }
        val yGapPx = with(density) { Responsive.h(6f).roundToPx() }

        val tipStartDp = if (isRight) Responsive.w(205f) else Responsive.w(125f)
        val tipWidthDp = Responsive.w(42f)
        val tipHeight = Responsive.h(20f)

        val tipCenterOffsetPx = with(density) { (tipStartDp + tipWidthDp / 2).roundToPx() }

        val provider = object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val anchorCenterX = (anchorBounds.left + anchorBounds.right) / 2
                val desiredLeft = anchorCenterX - tipCenterOffsetPx
                val minX = sideMarginPx
                val maxX = windowSize.width - popupContentSize.width - sideMarginPx
                val x = desiredLeft.coerceIn(minX, maxX)

                val desiredTop = anchorBounds.bottom + yGapPx
                val maxY = windowSize.height - popupContentSize.height - bottomMarginPx
                val y = desiredTop.coerceAtMost(maxY)

                return IntOffset(x, y)
            }
        }
        Popup(
            popupPositionProvider = provider,
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column {
                TriangleUpTip(
                    modifier = Modifier
                        .padding(start = tipStartDp)
                        .size(tipWidthDp, tipHeight),
                    color = Color(0xFF222124)
                )
                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(Responsive.w(8f)))
                ) {
                    Box(
                        modifier = Modifier
                            .width(Responsive.w(257f))
                            .wrapContentHeight()
                            .background(Color(0xFF222124))
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = Responsive.w(10f),
                                vertical = Responsive.h(10f)
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.info),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(Responsive.h(24f))
                            )
                            Spacer(Modifier.width(Responsive.w(10f)))
                            Text(
                                text = text,
                                fontFamily = pretendard,
                                fontSize = Responsive.spH(16f),
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                lineHeight = Responsive.spH(22f)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun TriangleUpTip(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w / 2f, 0f)   // 위쪽 뾰족 점
            lineTo(0f, h)        // 좌하단
            lineTo(w, h)         // 우하단
            close()
        }
        drawPath(path, color = color)
    }
}

