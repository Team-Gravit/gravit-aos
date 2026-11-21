package com.inuappcenter.gravit.main.Study.Chapter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.inuappcenter.gravit.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import com.inuappcenter.gravit.share.LoadFailedDialog
import com.inuappcenter.gravit.share.StatusBarStyle
import com.inuappcenter.gravit.ui.theme.AppColor
import com.inuappcenter.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.api.ChapterPageResponse
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Home.RoundedGauge
import com.inuappcenter.gravit.main.Home.resolvePlanetRes
import com.inuappcenter.gravit.main.User.TopBar
import com.inuappcenter.gravit.ui.theme.mbc1961
import com.inuappcenter.gravit.ui.theme.pretendard
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalLayoutDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.LineBreak

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Learning(
    navController: NavController
) {
    val context = LocalContext.current
    val vm: ChapterViewModel = viewModel(
        factory = ChapterVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.load()
    }

    LaunchedEffect(ui) {
        if (navigated) return@LaunchedEffect

        when (ui) {
            ChapterViewModel.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            ChapterViewModel.UiState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    launchSingleTop = true
                }
            }

            else -> {
                navigated = false
            }
        }
    }

    LearningUI(
        navController = navController,
        uiState = ui,
        onRetry = { vm.load() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun LearningUI(
    navController: NavController,
    uiState: ChapterViewModel.UiState,
    onRetry: () -> Unit
) {
    StatusBarStyle(darkIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg0)
    ) {
        TopBar(navController, "학습", useIcon = false)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg1)
                .navigationBarsPadding()
        ) {
            when (uiState) {
                ChapterViewModel.UiState.Loading -> {
                    ChapterSkeletonUI()
                }

                is ChapterViewModel.UiState.Success -> {
                    PreloadedChapterUI(
                        navController = navController,
                        chapters = uiState.data
                    )
                }

                ChapterViewModel.UiState.Failed -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ChapterSkeletonUI()

                        LoadFailedDialog(
                            onRetryClick = onRetry
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun PreloadedChapterUI(
    navController: NavController,
    chapters: List<ChapterPageResponse>
) {
    val context = LocalContext.current
    val imageLoader = context.imageLoader
    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val columnCount = when {
        screenWidth >= 840.dp -> 4
        screenWidth >= 600.dp -> 3
        else -> 2
    }

    val cardWidth = (
            screenWidth - 32.dp - 12.dp * (columnCount - 1)
            ) / columnCount

    val planetSize = cardWidth * 0.7f

    val imageSizePx = with(density) {
        planetSize.roundToPx()
    }

    val planetIds = remember(chapters) {
        chapters.mapNotNull {
            resolvePlanetRes(it.chapterSummaryResponse.chapterId)
        }.distinct()
    }

    val preloaded by produceState(
        initialValue = false,
        key1 = planetIds,
        key2 = imageSizePx,
        key3 = imageLoader
    ) {
        value = false

        withContext(Dispatchers.IO) {
            planetIds.forEach { resId ->
                val request = ImageRequest.Builder(context)
                    .data(resId)
                    .size(imageSizePx, imageSizePx)
                    .memoryCacheKey("planet_$resId")
                    .build()

                imageLoader.execute(request)
            }
        }

        value = true
    }

    if (preloaded) {
        ChapterUI(
            navController = navController,
            chapters = chapters
        )
    } else {
        ChapterSkeletonUI()
    }
}
@Composable
private fun ChapterUI(
    navController: NavController,
    chapters: List<ChapterPageResponse>
) {
    val buttons = remember(chapters) { mapToButtons(chapters) }
    val screenWidth = LocalConfiguration.current.screenWidthDp

    val columnCount = when {
        screenWidth >= 840 -> 4
        screenWidth >= 600 -> 3
        else -> 2
    }

    val rows = remember(buttons, columnCount) {
        buttons.chunked(columnCount)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2)),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(rows.size) { rowIndex ->
            val rowItems = rows[rowIndex]

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEachIndexed { index, button ->
                    ChapterButton(
                        description = button.description,
                        text = button.title,
                        onClick = {
                            navController.navigate("unit/${button.chapterId}")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(160f / 166f),
                        planet = button.planetRes,
                        isRight = index == rowItems.lastIndex,
                        rate = button.rate,
                        enabled = true
                    )

                    if (index != rowItems.lastIndex) {
                        Spacer(Modifier.width(12.dp))
                    }
                }

                repeat(columnCount - rowItems.size) {
                    Spacer(Modifier.width(12.dp))
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(160f / 166f)
                    )
                }
            }
        }
    }
}

data class ChapterButtonData(
    val chapterId: Long,
    val title: String,
    val description: String,
    val rate: Double,
    val planetRes: Int?,
)


fun mapToButtons(chapters: List<ChapterPageResponse>): List<ChapterButtonData> {
    return chapters.mapNotNull { c ->
        val chapterId = c.chapterSummaryResponse.chapterId
        val planetRes = resolvePlanetRes(chapterId) ?: return@mapNotNull null

        ChapterButtonData(
            chapterId = chapterId,
            title = c.chapterSummaryResponse.title,
            description = c.chapterSummaryResponse.description,
            rate = c.chapterProgressRate,
            planetRes = planetRes
        )
    }
}

@Composable
fun ChapterButton(
    description: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    planet: Int?,
    isRight: Boolean,
    rate: Double,
    enabled: Boolean = true
) {
    var showTooltip by remember { mutableStateOf(false) }
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(0.dp),
        enabled = enabled
    ) {
        Box(Modifier.fillMaxSize()) {
            if (planet != null) {
                Image(
                    painter = painterResource(id = R.drawable.planet_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val planetSize = maxWidth * 0.7f
                    val context = LocalContext.current
                    val density = LocalDensity.current

                    val imageSizePx = with(density) {
                        planetSize.roundToPx()
                    }

                    val imageRequest = remember(planet, imageSizePx) {
                        ImageRequest.Builder(context)
                            .data(planet)
                            .size(imageSizePx, imageSizePx)
                            .memoryCacheKey("planet_$planet")
                            .build()
                    }

                    AsyncImage(
                        model = imageRequest,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(planetSize)
                            .offset(
                                x = planetSize * (20f / 130f),
                                y = planetSize * (25f / 130f)
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(
                            text = text,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = mbc1961,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f),
                            color = AppColor.CTA_text
                        )
                        Box {
                            Icon(
                                painter = painterResource(R.drawable.info),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { showTooltip = !showTooltip },
                                tint = AppColor.CTA_text
                            )
                            Popup(
                                isShowing = showTooltip,
                                text = description,
                                onDismiss = { showTooltip = false },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    RoundedGauge(
                        rate = rate,
                        modifier = Modifier.fillMaxWidth(),
                        height = 10.dp,
                        width = 0.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${rate.toInt()}%",
                        style = AppTypography.Label1,
                        color = PrimitiveColor.Purple50
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
    onDismiss: () -> Unit,
) {
    if (!isShowing) return

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val tipWidth = 42.dp
    val tipHeight = 20.dp
    val popupWidth = 230.dp

    val tipHeightPx = with(density) { tipHeight.roundToPx() }
    val gapPx = with(density) { 6.dp.roundToPx() }

    // 화면 끝과 겹치지 않도록 최소 1px 확보
    val edgePx = 10

    // 가로모드에서 시스템 내비게이션 바 영역 제외
    val navInsets = WindowInsets.navigationBars
    val navLeftPx = navInsets.getLeft(density, layoutDirection)
    val navRightPx = navInsets.getRight(density, layoutDirection)

    val triangleProvider = remember(gapPx) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val centerX =
                    (anchorBounds.left + anchorBounds.right) / 2

                return IntOffset(
                    x = centerX - popupContentSize.width / 2,
                    y = anchorBounds.bottom + gapPx
                )
            }
        }
    }

    val boxProvider = remember(
        tipHeightPx,
        gapPx,
        navLeftPx,
        navRightPx
    ) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val centerX =
                    (anchorBounds.left + anchorBounds.right) / 2

                val desiredX =
                    centerX - popupContentSize.width / 2

                val minX = navLeftPx + edgePx

                val maxX = (
                        windowSize.width -
                                navRightPx -
                                popupContentSize.width -
                                edgePx
                        ).coerceAtLeast(minX)

                return IntOffset(
                    x = desiredX.coerceIn(minX, maxX),
                    y = anchorBounds.bottom + gapPx + tipHeightPx
                )
            }
        }
    }

    Popup(
        popupPositionProvider = triangleProvider,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = false,
            clippingEnabled = false
        )
    ) {
        TriangleUpTip(
            modifier = Modifier.size(tipWidth, tipHeight),
            color = Color(0xFF222124)
        )
    }

    Popup(
        popupPositionProvider = boxProvider,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            clippingEnabled = false
        )
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        val availableWidth = (
                screenWidth -
                        with(density) {
                            (navLeftPx + navRightPx + edgePx * 2).toDp()
                        }
                ).coerceAtLeast(1.dp)

        Surface(
            modifier = Modifier.widthIn(
                max = minOf(popupWidth, availableWidth)
            ),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF222124)
        ) {
            Row(
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.info),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        lineHeight = 22.sp,
                        lineBreak = LineBreak.Simple
                    )
                )
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
            moveTo(w / 2f, 0f)
            lineTo(0f, h)
            lineTo(w, h)
            close()
        }
        drawPath(path, color = color)
    }
}