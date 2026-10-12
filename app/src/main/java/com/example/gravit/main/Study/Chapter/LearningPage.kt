package com.inuappcenter.gravit.main.Study.Chapter

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import com.example.gravit.share.LoadFailedDialog
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.api.ChapterPageResponse
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Home.RoundedGauge
import com.inuappcenter.gravit.main.User.TopBar
import com.inuappcenter.gravit.share.TabBar
import com.inuappcenter.gravit.ui.theme.mbc1961
import com.inuappcenter.gravit.ui.theme.pretendard

enum class LearningTab { Chapter, AI }

@Composable
fun Learning(
    navController: NavController,
){
    val context = LocalContext.current
    val vm: ChapterViewModel = viewModel(factory = ChapterVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()

    var selectedTab by remember { mutableStateOf(LearningTab.Chapter) }
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
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onRetry = { vm.load() }
    )
}

@Composable
private fun LearningUI(
    navController: NavController,
    uiState: ChapterViewModel.UiState,
    selectedTab: LearningTab,
    onTabSelected: (LearningTab) -> Unit,
    onRetry: () -> Unit
){
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
            Spacer(Modifier.height(20.dp))
            TabBar(
                selectedIndex = when (selectedTab) {
                    LearningTab.Chapter -> 0
                    LearningTab.AI -> 1
                },
                onTabSelected = { index ->
                    onTabSelected(
                        if (index == 0) {
                            LearningTab.Chapter
                        } else {
                            LearningTab.AI
                        }
                    )
                },
                tab1 = "개념학습",
                tab2 = "면접대비"
            )
            when (selectedTab) {
                LearningTab.Chapter -> {
                    when (uiState) {
                        ChapterViewModel.UiState.Loading -> {
                            ChapterSkeletonUI()
                        }

                        is ChapterViewModel.UiState.Success -> {
                            ChapterUI(
                                navController = navController,
                                chapters = uiState.data
                            )
                        }
                        ChapterViewModel.UiState.Failed -> {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                ChapterSkeletonUI()

                                LoadFailedDialog(
                                    onRetryClick = onRetry
                                )
                            }
                        }
                        else -> Unit
                    }
                }

                LearningTab.AI -> {
                }
            }
        }
    }
}

@Composable
private fun ChapterUI(
    navController: NavController,
    chapters: List<ChapterPageResponse>
) {
    val buttons = remember(chapters) { mapToButtons(chapters) }

    Column(
        modifier = Modifier
            .background(Color(0xFFF2F2F2))
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp

        val columnCount = when {
            screenWidth >= 840 -> 4
            screenWidth >= 600 -> 3
            else -> 2
        }
        buttons.chunked(columnCount).forEach { rowItems ->
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
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
                repeat(columnCount - rowItems.size) {
                    Spacer(modifier = Modifier.width(12.dp))

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(160f / 166f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
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

fun resolvePlanetRes(id: Long): Int? {
    return planetById[id]
}

val planetById = mapOf(
    1L to R.drawable.data_structure_chapter,
    2L to R.drawable.algorithm_chapter,
    3L to R.drawable.computer_network_chapter,
    4L to R.drawable.database_chapter,
    5L to R.drawable.computer_security_chapter,
    6L to R.drawable.software_engineering_chapter,
    7L to R.drawable.opreating_system_chapter,
    8L to R.drawable.programming_language_chapter,
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
    val grayFilter = ColorFilter.colorMatrix(
        ColorMatrix(
            floatArrayOf(
                0.3f, 0.3f, 0.3f, 0f, 0f,
                0.3f, 0.3f, 0.3f, 0f, 0f,
                0.3f, 0.3f, 0.3f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    )

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
                    painter = painterResource(id = planet),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = if (enabled) null else grayFilter
                )
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
                                    .clickable { showTooltip = !showTooltip },
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
    val tipWidth = 42.dp
    val tipHeight = 20.dp
    val popupWidth = 257.dp

    val tipWidthPx = with(density) { tipWidth.roundToPx() }
    val tipHeightPx = with(density) { tipHeight.roundToPx() }
    val gapPx = with(density) { 6.dp.roundToPx() }
    val marginPx = with(density) { 8.dp.roundToPx() }

    // Both popups use the same anchor: the 24dp Box enclosing the info icon.
    val triangleProvider = remember(tipWidthPx, gapPx) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val centerX = (anchorBounds.left + anchorBounds.right) / 2
                return IntOffset(
                    x = centerX - popupContentSize.width / 2,
                    y = anchorBounds.bottom + gapPx
                )
            }
        }
    }

    val boxProvider = remember(tipHeightPx, gapPx, marginPx) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val centerX = (anchorBounds.left + anchorBounds.right) / 2
                val desiredX = centerX - popupContentSize.width / 2
                val maxX = (windowSize.width - popupContentSize.width - marginPx)
                    .coerceAtLeast(marginPx)
                return IntOffset(
                    x = desiredX.coerceIn(marginPx, maxX),
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
        Surface(
            modifier = Modifier.width(popupWidth),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF222124)
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
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
                        lineHeight = 22.sp
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