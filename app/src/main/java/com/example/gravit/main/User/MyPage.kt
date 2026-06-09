package com.inuappcenter.gravit.main.User

import android.annotation.SuppressLint
import android.graphics.BlurMaskFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.InlineButton
import com.example.gravit.ui.theme.InlineButtonState
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.DailySolvedCounts
import com.inuappcenter.gravit.api.MyPageBanner
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.SeasonHistory
import com.inuappcenter.gravit.main.Home.RoundedGauge
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.collections.associate
import kotlin.collections.distinctBy
import kotlin.collections.forEachIndexed
import kotlin.collections.mapIndexed
import kotlin.math.ceil

enum class MyPageTab {
    Summary,
    Learning,
    League,
    Social
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPage(
    navController: NavController,
    onSessionExpired: () -> Unit
) {
    val context = LocalContext.current
    val vm: UserScreenVM = viewModel(factory = UserVMFactory(RetrofitInstance.api, context))
    MyPageUI(vm, navController)
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPageUI(
    vm: UserScreenVM = viewModel(factory = UserVMFactory(RetrofitInstance.api, LocalContext.current)),
    navController: NavController,
) {

    var selectedTab by remember { mutableStateOf(MyPageTab.Summary) }

    val ui by vm.stateBanners.collectAsState()
    LaunchedEffect(Unit) { vm.loadBanners() }
    val banners = (ui as? UserScreenVM.BannersUiState.Success)?.data

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg2)
            .padding(WindowInsets.statusBars.asPaddingValues()),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            MyPageProfileHeader(banners,navController)
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                MyPageTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                when (selectedTab) {
                    MyPageTab.Summary -> SummaryUI(vm)
                    MyPageTab.Learning -> LearningTabUI(vm)
                    MyPageTab.League -> LeagueTabUI(vm)
                    MyPageTab.Social -> SocialTabUI(navController, vm)
                }
            }
        }
    }
}
@Composable
fun MyPageProfileHeader(
    banner: MyPageBanner?,
    navController: NavController,
) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(195.dp)
    ){
        Image(
            painter = painterResource(id = R.drawable.mypage_bg),
            contentDescription = "main back",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.height(70.dp)) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(ProfilePalette.idToColor(banner?.profileImgNumber ?: 0)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_logo),
                        contentDescription = "profile logo",
                        modifier = Modifier.size(32.dp, 40.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.padding(vertical = 7.5.dp)) {
                    Text(
                        text = "${banner?.nickname}",
                        style = AppTypography.Heading2,
                        color = AppColor.text1w
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ProfileBox("LV ${banner?.level}", 42.dp)
                        ProfileBox("${banner?.currentLeague}", 58.dp)
                    }
                }

                Spacer(Modifier.weight(1f))
                Icon(painter = painterResource(id = R.drawable.bell),
                    contentDescription = "bell",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("user/notice2")
                        },
                    tint = AppColor.icon_w
                )
                Spacer(Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.setting),
                    contentDescription = "setting",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("user/setting")
                        },
                    tint = AppColor.icon_w

                )
            }

            Text(
                text = "@${banner?.handle}",
                style = AppTypography.Label2,
                color = PrimitiveColor.Gray400
            )

            InlineButton(
                "프로필 편집",
                onClick = { navController.navigate("user/account") },
                state = InlineButtonState.Secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(37.dp),
                style = AppTypography.Body2_Nomal,
                color = AppColor.text3
            )
        }
    }
}
@Composable
fun ProfileBox(
    text: String,
    width: Dp
){
    Box(
        modifier = Modifier
            .size(width, 22.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(PrimitiveColor.Gray500.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.Caption1,
            color = PrimitiveColor.Gray50
        )
    }
}
@Composable
fun MyPageTabRow(
    selectedTab: MyPageTab,
    onTabSelected: (MyPageTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.bg1)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TapButton(
            text = "요약",
            selected = selectedTab == MyPageTab.Summary,
            onClick = {
                onTabSelected(MyPageTab.Summary)
            },
            modifier = Modifier.weight(1f)
        )
        TapButton(
            text = "학습",
            selected = selectedTab == MyPageTab.Learning,
            onClick = {
                onTabSelected(MyPageTab.Learning)
            },
            modifier = Modifier.weight(1f)
        )

        TapButton(
            text = "리그",
            selected = selectedTab == MyPageTab.League,
            onClick = {
                onTabSelected(MyPageTab.League)
            },
            modifier = Modifier.weight(1f)
        )

        TapButton(
            text = "소셜",
            selected = selectedTab == MyPageTab.Social,
            onClick = {
                onTabSelected(MyPageTab.Social)
            },
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
fun TapButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean = true,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
        modifier = modifier
            .height(38.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if(selected) AppColor.Main2 else AppColor.bg1,
            contentColor = if(selected) AppColor.text1w else AppColor.text3
        )
    ) {
        Text(
            text = text,
            style = AppTypography.Label1
        )
    }
}
@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SummaryUI(
    vm: UserScreenVM = viewModel(factory = UserVMFactory(RetrofitInstance.api, LocalContext.current))
) {
    val ui by vm.stateSummary.collectAsState()
    LaunchedEffect(Unit) { vm.loadSummary() }
    val summaries = (ui as? UserScreenVM.SummaryUiState.Success)?.data

    val currentMonth = LocalDate.now().monthValue
    var isFirstHalf by remember { mutableStateOf(currentMonth <= 6) }
    val months =
        if (isFirstHalf) {
            (1..6).toList()
        } else {
            (7..12).toList()
        }
    val colorCube = listOf(AppColor.bg1, PrimitiveColor.Purple200, PrimitiveColor.Purple300,PrimitiveColor.Purple500,PrimitiveColor.Purple700)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimitiveColor.Purple50),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.crown),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp, 18.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "상위 ${summaries?.learningSummary?.topPercent}%",
                            style = AppTypography.Title3,
                            color = AppColor.Main1
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "전체 학습 순위",
                            style = AppTypography.Caption1,
                            color = AppColor.text4
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
                val rankInfo =
                    listOf("${summaries?.learningSummary?.completedLessonCount}개", "완료 레슨", "${String.format("%.1f", summaries?.learningSummary?.totalLearningHours)}h", "총 학습시간", "${summaries?.learningSummary?.averageAccuracy}%", "평균 정답률").chunked(2)
                RankRow(rankInfo)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "학습기록",
                    style = AppTypography.Label2,
                    color = AppColor.text4
                )
                Text(
                    text = "${(summaries?.years[0] ?: 2026)}년",
                    style = AppTypography.Headline2,
                    color = AppColor.text1
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
                LearningGrassGrid(
                    year = summaries?.years[0] ?: 2026,
                    dailySolvedCounts = summaries?.learningHistory?.dailySolvedCounts ?: emptyList()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "적음",
                        style = AppTypography.Caption1,
                        color = AppColor.text3
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        colorCube.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                        }
                    }
                    Text(
                        text = "많음",
                        style = AppTypography.Caption1,
                        color = AppColor.text3
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, AppColor.bg3, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                )
                {
                    var peek = summaries?.learningHistory?.peakLearningHour.toString()
                    if (peek == "-1") {
                        peek = "-"
                    }
                    Text(
                        text = "주로 ${peek}시에 학습하시네요.",
                        style = AppTypography.Caption1,
                        color = AppColor.text3
                    )
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LearningGrassGrid(
    year: Int=2026,
    dailySolvedCounts: List<DailySolvedCounts>,
) {
    val startDate = LocalDate.of(year, 1, 1)
    val endDate = LocalDate.of(year, 12, 31)

    val solvedMap = dailySolvedCounts.associate {
        LocalDate.parse(it.date) to it.solvedLessonCount
    }

    val dates = generateSequence(startDate) { it.plusDays(1) }
        .takeWhile { !it.isAfter(endDate) }
        .toList()

    val rows = 10
    val cellSize = 12.dp
    val cellGap = 4.dp
    val columnWidth = cellSize + cellGap

    val columns = ceil(dates.size / rows.toFloat()).toInt()
    val totalWidth = columns * columnWidth

    Column(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .width(totalWidth)
    ) {
        Box(
            modifier = Modifier
                .width(totalWidth)
                .height(18.dp)
        ) {
            (1..12).forEach { month ->
                val monthStart = LocalDate.of(year, month, 1)
                val dayIndex = ChronoUnit.DAYS.between(startDate, monthStart).toInt()
                val columnIndex = dayIndex / rows

                Text(
                    text = "${month}월",
                    style = AppTypography.Caption1,
                    color = AppColor.text4,
                    modifier = Modifier.offset(
                        x = columnIndex * columnWidth
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(cellGap)
        ) {
            repeat(columns) { column ->
                Column(verticalArrangement = Arrangement.spacedBy(cellGap)) {
                    repeat(rows) { row ->
                        val index = column * rows + row
                        val date = dates.getOrNull(index)

                        if (date != null) {
                            val solvedCount = solvedMap[date] ?: 0

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(getGrassColor(solvedCount))
                            )
                        } else {
                            Spacer(Modifier.size(cellSize))
                        }
                    }
                }
            }
        }
    }
}

fun getGrassColor(count: Int): Color {
    return when (count) {
        0 -> AppColor.bg1
        in 1..2 -> PrimitiveColor.Purple200
        in 3..4 -> PrimitiveColor.Purple300
        in 5..7 -> PrimitiveColor.Purple500
        else -> PrimitiveColor.Purple700
    }
}
data class DailySolvedCount(
    val date: String,
    val solvedLessonCount: Int
)

data class DailyStudy(
    val day: String,
    val count: Int
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LearningTabUI(
    vm: UserScreenVM = viewModel(factory = UserVMFactory(RetrofitInstance.api, LocalContext.current))
) {
    val ui by vm.stateLearning.collectAsState()
    LaunchedEffect(Unit) { vm.loadLearning() }
    val learning = (ui as? UserScreenVM.LearningUiState.Success)?.data
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp),
        ){
            Column {
                Text(
                    text = "일별 완료한 레슨 수",
                    textAlign = TextAlign.Start,
                    style = AppTypography.Label2,
                    color = AppColor.text4
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val daily = listOf(
                        DailyStudy("월", learning?.weeklyReport?.MONDAY ?: 0),
                        DailyStudy("화", learning?.weeklyReport?.TUESDAY ?: 0),
                        DailyStudy("수", learning?.weeklyReport?.WEDNESDAY ?: 0),
                        DailyStudy("목", learning?.weeklyReport?.THURSDAY ?: 0),
                        DailyStudy("금", learning?.weeklyReport?.FRIDAY ?: 0),
                        DailyStudy("토", learning?.weeklyReport?.SATURDAY ?: 0),
                        DailyStudy("일", learning?.weeklyReport?.SUNDAY ?: 0)
                    )
                    DailyGraph(daily)
                }
                Spacer(Modifier.height(32.dp))
                Column (
                    modifier = Modifier.padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ){
                    Text(
                        text = "이번 주 완료 레슨",
                        style = AppTypography.Label1,
                        color = AppColor.text1
                    )
                    Text(
                        text = "${learning?.weeklyReport?.thisWeekCompletedLessonCount}개",
                        style = AppTypography.Heading1,
                        color = AppColor.text1
                    )
                }
                Spacer(Modifier.height(16.dp))
                val deltas = learning?.weeklyReport?.weekOverWeekDeltas.orEmpty()
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(3) { index ->
                        val delta = deltas.getOrNull(index) ?: 0
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, AppColor.bg3, RoundedCornerShape(4.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}주 전 대비",
                                    style = AppTypography.Caption1,
                                    color = AppColor.text3
                                )

                                Spacer(Modifier.weight(1f))

                                Text(
                                    text = "${delta}개",
                                    style = AppTypography.Caption1,
                                    color = if (delta < 0) Color(0xFF1FABFF) else AppColor.Main1
                                )
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
                Text(
                    text = "취약 개념 TOP7",
                    textAlign = TextAlign.Start,
                    style = AppTypography.Label2,
                    color = AppColor.text4
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "어떤 주제에 집중했나요?",
                    textAlign = TextAlign.Start,
                    style = AppTypography.Headline2,
                    color = AppColor.text1
                )
                Spacer(Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    learning?.topChapters?.forEach { it ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimitiveColor.Gray200)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(50f))
                                    .background(PrimitiveColor.Gray800),
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = "${it.rank}",
                                    style = AppTypography.Web_Btn_S_Caption1,
                                    color = AppColor.text1w
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column() {
                                Row {
                                    Text(
                                        text = it.chapterTitle,
                                        style = AppTypography.Headline2,
                                        color = AppColor.text2
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = "${it.solvedLessonCount}개",
                                        style = AppTypography.Body1_Nomal,
                                        color = AppColor.text2
                                    )
                                }
                                Spacer(Modifier.height(7.dp))
                                RoundedGauge(
                                    rate = it.ratio.toFloat(),
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp),
        ){
            Column {
                Text(
                    text = "이번 주 가장 많이 푼 챕터",
                    textAlign = TextAlign.Start,
                    style = AppTypography.Label2,
                    color = AppColor.text4
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "어떤 주제에 집중했나요?",
                    textAlign = TextAlign.Start,
                    style = AppTypography.Headline2,
                    color = AppColor.text1
                )
                Spacer(Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    learning?.weakConcepts?.forEach { it ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimitiveColor.Gray200)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(50f))
                                .background(PrimitiveColor.Gray800),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "${it.rank}",
                                style = AppTypography.Web_Btn_S_Caption1,
                                color = AppColor.text1w
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column {
                                Text(
                                    text = it.unitTitle,
                                    style = AppTypography.Label1,
                                    color = AppColor.text2
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${it.chapterTitle} • ${it.wrongAnswerCount}문제 오답",
                                    style = AppTypography.Label2,
                                    color = AppColor.text4
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Box(
                                modifier = Modifier
                                    .size(41.dp, 22.dp)
                                    .clip(RoundedCornerShape(60.dp))
                                    .background(AppColor.bg1)
                                    .border(1.dp, AppColor.Main2, RoundedCornerShape(60.dp)),
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = "${it.wrongAnswerRate}%",
                                    style = AppTypography.Caption1,
                                    color = AppColor.Main2
                                )
                            }
                        }
                    }
                } }

            }
        }
    }
}

