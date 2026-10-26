package com.inuappcenter.gravit.main.Ai.Home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.InlineButton
import com.example.gravit.ui.theme.InlineButtonState
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.main.Home.RoundedGauge
import com.inuappcenter.gravit.main.User.RankInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gravit.share.LoadFailedDialog
import com.example.gravit.ui.theme.AppRadius
import com.inuappcenter.gravit.api.InterviewDashboardResponse
import com.inuappcenter.gravit.api.InterviewSessionAnswer
import com.inuappcenter.gravit.api.InterviewSessionAnswersResponse
import com.inuappcenter.gravit.api.InterviewSessionSummaryResponse
import com.inuappcenter.gravit.api.InterviewSummaryRecentSession
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.ScoreTrend
import com.inuappcenter.gravit.main.Ai.Interview.AiAnswerAudioPlayer
import com.inuappcenter.gravit.main.Study.Lesson.MarkdownContent
import com.inuappcenter.gravit.main.User.Inquiry.InquiryPagination
import com.inuappcenter.gravit.main.User.TapButton
import com.inuappcenter.gravit.main.User.TopBar
import com.inuappcenter.gravit.ui.theme.pretendard
import kotlin.collections.forEachIndexed
import kotlin.collections.sortedBy
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AiHomePage(
    navController: NavController
) {
    val context = LocalContext.current
    val vm: AiHomeVM = viewModel(factory = AiHomeVMFactory(RetrofitInstance.api, context))

    val ui by vm.dashboardState.collectAsState()
    val weakTopicsUi by vm.weakTopicsState.collectAsState()

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadDashboard()
    }

    LaunchedEffect(ui) {
        if (navigated) return@LaunchedEffect

        when (ui) {
            AiHomeVM.DashboardUiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            AiHomeVM.DashboardUiState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    launchSingleTop = true
                }
            }

            else -> navigated = false
        }
    }

    when (ui) {
        AiHomeVM.DashboardUiState.Loading -> {
            AiHomeSkeletonUI(
                navController = navController
            )
        }

        is AiHomeVM.DashboardUiState.Success -> {
            val state = ui as AiHomeVM.DashboardUiState.Success
            AiHomeUI(
                dashboard = state.data,
                weakTopicsUi = weakTopicsUi,
                onLoadWeakTopics = vm::loadWeakTopics,
                navController = navController
            )
        }

        AiHomeVM.DashboardUiState.Failed -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AiHomeSkeletonUI(
                    navController = navController
                )

                LoadFailedDialog(
                    onRetryClick = {
                        vm.loadDashboard()
                    }
                )
            }
        }
        else -> Unit
    }
}
@Composable
private fun AiHomeUI(
    dashboard: InterviewDashboardResponse,
    weakTopicsUi: AiHomeVM.WeakTopicsUiState,
    onLoadWeakTopics: () -> Unit,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg2),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            AiHomeHeader(navController)
        }

        item {
            AiHomeContent(
                dashboard = dashboard,
                weakTopicsUi = weakTopicsUi,
                onLoadWeakTopics = onLoadWeakTopics,
                navController = navController
            )
        }
    }
}
@Composable
private fun AiHomeContent(
    dashboard: InterviewDashboardResponse,
    weakTopicsUi: AiHomeVM.WeakTopicsUiState,
    onLoadWeakTopics: () -> Unit,
    navController: NavController
) {
    var showWeakestAll by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    val socialInfo = listOf(
                        dashboard.completedSessionCount.let { "${it}회" } ?: "0회",
                        "누적 세션",
                        dashboard.recentAverageScore.let { "${it}점" } ?: "-점",
                        "최근 5개 평균"
                    ).chunked(2)
                    val isLeague = false
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        socialInfo.forEachIndexed { index, item ->

                            RankInfo(
                                value = item[0],
                                label = item[1],
                                modifier = Modifier.weight(1f),
                                color = if (index == 0 && isLeague) AppColor.Main1 else AppColor.text1,
                                onClick = {}
                            )
                            if (index != socialInfo.lastIndex) {
                                VerticalDivider(
                                    modifier = Modifier.height(40.dp),
                                    thickness = 1.dp,
                                    color = AppColor.divider1
                                )
                            }
                        }
                    }

                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .padding(16.dp),
            ){
                Column {
                    Spacer(Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "최근 응시 이력",
                            textAlign = TextAlign.Start,
                            style = AppTypography.Label2,
                            color = AppColor.text4
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "전체 보기",
                            textAlign = TextAlign.Start,
                            style = AppTypography.Label2,
                            color = AppColor.text4,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(
                                onClick = { navController.navigate("ai/home/recentAllSessions")}
                             )
                        )
                    }
                    if(dashboard.recentSessions.isEmpty()){
                        EmptyContent(
                            text = "아직 응시한 세션이 없어요.\n첫 세션을 시작해 볼까요?",
                            isButton = false
                        )
                    }else {
                        Spacer(Modifier.height(16.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            dashboard.recentSessions.forEach {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimitiveColor.Gray200)
                                        .clickable {
                                            navController.navigate("ai/feedback/${it.sessionId}")
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(50f))
                                            .background(PrimitiveColor.Gray800),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${it.sequence}",
                                            style = AppTypography.Body1_Normal,
                                            color = AppColor.text1w
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = it.mode,
                                                style = AppTypography.Label1,
                                                color = AppColor.text2
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = "${it.topics.firstOrNull()?.displayName ?: ""} • ${it.startedAt.substringBefore("T")}",
                                                style = AppTypography.Label2,
                                                color = AppColor.text4
                                            )

                                        }
                                        Spacer(Modifier.weight(1f))
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp, 22.dp)
                                                .clip(RoundedCornerShape(60.dp))
                                                .background(AppColor.bg0)
                                                .border(
                                                    1.dp,
                                                    AppColor.Main2,
                                                    RoundedCornerShape(60.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${it.score}점",
                                                style = AppTypography.Caption1,
                                                color = AppColor.Main2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .padding(16.dp),
            ){
                Column {
                    Spacer(Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "가장 약한 분야",
                            textAlign = TextAlign.Start,
                            style = AppTypography.Label2,
                            color = AppColor.text4
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "전체 보기",
                            textAlign = TextAlign.Start,
                            style = AppTypography.Label2,
                            color = AppColor.text4,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(
                                onClick = {
                                    showWeakestAll = true
                                    onLoadWeakTopics()
                                }
                            )
                        )
                    }
                    if(dashboard.weakestTopics.isEmpty()){
                        EmptyContent(
                            text = "세션이 쌓이면\n약점 분야를 알려드려요.",
                            isButton = false,
                            height = 275.dp
                        )
                    }else {
                        Spacer(Modifier.height(16.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            dashboard.weakestTopics.forEach {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimitiveColor.Gray200)
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = it.topic.displayName,
                                                style = AppTypography.Headline2,
                                                color = AppColor.text2
                                            )
                                            Spacer(Modifier.weight(1f))
                                            Text(
                                                text = "정확도 ${it.accuracyRate}%",
                                                style = AppTypography.Body1_Normal,
                                                color = AppColor.text2
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        RoundedGauge(
                                            rate = it.accuracyRate,
                                            modifier = Modifier.fillMaxWidth(),
                                            height = 8.dp,
                                            width = 0.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .padding(16.dp),
            ){
                Column {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "점수 추이",
                        textAlign = TextAlign.Start,
                        style = AppTypography.Label2,
                        color = AppColor.text4
                    )
                    if(dashboard.scoreTrends.isEmpty()){
                        EmptyContent(
                            text = "세션이 2개 이상 쌓이면\n추이를 볼 수 있어요.",
                            isButton = false,
                            height = 212.dp
                        )
                    }else {
                        Spacer(Modifier.height(16.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ScoreTrendChart(
                                trends = dashboard.scoreTrends,
                                modifier = Modifier
                                    .padding(top = 30.dp, bottom = 8.dp)
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(45.dp))
        }
        if(showWeakestAll) {
            Dialog(
                onDismissRequest = {
                    showWeakestAll = false
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                Surface(
                    modifier = Modifier
                        .width(328.dp)
                        .padding(horizontal = 16.dp)
                        .height(445.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = AppColor.bg1
                ) {
                    Column(modifier = Modifier) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 20.dp
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "가장 약한 분야",
                                    style = AppTypography.Body2_Normal,
                                    color = PrimitiveColor.Gray500
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clickable(
                                            interactionSource = remember {
                                                MutableInteractionSource()
                                            },
                                            indication = null
                                        ) {
                                            showWeakestAll = false
                                        },
                                    tint = AppColor.text1
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = AppColor.divider1
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            when (weakTopicsUi) {
                                AiHomeVM.WeakTopicsUiState.Loading,
                                AiHomeVM.WeakTopicsUiState.Failed -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = AppColor.Main1
                                        )
                                    }
                                }

                                is AiHomeVM.WeakTopicsUiState.Success -> {
                                    if (weakTopicsUi.data.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            EmptyContent(
                                                text = "세션이 쌓이면\n약점 분야를 알려드려요.",
                                                isButton = false,
                                                height = 275.dp
                                            )
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            items(weakTopicsUi.data) { item ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(PrimitiveColor.Gray200)
                                                        .padding(16.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = item.topic.displayName,
                                                                style = AppTypography.Headline2,
                                                                color = AppColor.text2
                                                            )

                                                            Spacer(Modifier.weight(1f))

                                                            fun accuracyColor(rate: Double): Color {
                                                                return when {
                                                                    rate < 30 -> Color(0xFFEB3D32)
                                                                    rate < 70 -> Color(0xFFF9A825)
                                                                    else -> Color(0xFF00C30D)
                                                                }
                                                            }

                                                            Text(
                                                                text = "정확도 ${item.accuracyRate}%",
                                                                style = AppTypography.Body1_Normal,
                                                                color = accuracyColor(item.accuracyRate)
                                                            )
                                                        }

                                                        Spacer(Modifier.height(8.dp))

                                                        val weakestGaugeBrush =
                                                            Brush.horizontalGradient(
                                                                colorStops = arrayOf(
                                                                    0f to Color(0xFFEB3D32),
                                                                    0.5f to Color(0xFFF9A825),
                                                                    1f to Color(0xFF00C30D)
                                                                )
                                                            )

                                                        RoundedGauge(
                                                            rate = item.accuracyRate,
                                                            modifier = Modifier.fillMaxWidth(),
                                                            height = 8.dp,
                                                            width = 0.dp,
                                                            gaugeBrush = weakestGaugeBrush
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                else -> Unit
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun AiHomeHeader(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ai_home_bg),
            contentDescription = "ai home bg",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AI 면접으로\n실전 감각을 키워보세요!",
                color = AppColor.CTA_text,
                style = AppTypography.Heading2
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "CS 개념부터 직군 실무까지,\n6문항으로 점검해요.",
                color = PrimitiveColor.Gray400,
                style = AppTypography.Label2
            )
            Spacer(Modifier.weight(1f))
            InlineButton(
                "새 세션 시작",
                onClick = { navController.navigate("ai/selected")},
                state = InlineButtonState.Default,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                style = AppTypography.Label2,
                color = AppColor.CTA_text
            )
        }
    }
}
@Composable
fun EmptyContent(
    text: String,
    subText: String = "",
    isButton: Boolean,
    height: Dp = 265.dp
)

{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                style = AppTypography.Label1,
                color = AppColor.text3w,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun ScoreTrendChart(
    trends: List<ScoreTrend>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val sortedTrends = remember(trends) { trends.sortedBy { it.sequence } }

    var selectedIndex by remember(sortedTrends) {
        mutableStateOf(
            if (sortedTrends.isNotEmpty()) {
                sortedTrends.lastIndex
            } else {
                null
            }
        )
    }

    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val minScore = sortedTrends.minOfOrNull { it.score } ?: 0
    val maxScore = sortedTrends.maxOfOrNull { it.score } ?: 0

    val scorePadding = 5
    val graphMinScore = (minScore - scorePadding).coerceAtLeast(0)
    val graphMaxScore = maxScore + scorePadding
    val scoreRange = (graphMaxScore - graphMinScore).coerceAtLeast(1)

    val yLabels = listOf(
        graphMaxScore,
        graphMinScore + scoreRange / 2,
        graphMinScore
    )

    val textStyle = AppTypography.Caption2

    val labelLayouts = yLabels.associateWith { score ->
        textMeasurer.measure(
            text = score.toString(),
            style = textStyle
        )
    }

    val topPadding = 30f
    val bottomPadding = 30f

    val lineStartX = 20f
    val lineEndPadding = 20f

    val graphStartX = 55f
    val graphEndPadding = 55f

    val graphWidth =
        (canvasSize.width - graphStartX - graphEndPadding)
            .coerceAtLeast(0f)


    val xGap =
        if (sortedTrends.size > 1) {
            graphWidth / (sortedTrends.size - 1)
        } else {
            0f
        }

    Canvas(
        modifier = modifier
            .onSizeChanged {
                canvasSize = Size(
                    width = it.width.toFloat(),
                    height = it.height.toFloat()
                )
            }
            .pointerInput(sortedTrends, graphStartX, xGap) {
                detectTapGestures { tapOffset ->
                    if (sortedTrends.isEmpty()) {
                        return@detectTapGestures
                    }

                    val nearestIndex =
                        if (sortedTrends.size == 1) {
                            0
                        } else {
                            sortedTrends.indices.minByOrNull { index ->
                                abs(
                                    tapOffset.x -
                                            (graphStartX + index * xGap)
                                )
                            }
                        }

                    if (nearestIndex != null) {
                        val pointX =
                            graphStartX + nearestIndex * xGap

                        if (abs(tapOffset.x - pointX) <= 40f) {
                            selectedIndex = nearestIndex
                        }
                    }
                }
            }
    ) {
        if (sortedTrends.isEmpty()) return@Canvas

        val graphTop = topPadding
        val graphBottom = size.height - bottomPadding - 50f
        val graphHeight = graphBottom - graphTop

        fun scoreToY(score: Int): Float {
            val ratio =
                (score - graphMinScore).toFloat() /
                        scoreRange.toFloat()

            return graphBottom - ratio * graphHeight
        }

        yLabels.forEach { score ->
            val y = scoreToY(score)
            drawLine(
                color = AppColor.divider1,
                start = Offset(
                    x = lineStartX,
                    y = y
                ),
                end = Offset(
                    x = size.width - lineEndPadding,
                    y = y
                ),
                strokeWidth = 2f
            )
        }

        val points = sortedTrends.mapIndexed { index, trend ->
            Offset(
                x = graphStartX + index * xGap,
                y = scoreToY(trend.score)
            )
        }

        val path = Path().apply {
            moveTo(
                points.first().x,
                points.first().y
            )

            points.drop(1).forEach {
                lineTo(it.x, it.y)
            }
        }

        val fillPath = Path().apply {
            addPath(path)

            lineTo(
                points.last().x,
                graphBottom
            )

            lineTo(
                points.first().x,
                graphBottom
            )

            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    AppColor.Main2.copy(alpha = 0.9f),
                    Color.Transparent
                ),
                startY = graphTop,
                endY = graphBottom
            )
        )

        drawPath(
            path = path,
            color = AppColor.Main2,
            style = Stroke(width = 2f)
        )

        sortedTrends.forEachIndexed { index, trend ->
            val point = points[index]
            val isSelected = selectedIndex == index

            if (isSelected) {
                drawCircle(
                    color = AppColor.bg1,
                    radius = 15f,
                    center = point
                )
            }

            drawCircle(
                color = AppColor.Main2,
                radius = 10f,
                center = point
            )

            val sequenceText =
                if (index == sortedTrends.lastIndex) {
                    "현재"
                } else {
                    "${trend.sequence}차"
                }

            val sequenceStyle = AppTypography.Caption1.copy(
                color = if (isSelected) {
                    AppColor.Main2
                } else {
                    AppColor.text3
                }
            )

            val sequenceLayout = textMeasurer.measure(
                text = sequenceText,
                style = sequenceStyle
            )

            drawText(
                textMeasurer = textMeasurer,
                text = sequenceText,
                style = sequenceStyle,
                topLeft = Offset(
                    x = point.x - sequenceLayout.size.width / 2f,
                    y = graphBottom + 30f
                )
            )

            if (isSelected) {
                val scoreText = "${trend.score}점"
                val scoreTextStyle = AppTypography.Label2

                val scoreTextLayout = textMeasurer.measure(
                    text = scoreText,
                    style = scoreTextStyle
                )

                val horizontalPadding = 20f
                val verticalPadding = 15f

                val boxWidth =
                    scoreTextLayout.size.width + horizontalPadding * 2

                val boxHeight =
                    scoreTextLayout.size.height + verticalPadding * 2

                val boxGap = 40f
                val edgePadding = 10f

                val boxLeft =
                    (point.x - boxWidth / 2f).coerceIn(
                        minimumValue = edgePadding,
                        maximumValue = size.width - boxWidth - edgePadding
                    )

                val boxTop =
                    point.y - boxGap - boxHeight

                drawRoundRect(
                    color = AppColor.bg1,
                    topLeft = Offset(
                        x = boxLeft,
                        y = boxTop
                    ),
                    size = Size(
                        width = boxWidth,
                        height = boxHeight
                    ),
                    cornerRadius = CornerRadius(
                        x = 12f,
                        y = 12f
                    )
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = scoreText,
                    style = scoreTextStyle.copy(
                        color = AppColor.text1
                    ),
                    topLeft = Offset(
                        x = boxLeft + horizontalPadding,
                        y = boxTop + verticalPadding
                    )
                )
                drawLine(
                    color = AppColor.Main2,
                    start = point,
                    end = Offset(
                        x = point.x,
                        y = graphBottom
                    ),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(10f, 7f)
                    )
                )
            }
        }
    }
}
@Composable
fun RecentAllSessions(
    navController: NavController
) {
    val context = LocalContext.current
    val vm: AiHomeVM = viewModel(factory = AiHomeVMFactory(RetrofitInstance.api, context))
    val ui by vm.historyState.collectAsState()

    var currentPage by remember { mutableStateOf(1) }
    var sort by remember { mutableStateOf("LATEST") }

    LaunchedEffect(currentPage, sort) {
        vm.loadHistory(
            page = currentPage - 1,
            sort = sort
        )
    }

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(ui) {
        if (navigated) return@LaunchedEffect

        when (ui) {
            AiHomeVM.HistoryUiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            AiHomeVM.HistoryUiState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    launchSingleTop = true
                }
            }

            else -> navigated = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg0),
        ) {
            TopBar(
                navController = navController,
                title = "최근 응시 이력",
                icon = painterResource(id = R.drawable.unselected_home_button),
                height = 48.dp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .weight(1f)
            ) {
                Column() {
                    Spacer(Modifier.height(20.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "최신 순",
                                color = if (sort == "LATEST") AppColor.text2 else AppColor.text4,
                                modifier = Modifier.clickable (
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                    if (sort != "LATEST") {
                                        currentPage = 1
                                        sort = "LATEST"
                                    }
                                }
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "오래된 순",
                                color = if (sort == "OLDEST") AppColor.text2 else AppColor.text4,
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        if (sort != "OLDEST") {
                                            currentPage = 1
                                            sort = "OLDEST"
                                        }
                                    }
                                )
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
                    }
                    when (val state = ui) {
                        AiHomeVM.HistoryUiState.Loading -> {
                            RecentAllSessionsSkeletonUI()
                        }

                        is AiHomeVM.HistoryUiState.Success -> {
                            if (state.data.contents.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    EmptyContent(
                                        text = "아직 응시한 세션이 없어요.\n첫 세션을 시작해 볼까요?",
                                        isButton = false
                                    )
                                }
                            }else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(
                                        top = 16.dp,
                                        bottom = 16.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(
                                        items = state.data.contents,
                                        key = { it.sessionId }
                                    ) { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(AppColor.bg0)
                                                .clickable {
                                                    navController.navigate("ai/feedback/${item.sessionId}")
                                                }
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(RoundedCornerShape(50f))
                                                    .background(PrimitiveColor.Gray800),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${item.sequence}",
                                                    style = AppTypography.Body1_Normal,
                                                    color = AppColor.text1w
                                                )
                                            }

                                            Spacer(Modifier.width(12.dp))

                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = item.mode,
                                                        style = AppTypography.Label1,
                                                        color = AppColor.text2
                                                    )

                                                    Spacer(Modifier.height(2.dp))

                                                    Text(
                                                        text = "${
                                                            item.topics
                                                                .firstOrNull()
                                                                ?.displayName
                                                                ?: item.stack?.displayName
                                                                ?: ""
                                                        } • ${
                                                            item.startedAt.substringBefore("T")
                                                        }",
                                                        style = AppTypography.Label2,
                                                        color = AppColor.text4
                                                    )
                                                }

                                                Spacer(Modifier.weight(1f))

                                                Box(
                                                    modifier = Modifier
                                                        .size(42.dp, 22.dp)
                                                        .clip(RoundedCornerShape(60.dp))
                                                        .background(AppColor.bg0)
                                                        .border(
                                                            width = 1.dp,
                                                            color = AppColor.Main2,
                                                            shape = RoundedCornerShape(60.dp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${item.score}점",
                                                        style = AppTypography.Caption1,
                                                        color = AppColor.Main2
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (
                                        state.data.contents.isNotEmpty() &&
                                        state.data.totalPages > 0
                                    ) {
                                        item {
                                            InquiryPagination(
                                                currentPage = currentPage,
                                                totalPages = state.data.totalPages,
                                                onPageClick = { page ->
                                                    currentPage = page
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        AiHomeVM.HistoryUiState.Failed -> {
                            RecentAllSessionsSkeletonUI()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}
enum class FeedBackTab {
    Synthesis,
    Detail
}
@Composable
fun FeedBackReport(
    navController: NavController,
    sessionId: Long
)
{
    val context = LocalContext.current
    val vm: AiFeedbackVM = viewModel(factory = AiFeedbackVMFactory(RetrofitInstance.api, context))

    val summaryState by vm.summaryState.collectAsState()
    val answersState by vm.answersState.collectAsState()
    var playingAnswerNumber by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(sessionId) {
        vm.loadSummary(sessionId)
    }
    var errorNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(summaryState, answersState) {
        if (errorNavigated) return@LaunchedEffect

        when {
            summaryState == AiFeedbackVM.SummaryUiState.SessionExpired ||
                    answersState == AiFeedbackVM.AnswersUiState.SessionExpired -> {
                errorNavigated = true
                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            summaryState == AiFeedbackVM.SummaryUiState.NotFound ||
                    answersState == AiFeedbackVM.AnswersUiState.NotFound -> {
                errorNavigated = true
                navController.navigate("error/404") {
                    launchSingleTop = true
                }
            }

            else -> errorNavigated = false
        }
    }
    val listState = rememberLazyListState()
    var selectedTab by remember { mutableStateOf(FeedBackTab.Synthesis) }
    var expandedInquiryIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg0),
        ) {
            TopBar(
                navController = navController,
                title = "피드백 리포트",
                height = 48.dp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColor.bg2)
                        .padding(horizontal = 16.dp),
                ) {
                    item {
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColor.bg1)
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TapButton(
                                text = "세션 종합",
                                selected = selectedTab == FeedBackTab.Synthesis,
                                onClick = {
                                    selectedTab = FeedBackTab.Synthesis
                                    playingAnswerNumber = null
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .weight(1f)
                            )
                            TapButton(
                                text = "문항별 상세",
                                selected = selectedTab == FeedBackTab.Detail,
                                onClick = {
                                    selectedTab = FeedBackTab.Detail

                                    if (answersState !is AiFeedbackVM.AnswersUiState.Success) {
                                        vm.loadAnswers(sessionId)
                                    }
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .weight(1f)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    when (selectedTab) {
                        FeedBackTab.Synthesis -> {
                            when (val state = summaryState) {
                                AiFeedbackVM.SummaryUiState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(300.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = AppColor.Main1
                                            )
                                        }
                                    }
                                }

                                is AiFeedbackVM.SummaryUiState.Success -> {
                                    item {
                                        SynthesisUi(
                                            synthesis = state.data,
                                            navController = navController
                                        )
                                    }
                                }

                                AiFeedbackVM.SummaryUiState.Failed -> {
                                    item {
                                        LoadFailedDialog(
                                            onRetryClick = {
                                                vm.loadSummary(sessionId)
                                            }
                                        )
                                    }
                                }

                                else -> Unit
                            }
                        }

                        FeedBackTab.Detail -> {
                            when (val state = answersState) {
                                AiFeedbackVM.AnswersUiState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(300.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = AppColor.Main1
                                            )
                                        }
                                    }
                                }

                                is AiFeedbackVM.AnswersUiState.Success -> {
                                    item {
                                        DetailUi(
                                            sessionAnswers = state.data,
                                            expandedInquiryIds = expandedInquiryIds,
                                            onInquiryClick = { displayOrder ->
                                                if (displayOrder in expandedInquiryIds) {
                                                    if (playingAnswerNumber == displayOrder) {
                                                        playingAnswerNumber = null
                                                    }

                                                    expandedInquiryIds =
                                                        expandedInquiryIds - displayOrder
                                                } else {
                                                    expandedInquiryIds =
                                                        expandedInquiryIds + displayOrder
                                                }
                                            },
                                            onPageClick = {
                                                expandedInquiryIds = emptySet()
                                            },
                                            playingAnswerNumber = playingAnswerNumber,
                                            onPlayingAnswerChange = {
                                                playingAnswerNumber = it
                                            }
                                        )
                                    }
                                }

                                AiFeedbackVM.AnswersUiState.Failed -> {
                                    item {
                                        LoadFailedDialog(
                                            onRetryClick = {
                                                vm.loadAnswers(sessionId)
                                            }
                                        )
                                    }
                                }

                                else -> Unit
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SynthesisUi(
    navController: NavController,
    synthesis: InterviewSessionSummaryResponse
) {
    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column(

        ) {
            Text(
                text = synthesis.startedAt.substringBefore("T"),
                color = AppColor.text3w,
                style = AppTypography.Caption2
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .padding(16.dp),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "전체 점수",
                            color = AppColor.text4,
                            style = AppTypography.Label2
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "전체 보기",
                            color = AppColor.text4,
                            style = AppTypography.Label2,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(126.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScoreGauge(
                            score = synthesis.score,
                            maxScore = synthesis.maxScore,
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                            ){
                                Column ( modifier = Modifier.fillMaxWidth()) {
                                    val calculatorAccuracyScore = synthesis.accuracyScore - synthesis.averageAccuracyScore
                                    val isUp = calculatorAccuracyScore >= 0
                                    Text(
                                        text = "정확도",
                                        style = AppTypography.Caption2,
                                        color = AppColor.text3w
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${synthesis.accuracyScore}점",
                                            style = AppTypography.Headline2,
                                            color = AppColor.text1
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Image(
                                            painter = painterResource(R.drawable.graph),
                                            contentDescription = null,
                                            modifier = Modifier.size(150.dp, 20.dp),
                                            contentScale = ContentScale.Fit,
                                            alignment = Alignment.CenterEnd

                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(SpanStyle(color = if(isUp) AppColor.errorColor else AppColor.accentColor)){
                                                append(if(isUp) "▲" else "▼")
                                            }
                                            append(" ")
                                            withStyle(SpanStyle(color = AppColor.text3)){
                                                append("평균보다 ${calculatorAccuracyScore}점 ${ if(isUp)"높아요" else "낮아요"}")
                                            }
                                        },
                                        style = AppTypography.Caption2
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                            ){
                                Column {
                                    val calculatorDeliveryScore = synthesis.deliveryScore - synthesis.averageDeliveryScore
                                    val isUp = calculatorDeliveryScore >= 0
                                    Text(
                                        text = "전달력",
                                        style = AppTypography.Caption2,
                                        color = AppColor.text3w
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${synthesis.deliveryScore}점",
                                            style = AppTypography.Headline2,
                                            color = AppColor.text1
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Image(
                                            painter = painterResource(R.drawable.graph),
                                            contentDescription = null,
                                            modifier = Modifier.size(120.dp, 15.dp),
                                            contentScale = ContentScale.Fit,
                                            alignment = Alignment.CenterEnd

                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(SpanStyle(color = if(isUp) AppColor.errorColor else AppColor.accentColor)){
                                                append(if(isUp) "▲" else "▼")
                                            }
                                            append(" ")
                                            withStyle(SpanStyle(color = AppColor.text3)){
                                                append("평균보다 ${calculatorDeliveryScore}점 ${ if(isUp)"높아요" else "낮아요"}")
                                            }
                                        },
                                        style = AppTypography.Caption2
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(242.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = "문항별 득점",
                        color = AppColor.text4,
                        style = AppTypography.Label2
                    )
                    Spacer(Modifier.height(16.dp))
                    ScoreStackedBarChart(
                        sessions = synthesis.recentSessions,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF5C49A8))
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = "정확도",
                            color = AppColor.text1,
                            style = AppTypography.Caption1
                        )
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFD3C8F3))
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = "전달력",
                            color = AppColor.text1,
                            style = AppTypography.Caption1
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = "약점 분야",
                        color = AppColor.text4,
                        style = AppTypography.Label2
                    )
                    Spacer(Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        synthesis.weakTopics.forEach {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(101.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AppColor.bg1)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = it.topic.displayName,
                                    color = AppColor.text1,
                                    style = AppTypography.Headline2
                                )
                                Spacer(Modifier.height(16.dp))
                                InlineButton(
                                    state = InlineButtonState.Default,
                                    text = "학습 콘텐츠 바로가기",
                                    style = AppTypography.Label2,
                                    color = AppColor.CTA_text,
                                    onClick = {
                                        navController.navigate("lessonList/${it.unitId}") {
                                            launchSingleTop = true
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(32.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
@Composable
fun ScoreStackedBarChart(
    sessions: List<InterviewSummaryRecentSession>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val accuracyColor = Color(0xFF5C49A8)
    val deliveryColor = Color(0xFFD3C8F3)
    val gridColor = AppColor.divider1
    val labelColor = AppColor.text3

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(148.dp)
    ) {
        // 그래프 영역
        val leftPadding = 52.dp.toPx()
        val rightPadding = 8.dp.toPx()
        val topPadding = 16.dp.toPx()
        val bottomPadding = 32.dp.toPx()

        val chartLeft = leftPadding
        val chartRight = size.width - rightPadding
        val chartTop = topPadding
        val chartBottom = size.height - bottomPadding

        val chartHeight = chartBottom - chartTop
        val chartWidth = chartRight - chartLeft

        val guides = listOf(
            100 to chartTop,
            50 to chartTop + chartHeight / 2f,
            0 to chartBottom
        )

        guides.forEach { (percentage, y) ->

            val label = textMeasurer.measure(
                text = "$percentage%",
                style = AppTypography.Caption2.copy(
                    color = labelColor
                )
            )

            drawText(
                textLayoutResult = label,
                topLeft = Offset(
                    x = 0f,
                    y = y - label.size.height / 2f
                )
            )

            drawLine(
                color = if(percentage == 0) AppColor.CTA_secondary_hover else gridColor,
                start = Offset(chartLeft, y),
                end = Offset(chartRight, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val slotWidth = chartWidth / 6
        val barWidth = minOf(
            40.dp.toPx(),
            slotWidth * 0.55f
        )

        sessions.forEachIndexed { index, session ->
            val centerX = chartLeft + slotWidth * index + slotWidth / 2f
            val barLeft = centerX - barWidth / 2f
            val accuracyHeight = chartHeight * (session.accuracyScore / 100f)
            val deliveryHeight = chartHeight * (session.deliveryScore / 100f)
            val accuracyTop = chartBottom - accuracyHeight
            val deliveryTop = accuracyTop - deliveryHeight

            drawRect(
                color = accuracyColor,
                topLeft = Offset(
                    x = barLeft,
                    y = accuracyTop
                ),
                size = Size(
                    width = barWidth,
                    height = accuracyHeight
                )
            )

            val radius = 8.dp.toPx()
            val deliveryPath = Path().apply {
                moveTo(barLeft, accuracyTop)
                lineTo(barLeft, deliveryTop + radius)
                quadraticTo(
                    barLeft,
                    deliveryTop,
                    barLeft + radius,
                    deliveryTop
                )
                lineTo(barLeft + barWidth - radius, deliveryTop)
                quadraticTo(
                    barLeft + barWidth,
                    deliveryTop,
                    barLeft + barWidth,
                    deliveryTop + radius
                )
                lineTo(barLeft + barWidth, accuracyTop)
                close()
            }
            drawPath(
                path = deliveryPath,
                color = deliveryColor
            )

            if (session.accuracyScore > 0) {
                val accuracyText = textMeasurer.measure(
                    text = session.accuracyScore.toString(),
                    style = AppTypography.Caption2.copy(
                        color = AppColor.bg0
                    )
                )

                drawText(
                    textLayoutResult = accuracyText,
                    topLeft = Offset(
                        x = centerX - accuracyText.size.width / 2f,
                        y = accuracyTop +
                                accuracyHeight / 2f -
                                accuracyText.size.height / 2f
                    )
                )
            }

            if (session.deliveryScore > 0) {
                val deliveryText = textMeasurer.measure(
                    text = session.deliveryScore.toString(),
                    style = AppTypography.Caption2.copy(
                        color = AppColor.text3
                    )
                )

                drawText(
                    textLayoutResult = deliveryText,
                    topLeft = Offset(
                        x = centerX - deliveryText.size.width / 2f,
                        y = deliveryTop +
                                deliveryHeight / 2f -
                                deliveryText.size.height / 2f
                    )
                )
            }

            val sequenceText = textMeasurer.measure(
                text = "Q${session.sequence}",
                style = AppTypography.Caption2.copy(
                    color = AppColor.text2
                )
            )

            drawText(
                textLayoutResult = sequenceText,
                topLeft = Offset(
                    x = centerX - sequenceText.size.width / 2f,
                    y = chartBottom + 8.dp.toPx()
                )
            )
        }
    }
}
@Composable
fun ScoreGauge(
    score: Int,
    maxScore: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val progress = if (maxScore > 0) {
            (score.toFloat() / maxScore).coerceIn(0f, 1f)
        } else {
            0f
        }

        Canvas(modifier = modifier) {
            val strokeWidth = 9.dp.toPx() //두께(중심선을 기준으로 양옆으로 두께가 늘어남)
            val inset = strokeWidth / 2f //늘어난만큼 안으로 들어오게

            val startAngle = 126f
            val sweepAngle = 288f //126도부터 몇 도까지 그릴 건지

            val arcSize = Size( //size = 캔버스 크기
                width = size.width - strokeWidth, //늘어난만큼 안으로 들어왔기에 원도 그 안쪽에서 그려져야 함
                height = size.height - strokeWidth
            )
            val progressSweep = sweepAngle * progress

            drawArc(
                color = PrimitiveColor.Purple100,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = PrimitiveColor.Purple400,
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            val endAngle = Math.toRadians(//보라색 원호가 끝나는 지점
                (startAngle + progressSweep).toDouble()
            )

            val center = Offset( //원 중심점
                x = size.width / 2f,
                y = size.height / 2f
            )

            val radius = arcSize.width / 2f //원 반지름

            val endPoint = Offset(//원의 어떤 각도에 해당하는 좌표
                x = center.x + radius * cos(endAngle).toFloat(),
                y = center.y + radius * sin(endAngle).toFloat()
            )
            drawCircle( //그림자
                color = AppColor.text1.copy(alpha = 0.1f),
                radius = 7.dp.toPx(),
                center = endPoint + Offset(-0.5f, 0f)
            )
            drawCircle(
                color = AppColor.bg0,
                radius = 7.dp.toPx(),
                center = endPoint
            )
            drawCircle(
                color = PrimitiveColor.Purple400,
                radius = 4.dp.toPx(),
                center = endPoint
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$score",
                    style = AppTypography.Headline1,
                    color = AppColor.text1
                )

                Text(
                    text = "/$maxScore",
                    style = AppTypography.Body1_Normal,
                    color = AppColor.text4
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "총점",
                style = AppTypography.Caption1,
                color = AppColor.Main2
            )
        }
    }
}

@Composable
fun DetailUi(
    sessionAnswers: InterviewSessionAnswersResponse,
    expandedInquiryIds: Set<Int>,
    onInquiryClick: (Int) -> Unit,
    onPageClick: (Int) -> Unit,
    playingAnswerNumber: Int?,
    onPlayingAnswerChange: (Int?) -> Unit
) {
    val expandedAnswerOrders = remember { mutableStateOf(setOf<Int>()) }

    Box (
        modifier = Modifier.fillMaxSize()
    ) {
        Column(

        ) {
            Text(
                text = sessionAnswers.startedAt.substringBefore("T"),
                color = AppColor.text3w,
                style = AppTypography.Caption2
            )
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sessionAnswers.answers.forEach { item ->
                    DetailItem(
                        detail = item,
                        onClick = onInquiryClick,
                        expanded = item.displayOrder in expandedInquiryIds,
                        playingAnswerNumber = playingAnswerNumber,
                        onPlayingAnswerChange = onPlayingAnswerChange
                    )
                }
            }
        }
    }
}
@Composable
fun DetailItem(
    detail: InterviewSessionAnswer,
    onClick: (Int) -> Unit,
    expanded: Boolean,
    playingAnswerNumber: Int?,
    onPlayingAnswerChange: (Int?) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.bg0)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(detail.displayOrder) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${detail.displayOrder}. ${detail.topic.displayName}",
                style = AppTypography.Headline2,
                color = AppColor.Main2
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "펼쳐서 보기",
                style = AppTypography.Caption1,
                color = AppColor.text4
            )

            Spacer(Modifier.width(4.dp))

            Icon(
                painter = painterResource(
                    if (expanded) R.drawable.chevron_up
                    else R.drawable.chevron_down
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = AppColor.icon_disabled
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = detail.questionContent,
                        color = AppColor.text1,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = pretendard
                    )
                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 146.dp)
                            .clip(RoundedCornerShape(AppRadius.sm))
                            .border(
                                1.dp,
                                AppColor.divider1,
                                RoundedCornerShape(AppRadius.sm)
                            )
                            .background(AppColor.bg1)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "내 답변",
                            style = AppTypography.Caption1,
                            color = AppColor.text4
                        )
                        Spacer(Modifier.height(4.dp))
                        detail.answerContent?.let {
                            Text(
                                text = it,
                                style = AppTypography.Body2_Reading,
                                color = AppColor.text1
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Spacer(Modifier.weight(1f))
                        AiAnswerAudioPlayer(
                            audioUrl = detail.audioUrl,
                            isSelected = playingAnswerNumber == detail.displayOrder,
                            pauseForExitDialog = false,
                            onPlay = {
                                onPlayingAnswerChange(detail.displayOrder)
                            },
                            onStop = {
                                if (playingAnswerNumber == detail.displayOrder) {
                                    onPlayingAnswerChange(null)
                                }
                            }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .border(
                                1.dp,
                                PrimitiveColor.Purple200,
                                RoundedCornerShape(AppRadius.sm)
                            )
                            .background(PrimitiveColor.Purple50)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "모범답안",
                                style = AppTypography.Caption1,
                                color = AppColor.Main1
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = detail.modelAnswer,
                                style = AppTypography.Body2_Reading,
                                color = AppColor.text1
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "항목별 점수",
                        color = AppColor.text1,
                        style = AppTypography.Headline2
                    )

                    Spacer(Modifier.height(16.dp))

                    ScoreItem(
                        title = "정확도",
                        score = detail.accuracyScore,
                        maxScore = detail.accuracyMaxScore
                    )

                    Spacer(Modifier.height(8.dp))

                    ScoreItem(
                        title = "구조성",
                        score = detail.structureScore,
                        maxScore = detail.structureMaxScore
                    )

                    Spacer(Modifier.height(8.dp))

                    ScoreItem(
                        title = "명료성",
                        score = detail.clarityScore,
                        maxScore = detail.clarityMaxScore
                    )
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (detail.concepts.isNotEmpty()) {
                        Text(
                            text = "핵심 개념",
                            style = AppTypography.Headline2,
                            color = AppColor.text1
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            detail.concepts.forEach { concept ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AppColor.bg1)
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.circle_check),
                                            contentDescription = "",
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = concept.name,
                                            style = AppTypography.Body2_Reading,
                                            color = AppColor.text2,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "개선 피드백",
                        color = AppColor.text1,
                        style = AppTypography.Headline2
                    )
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(AppColor.bg1)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        MarkdownContent(
                            content = detail.improvementSuggestion.orEmpty()
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun ScoreItem(
    title: String,
    score: Int,
    maxScore: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(83.dp)
            .clip(RoundedCornerShape(AppRadius.sm))
            .border(1.dp, AppColor.divider1, RoundedCornerShape(AppRadius.sm))
            .background(AppColor.bg1)
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = AppColor.text1,
                    style = AppTypography.Headline2
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = "${score}점",
                    color = AppColor.Main1,
                    style = AppTypography.Caption1
                )
            }

            Spacer(Modifier.height(10.dp))

            ScoreBar(
                score = score,
                maxScore = maxScore
            )
        }
    }
}
@Composable
private fun ScoreBar(
    score: Int,
    maxScore: Int
) {
    val points = listOf(
        (maxScore + 2) / 3,
        (maxScore * 2 + 2) / 3,
        maxScore
    )

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        ) {
            val gap = 8.dp.toPx()
            val segmentWidth = (size.width - gap * 2) / 3
            val radius = size.height / 2

            val gradient = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFDD00FF),
                    Color(0xFFFF5DE9)
                ),
                startX = 0f,
                endX = size.width
            )

            repeat(3) { index ->
                val left = index * (segmentWidth + gap)

                drawRoundRect(
                    color = AppColor.bg3,
                    topLeft = Offset(left, 0f),
                    size = Size(segmentWidth, size.height),
                    cornerRadius = CornerRadius(radius, radius)
                )

                val segmentStart = index / 3f
                val segmentEnd = (index + 1) / 3f
                val totalProgress = score.toFloat() / maxScore.toFloat()

                val segmentProgress = (
                        (totalProgress - segmentStart) /
                                (segmentEnd - segmentStart)
                        ).coerceIn(0f, 1f)

                if (segmentProgress > 0f) {
                    drawRoundRect(
                        brush = gradient,
                        topLeft = Offset(left, 0f),
                        size = Size(
                            segmentWidth * segmentProgress,
                            size.height
                        ),
                        cornerRadius = CornerRadius(radius, radius)
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            points.forEach { point ->
                Text(
                    text = "${point}점",
                    color = AppColor.text4,
                    style = AppTypography.Caption1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}