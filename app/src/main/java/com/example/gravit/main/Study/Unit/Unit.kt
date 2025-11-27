package com.example.gravit.main.Study.Unit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.ui.theme.Responsive
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.api.UnitDetails
import com.example.gravit.navigation.toLesson
import com.example.gravit.ui.theme.pretendard
import kotlin.collections.forEachIndexed
import kotlin.collections.getOrNull

data class PlanetState(
    val name: String,
    val isUnlocked: Boolean, // selected 상태 여부
    val progress: Int // 진행도
)

@Composable
fun Unit(
    navController: NavController,
    chapterId: Int,
    onSessionExpired: () -> Unit
){
    val context = LocalContext.current
    val vm: UnitViewModel = viewModel(
        factory = UnitVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()
    LaunchedEffect(chapterId) { vm.load(chapterId) }


    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(ui) {
        when (ui) {
            UnitViewModel.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            UnitViewModel.UiState.NotFound ->  {
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            UnitViewModel.UiState.Failed ->  {
                navigated = true
                onSessionExpired()
            }

            else -> Unit
        }
    }

    val success = ui as? UnitViewModel.UiState.Success
    val units = success?.data?.unitDetails.orEmpty()
    val chapterName = success?.data?.chapterName
    val chapterDescription = success?.data?.chapterDescription

    val unitSlots: List<UnitDetails?> = remember(units) {
        val count = units.size + 1
        List(count) { idx -> units.getOrNull(idx) }   // nullable 리스트
    }

    val planet: Map<Int, Int> = mapOf(
        1 to R.drawable.data_structure_planet,
        2 to R.drawable.algorithm_planet,
        3 to R.drawable.computer_network_planet,
        4 to R.drawable.operating_system_planet,
        5 to R.drawable.database_planet,
        6 to R.drawable.computer_security_planet,
        7 to R.drawable.software_engineering_planet,
        8 to R.drawable.programming_language_planet
    )
    val img = planet[chapterId] ?: 1

    var selectedUnitIndex by remember { mutableStateOf<Int?>(null) }

    Box (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image( //베경 이미지
                painter = painterResource(id = R.drawable.unitback),
                contentDescription = "background",
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                contentScale = ContentScale.FillWidth

            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column (modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = Responsive.w(20f), end = Responsive.w(20f), top = Responsive.h(40f)),
                    verticalArrangement = Arrangement.Center,
                ){ //설명 텍스트
                    Text(
                        text = "$chapterName",
                        color = Color.White,
                        fontFamily = pretendard,
                        fontSize = Responsive.spH(20f),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(Responsive.h(8f)))
                    Text(
                        text = "$chapterDescription",
                        color = Color.White,
                        fontFamily = pretendard,
                        fontSize = Responsive.spH(16f),
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = Responsive.spH(22f)
                    )
                }

                val x1 = 239f
                val x2 = 136f
                val x3 =  37f
                val x4 = 136f

                val y1 = 160f
                val y2 = 205f
                val y3 = 246f
                val y4 = 325f

                val step1 = 220f
                val step2 = 258f
                val step3 = 270f

                @Composable
                fun positionFor(index1: Int): Pair<Dp, Dp> {
                    val pat = (index1 - 1) % 4
                    val x = when (pat) {
                        0 -> x1     // 1,5,9,...
                        1 -> x2     // 2,6,10,...
                        2 -> x3     // 3,7,11,...
                        else -> x4  // 4,8,12,...
                    }

                    val cycles = (index1 - 1) / 4

                    val y = when (pat) {
                        0 -> y1 + cycles * step1
                        1 -> y2 + cycles * step2
                        2 -> y3 + cycles * step3
                        else -> y4 + cycles * step2
                    }

                    val start = Responsive.w(x)
                    val top   = Responsive.w(y)
                    return start to top
                }

                val positions: Map<Int, Modifier> =
                    (1..unitSlots.size).associateWith { idx ->
                        val (start, top) = positionFor(idx)
                        Modifier.padding(start = start, top = top)
                    }
                val anchorMap: Map<Int, Pair<Dp, Dp>> =
                    (1..unitSlots.size).associateWith { idx -> positionFor(idx) }

                //행성 생성
                Box {
                    unitSlots.forEachIndexed { i, unit ->
                        val displayIndex = i + 1
                        val detail = unit?.unitProgressDetailResponse
                        val prevDetail = unitSlots.getOrNull(i - 1)?.unitProgressDetailResponse
                        val prevDone = (prevDetail?.completedLesson ?: 0) >= (prevDetail?.totalLesson ?: 3)

                        val isUnlocked = if (i == 0) true else prevDone

                        val completedLesson = detail?.completedLesson ?: 0
                        val totalLesson = detail?.totalLesson ?: 3

                        Planet(
                            state = PlanetState(
                                name = detail?.name ?: displayIndex.toString(),
                                isUnlocked = isUnlocked,
                                progress = completedLesson
                            ),
                            totalLesson = totalLesson,
                            modifier = positions[displayIndex] ?: Modifier,
                            onClick = { selectedUnitIndex = displayIndex },
                            index = displayIndex,
                            total = unitSlots.size,
                            img = img
                        )

                    }
                }

                selectedUnitIndex?.let { displayIdx ->
                    val zeroIdx = displayIdx - 1
                    val newUnitId = displayIdx
                    val (anchorStart, anchorTop) = anchorMap[displayIdx] ?: (0.dp to 0.dp)
                    val triangleTop = anchorTop + Responsive.w(100f)

                    val unit = unitSlots.getOrNull(zeroIdx)
                    val unitName = unit?.unitProgressDetailResponse?.name ?: ""

                    val prevDetail = unitSlots.getOrNull(zeroIdx - 1)?.unitProgressDetailResponse
                    val prevDone  = (prevDetail?.completedLesson ?: 0) >= (prevDetail?.totalLesson ?: 3)
                    val isUnlocked = if (zeroIdx == 0) true else prevDone

                    val popupBg = if (isUnlocked) Color(0xFFFFB608) else Color(0xFFA8A8A8)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { selectedUnitIndex = null }, //다른 부분 누르면 팝업 닫힘
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column {
                            Canvas( //삼각형
                                modifier = Modifier
                                    .padding(start = anchorStart- Responsive.w(10f), top = triangleTop)
                                    .size(Responsive.w(61f), Responsive.h(27f))
                            ) {
                                val width = size.width
                                val height = size.height

                                val path = Path().apply {
                                    moveTo(width / 2f, 0f)      // 위쪽 중앙 꼭짓점 (가로 중앙, y=0)
                                    lineTo(0f, height)          // 왼쪽 아래 꼭짓점 (x=0, y=height)
                                    lineTo(
                                        width,
                                        height
                                    )       // 오른쪽 아래 꼭짓점 (x=width, y=height)
                                    close()
                                }
                                drawPath(path, popupBg)
                            }
                            Box(
                                modifier = Modifier
                                    .size(
                                        Responsive.w(316f),
                                        Responsive.h(118f)
                                    )
                                    .clip(RoundedCornerShape(Responsive.h(16f)))
                                    .background(popupBg)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(
                                            horizontal = Responsive.w(16f),
                                            vertical = Responsive.h(16f)
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$chapterName: $unitName",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = Responsive.spH(20f),
                                        fontFamily = pretendard,
                                        color = Color.White,
                                        modifier = Modifier
                                            .size(
                                                Responsive.w(284f),
                                                Responsive.h(30f)
                                            )
                                            .align(Alignment.Start)
                                    )

                                    Spacer(modifier = Modifier.height(Responsive.h(8f)))

                                    Button(
                                        onClick = {
                                            if (!isUnlocked) return@Button
                                            val detail = unit?.unitProgressDetailResponse
                                            val unitId = detail?.unitId ?: return@Button
                                            val lessons = unit.lessonProgressSummaryResponses
                                            val nextLessonId = lessons
                                                .sortedBy { it.lessonId }
                                                .firstOrNull { it.isCompleted == false }?.lessonId
                                                ?: lessons.lastOrNull()?.lessonId
                                                ?: 1
                                            val togo = "lesson"

                                            navController.toLesson(
                                                chapterId = chapterId,
                                                unitId = newUnitId,
                                                lessonId = nextLessonId,
                                                chapterName = "$chapterName",
                                                togo = togo
                                            )
                                        },
                                        enabled = isUnlocked,
                                        shape = RoundedCornerShape(Responsive.h(16f)),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .size(
                                                Responsive.w(284f),
                                                Responsive.h(48f)
                                            ),
                                        colors = ButtonDefaults.buttonColors(
                                            contentColor = Color(0xFF222124),
                                            containerColor = Color.White,
                                            disabledContainerColor = Color.White,
                                        )
                                    ) {
                                        if(isUnlocked){
                                            Text(
                                                text = "학습 시작하기 (+20xp)",
                                                fontSize = Responsive.spH(16f),
                                                fontFamily = pretendard,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }else{
                                            Image(
                                                painter = painterResource(id = R.drawable.round_lock_24),
                                                contentDescription = null,
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (ui is UnitViewModel.UiState.Loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun Planet(
    state: PlanetState,
    planetImage: Int = R.drawable.moon,
    selectedPlanetImage: Int = R.drawable.color_moon,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    totalLesson: Int,
    index: Int,
    total: Int,
    img: Int
) {
    val enabled = state.isUnlocked

    val segmentCount = totalLesson.coerceAtLeast(1)
    val strokeWidthDp = Responsive.w(9f)
    val isSingle = segmentCount <= 1
    val rotation = when (segmentCount) {
        1 -> 0f
        2 -> 11f
        else -> 14f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(Responsive.w(90f))
    ) {
        if(index == total) {
            Image(
                painter = painterResource(id = img),
                contentDescription = null,
                modifier = Modifier.size(Responsive.w(110f))
            )
        } else {
            // 바깥 테두리 게이지
            if (enabled) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                ) {
                    val strokeWidth = strokeWidthDp.toPx()
                    val padding = strokeWidth / 2
                    val arcDiameter = size.minDimension - strokeWidth
                    val topLeftOffset = Offset(padding, padding)

                    val totalSweep = 360f
                    val gapAngle = if (isSingle) 0f else 20f.coerceAtLeast(12f - (segmentCount - 3))
                    val segmentSweep = if (isSingle) 360f else (totalSweep - (gapAngle * segmentCount)) / segmentCount

                    repeat(segmentCount) { i ->
                        val startAngle = -90f + i * (segmentSweep + gapAngle)
                        drawArc(
                            color = if (i < state.progress) Color(0xFF81DACD) else Color.White.copy(alpha = 0.6f),
                            startAngle = startAngle,
                            sweepAngle = segmentSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = topLeftOffset,
                            size = Size(arcDiameter, arcDiameter)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(Responsive.w(58f))
                    .clip(CircleShape)
                    .clickable{ onClick() }
            ) {
                Image( //enable 조건을 넣어서 이미지 변경
                    painter = painterResource(id = if (enabled) selectedPlanetImage else planetImage),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
    }

}