@Composable
fun DailyGraph(
    daily: List<DailyStudy>
){
    val maxValue = daily.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    daily.forEach { item ->
        val num = item.count
        val day = item.day

        val height = if (num == 0) {
            4.dp
        } else {
            (124.dp - 4.dp) * (num.toFloat() / maxValue.toFloat())
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(34.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            Text(
                text = "${num}개",
                style = AppTypography.Caption1,
                color = if (num == 0) AppColor.text4 else AppColor.Main1
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (num == 0) Color(0xFFD9D9D9) else AppColor.Main1)
            )

            Text(
                text = day,
                style = AppTypography.Caption1,
                color = AppColor.text4
            )
        }
    }
}
@Composable
fun ReportBox(
    text: String,
    onClick: () -> Unit,
    image: Int
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        verticalAlignment = Alignment.CenterVertically){
        Image(painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(38.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = AppTypography.Label1,
            color = AppColor.text1
        )
        Spacer(Modifier.weight(1f))
        Image(painter = painterResource(id = R.drawable.chevron_right),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onClick))
    }
}
@Composable
fun RankRow(
    rankInfo: List<List<String>>,
    isLeague: Boolean = false
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        rankInfo.forEachIndexed { index, item ->

            RankInfo(
                value = item[0],
                label = item[1],
                modifier = Modifier.weight(1f),
                color = if(index==0 && isLeague) AppColor.Main1 else AppColor.text1,
                onClick = {}
            )

            if (index != rankInfo.lastIndex) {

                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    thickness = 1.dp,
                    color = AppColor.divider1
                )
            }
        }
    }
}
@Composable
fun RankInfo(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = AppColor.text1,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = AppTypography.Headline2,
            color = color
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            style = AppTypography.Caption1,
            color = AppColor.text4
        )
    }
}
@Composable
fun LeagueTabUI(
    vm: UserScreenVM = viewModel(factory = UserVMFactory(RetrofitInstance.api, LocalContext.current))
) {
    val ui by vm.stateLeague.collectAsState()
    LaunchedEffect(Unit) { vm.loadLeague() }
    val league = (ui as? UserScreenVM.LeargueUiState.Success)?.data

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    Text(
                        text = "리그 시즌 히스토리",
                        style = AppTypography.Label2,
                        color = AppColor.text4
                    )
                    Text(
                        text = "시즌별 최종 티어 기록",
                        style = AppTypography.Headline2,
                        color = AppColor.text1
                    )
                }
                val rankinfo = listOf("${league?.currentSeasonRank}위", "현재 시즌 순위", "${league?.top3SeasonCount}회", "3위권 진입", "${league?.bestLeagueName}", "최고티어").chunked(2)
                RankRow(rankinfo, true)
                TierChart(
                    histories = league?.seasonHistory.orEmpty(),
                    modifier = Modifier
                        .padding(top = 30.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .height(150.dp)
                )

            }


        }
    }
}
@Composable
fun TierChart(
    histories: List<SeasonHistory>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
    ) {
        if (histories.isEmpty()) return@Canvas

        val count = histories.size

        val topPadding = 30f
        val bottomPadding = 30f

        val uniqueLeagues = histories
            .distinctBy { it.sortOrder }
            .sortedByDescending { it.sortOrder }

        val orderTextStyle = AppTypography.Caption2

        val labelLayouts = uniqueLeagues.associateWith { league ->
            textMeasurer.measure(
                text = league.leagueName,
                style = orderTextStyle
            )
        }

        val maxLabelWidth = labelLayouts.values.maxOf { it.size.width }.toFloat()
        val maxLabelHeight = labelLayouts.values.maxOf { it.size.height }.toFloat()

        val labelStartX = 30f
        val labelGraphGap = 70f

        val graphStartX = labelStartX + maxLabelWidth + labelGraphGap
        val graphEndPadding = 70f
        val graphWidth = size.width - graphStartX - graphEndPadding

        val xGap =
            if (count > 1) graphWidth / (count - 1)
            else 0f

        val labelTop = topPadding + maxLabelHeight / 2f
        val labelBottom = size.height - bottomPadding - maxLabelHeight / 2f

        val chartHeight = labelBottom - labelTop - 30f

        val yGap =
            if (uniqueLeagues.size > 1)
                chartHeight / (uniqueLeagues.size - 1)
            else
                0f

        val orderToY =
            if (uniqueLeagues.size == 1) {
                mapOf(
                    uniqueLeagues.first().sortOrder to labelBottom - 40f
                )
            } else {
                uniqueLeagues
                    .mapIndexed { index, history ->
                        history.sortOrder to (labelTop + index * yGap)
                    }
                    .toMap()
            }

        uniqueLeagues.forEach { league ->
            val y = orderToY[league.sortOrder] ?: return@forEach
            val textLayout = labelLayouts[league] ?: return@forEach

            drawText(
                textMeasurer = textMeasurer,
                text = league.leagueName,
                style = orderTextStyle.copy(color = AppColor.text3),
                topLeft = Offset(
                    x = labelStartX,
                    y = y - textLayout.size.height / 2f
                )
            )
            drawLine(
                color = AppColor.divider1,
                start = Offset(
                    x = graphStartX - 50f,
                    y = y
                ),
                end = Offset(
                    x = size.width - 50f,
                    y = y
                ),
                strokeWidth = 1f
            )
        }
        val bottomY = orderToY[uniqueLeagues.last().sortOrder] ?: return@Canvas
        val points = histories.mapIndexed { index, history ->
            Offset(
                x = graphStartX + index * xGap,
                y = orderToY[history.sortOrder]!!
            )
        }
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)

            points.drop(1).forEach {
                lineTo(it.x, it.y)
            }
        }
        val fillPath = Path().apply {
            addPath(path)

            lineTo(points.last().x, bottomY)
            lineTo(points.first().x, bottomY)

            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    AppColor.Main2.copy(0.9f), Color.Transparent
                ),
                startY = 0f,
                endY = bottomY
            )
        )
        drawPath(
            path = path,
            color = AppColor.Main2,
            style = Stroke(width = 2f)
        )
        histories.forEachIndexed { index, history ->
            val y = orderToY[history.sortOrder] ?: return@forEachIndexed

            val point = Offset(
                x = graphStartX + index * xGap,
                y = y
            )
            if (history.isCurrent) {
                drawLine(
                    color = AppColor.Main2,
                    start = point,
                    end = Offset(
                        x = point.x,
                        y = bottomY
                    ),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(10f, 7f)
                    )
                )
                drawIntoCanvas { canvas ->
                    val glowPaint = Paint().asFrameworkPaint().apply {
                        color = AppColor.Main2.toArgb()
                        maskFilter = BlurMaskFilter(
                            15f, // 퍼지는 정도
                            BlurMaskFilter.Blur.NORMAL
                        )
                    }

                    canvas.nativeCanvas.drawCircle(
                        point.x,
                        point.y,
                        12f, // 글로우 반지름
                        glowPaint
                    )
                }

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
            val text = if(history.isCurrent) "현재" else "S${index + 1}"
            val textLayout = textMeasurer.measure(
                text = text,
                style = orderTextStyle
            )

            drawText(
                textMeasurer = textMeasurer,
                text = text,
                style = orderTextStyle.copy(color = if(history.isCurrent) AppColor.Main2 else Color(0xFF646464)),
                topLeft = Offset(
                    x = point.x - textLayout.size.width / 2f,
                    y = bottomY + 30f
                )
            )
        }
    }
}
@Composable
fun SocialTabUI(
    navController: NavController,
    vm: UserScreenVM = viewModel(factory = UserVMFactory(RetrofitInstance.api, LocalContext.current))
) {

    val ui by vm.stateSocial.collectAsState()
    LaunchedEffect(Unit) {
        vm.loadSocial()
        Log.d("SOCIAL_UI", ui.toString())
    }
    val social = (ui as? UserScreenVM.SocialUiState.Success)?.data

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
            Column() {
                val socialInfo = listOf("${social?.count?.followerCount}", "팔로우", "${social?.count?.followingCount}", "팔로잉").chunked(2)
                val isLeague = false
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    socialInfo.forEachIndexed { index, item ->

                        RankInfo(
                            value = item[0],
                            label = item[1],
                            modifier = Modifier.weight(1f),
                            color = if(index==0 && isLeague) AppColor.Main1 else AppColor.text1,
                            onClick = if(item[1] == "팔로우") {
                                {navController.navigate("user/followList?tab=followers") { launchSingleTop = true } }
                            } else {
                                {navController.navigate("user/followList?tab=following") { launchSingleTop = true }}
                            }
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
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "친구 활동",
                    style = AppTypography.Label2,
                    color = PrimitiveColor.Gray500
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "팔로잉한 친구들의 최근 성취",
                    style = AppTypography.Headline2,
                    color = PrimitiveColor.Gray900
                )
                Spacer(Modifier.height(8.dp))
                Column {
                    social?.feed?.contents?.forEach { feed ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(78.dp)
                                .padding(vertical = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(ProfilePalette.idToColor(feed.actorProfileImgNumber)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_logo),
                                    contentDescription = "profile logo",
                                    modifier = Modifier.size(18.dp, 20.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column() {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = feed.actorNickname,
                                        style = AppTypography.Label1,
                                        color = AppColor.text1
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "2시간 전",
                                        style = AppTypography.Caption1,
                                        color = AppColor.text4
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = feed.message,
                                    style = AppTypography.Label2,
                                    color = AppColor.text3
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            InlineButton(
                                text = "축하하기",
                                state = InlineButtonState.Default,
                                onClick = {},
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(77.dp),
                                style = AppTypography.Label2,
                                color = AppColor.CTA_text
                            )
                        }
                        HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)

                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "추천 친구",
                        style = AppTypography.Label2,
                        color = PrimitiveColor.Gray500
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "비슷한 레벨의 학습자들",
                        style = AppTypography.Headline2,
                        color = PrimitiveColor.Gray900
                    )
                }
                Spacer(Modifier.height(16.dp))
                LazyRow(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(social?.recommend.orEmpty()) { recommend ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(144.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(AppColor.bg1),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(ProfilePalette.idToColor(recommend.profileImgNumber)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.profile_logo),
                                        contentDescription = "profile logo",
                                        modifier = Modifier.size(21.dp, 26.dp)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = recommend.nickname,
                                    style = AppTypography.Label1,
                                    color = AppColor.text1
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "내이름님 외 ${recommend.mutualFollowCount}명",
                                    style = AppTypography.Caption1,
                                    color = AppColor.text4
                                )
                                Spacer(Modifier.weight(1f))
                                InlineButton(
                                    text = "+ 팔로우",
                                    state = InlineButtonState.Stroke_Color,
                                    onClick = { vm.followRecommend(recommend.userId.toLong())},
                                    modifier = Modifier
                                        .height(26.dp)
                                        .width(104.dp),
                                    style = AppTypography.Caption2,
                                    color = AppColor.Main2,
                                    shape = RoundedCornerShape(4.dp),
                                    padding = 4.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendsRow(
){
    Row(
        Modifier
            .fillMaxWidth()
            .height(78.dp)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ProfilePalette.idToColor(1)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile logo",
                modifier = Modifier.size(18.dp, 20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "김나영",
                    style = AppTypography.Label1,
                    color = AppColor.text1
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "2시간 전",
                    style = AppTypography.Caption1,
                    color = AppColor.text4
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "자료구조 행성을 정복했어요!",
                style = AppTypography.Label2,
                color = AppColor.text3
            )
        }
        Spacer(Modifier.weight(1f))
        InlineButton(
            text = "축하하기",
            state = InlineButtonState.Default,
            onClick = {},
            modifier = Modifier
                .height(32.dp)
                .width(77.dp),
            style = AppTypography.Label2,
            color = AppColor.CTA_text
        )
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
}
//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun MyPagePreview() {
//    MyPageUI()
//}