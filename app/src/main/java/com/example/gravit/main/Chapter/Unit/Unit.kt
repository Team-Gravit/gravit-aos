package com.example.gravit.main.Chapter.Unit

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.api.UnitPageResponse
import com.example.gravit.main.navigateToLesson
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import com.example.gravit.ui.theme.pretendard

data class PlanetState(
    val name: String,
    val isUnlocked: Boolean, // selected 상태 여부
    val progress: Int // 진행도
)

@Composable
fun Unit(
    navController: NavController,
    chapterId: Int,
    initialName: String,
    initialDesc: String,
    initialTotalUnits: Int,
    initialCompletedUnits: Int,
){
    val context = LocalContext.current
    val vm: UnitViewModel = viewModel(
        factory = UnitVMFactory(RetrofitInstance.api, context)
    )

    // 진입 시 유닛만 로드
    LaunchedEffect(chapterId) { vm.load(chapterId) }

    val ui by vm.state.collectAsState()
    when (ui) {
        UnitViewModel.UiState.SessionExpired -> {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        }

        else -> Unit
    }

    val background: Map<Int, Int> = mapOf(
        1 to R.drawable.data_structure_unit,
        2 to R.drawable.algorithm_unit,
        3 to R.drawable.computer_network_unit,
        4 to R.drawable.operating_system_unit,
        5 to R.drawable.database_unit,
        6 to R.drawable.computer_security_unit,
        7 to R.drawable.software_engineering_unit,
        8 to R.drawable.programming_language_unit
    )
    val img = background[chapterId] ?: 1

    var selectedUnitIndex by remember { mutableStateOf<Int?>(null) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CompositionLocalProvider(
            LocalScreenWidth provides screenWidth,
            LocalScreenHeight provides screenHeight
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image( //베경 이미지
                    painter = painterResource(id = img),
                    contentDescription = "background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column { //설명 텍스트
                        Text(
                            text = initialName,
                            color = Color.White,
                            fontFamily = pretendard,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                start = screenWidth * (20f / 360f),
                                top = screenHeight * (103f / 1290f)
                            )
                        )

                        Text(
                            text = initialDesc,
                            color = Color.White,
                            fontFamily = pretendard,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                start = screenWidth * (20f / 360f),
                                top = screenHeight * (8f / 1290f)
                            )
                        )
                    }
                    val positionFracs: Map<Int, Pair<Float, Float>> = mapOf(
                        0 to (239f / 360f to 175f / 1290f),
                        1 to (136f / 360f to 277f / 1290f),
                        2 to (37f / 360f to 390f / 1290f),
                        3 to (136f / 360f to 504f / 1290f),
                        4 to (228f / 360f to 616f / 1290f),
                        5 to (136f / 360f to 800f / 1290f),
                        6 to (37f / 360f to 914f / 1290f),
                        7 to (136f / 360f to 1027f / 1290f),
                        8 to (228f / 360f to 1139f / 1290f),
                        9 to (136f / 360f to 1270f / 1290f),
                        10 to (37f / 360f to 1380f / 1290f),
                        11 to (136f / 360f to 1490f / 1290f)
                    )
                    val positions: Map<Int, Modifier> = positionFracs.mapValues { (_, frac) ->
                        val (sx, ty) = frac
                        Modifier.padding(
                            start = screenWidth * sx,
                            top = screenHeight * ty
                        )
                    }
                    val unitSlots: List<UnitPageResponse?> = remember(ui, initialTotalUnits) {
                        List(initialTotalUnits) { idx ->
                            (ui as? UnitViewModel.UiState.Success)?.data?.getOrNull(idx)
                        }
                    }
                    //생성
                    Box {
                        unitSlots.forEachIndexed { index, unit ->
                            val detail = unit?.unitProgressDetailResponse

                            val prevDetail = unitSlots.getOrNull(index - 1)?.unitProgressDetailResponse
                            val prevDone = (prevDetail?.completedLesson ?: 0) >= (prevDetail?.totalLesson ?: 3)

                            val isUnlocked = when {
                                index == 0 -> true                       // 유닛 1은 무조건 보라
                                prevDetail != null -> prevDone           // 이전 유닛을 다 끝냈다면 언락
                                else -> false                            // 이전 정보가 없으면 잠금 유지
                            }

                            val completedLesson = detail?.completedLesson ?: 0
                            val totalLesson = detail?.totalLesson ?: 3

                            Planet(
                                state = PlanetState(
                                    name = detail?.name ?: (index + 1).toString(),
                                    isUnlocked = isUnlocked,
                                    progress = completedLesson
                                ),
                                totalLesson = totalLesson,
                                modifier = positions[index] ?: Modifier,
                                onClick = { selectedUnitIndex = index }
                            )
                        }
                    }
                    fun popupAnchorStart(idx: Int): Dp =
                        screenWidth * (positionFracs[idx]?.first ?: 0f)

                    fun popupAnchorTop(idx: Int): Dp =
                        screenHeight * (positionFracs[idx]?.second ?: 0f)

                    selectedUnitIndex?.let { idx ->
                        val anchorStart = popupAnchorStart(idx)
                        val anchorTop   = popupAnchorTop(idx)

                        val triangleStart = anchorStart - screenWidth * (10f / 360f)
                        val triangleTop   = anchorTop   + screenHeight * (153f / 1290f)

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { selectedUnitIndex = null }, //다른 부분 누르면 팝업 닫힘
                            contentAlignment = Alignment.TopCenter
                        ) {
                            val triangleWidth = 61f / 360f

                            Box {
                                Canvas( //삼각형
                                    modifier = Modifier
                                        .padding(start = triangleStart, top = triangleTop)
                                        .size(
                                            screenWidth * triangleWidth,
                                            screenHeight * (27f / 740f)
                                        )
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
                                    drawPath(path, Color(0xFFFFB608))
                                }
                                Box(
                                    Modifier
                                        .padding(top = anchorTop + screenHeight * (180f / 1290f))
                                        .size(
                                            screenWidth * (316f / 360f),
                                            screenHeight * (118f / 740f)
                                        )
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFFFB608))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(
                                                horizontal = screenWidth * (16f / 360f),
                                                vertical = screenHeight * (16f / 740f)
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val unit = unitSlots.getOrNull(idx)
                                        val unitName = unit?.unitProgressDetailResponse?.name ?: "${idx + 1}번 유닛"

                                        Text(
                                            text = "$initialName: $unitName",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            fontFamily = pretendard,
                                            color = Color.White,
                                            modifier = Modifier
                                                .size(
                                                    screenWidth * (284f / 360f),
                                                    screenHeight * (30f / 740f)
                                                )
                                                .align(Alignment.Start)
                                        )

                                        Spacer(modifier = Modifier.height(screenHeight * (8f / 740f)))

                                        Button(
                                            onClick = {
                                                val unit = unitSlots.getOrNull(idx)
                                                val detail = unit?.unitProgressDetailResponse
                                                val unitId = detail?.unitId ?: return@Button
                                                val lessons = unit.lessonProgressSummaryResponses

                                                // 2) 미완료 첫 레슨 → 없으면 마지막 레슨 → 없으면 1로 폴백
                                                val nextLessonId = lessons
                                                    .sortedBy { it.lessonId }
                                                    .firstOrNull { it.isCompleted == false }?.lessonId
                                                    ?: lessons.lastOrNull()?.lessonId
                                                    ?: 1

                                                navController.navigateToLesson(
                                                    chapterId = chapterId,
                                                    unitId = unitId,
                                                    lessonId = nextLessonId,
                                                    chapterName = initialName
                                                ) },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .size(
                                                    screenWidth * (284f / 360f),
                                                    screenHeight * (48f / 740f)
                                                ),
                                            colors = ButtonDefaults.buttonColors(
                                                contentColor = Color(0xFF222124),
                                                containerColor = Color.White
                                            )
                                        ) {
                                            Text(
                                                text = "학습 시작하기 (+20xp)",
                                                fontSize = 16.sp,
                                                fontFamily = pretendard,
                                                fontWeight = FontWeight.SemiBold
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
    }
}


@Composable
fun Planet(
    state: PlanetState,
    planetImage: Int = R.drawable.moon,
    selectedPlanetImage: Int = R.drawable.color_moon,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    totalLesson: Int

) {
    val enabled = state.isUnlocked
    val screenWidth = LocalScreenWidth.current

    val segmentCount = totalLesson.coerceAtLeast(1)
    val progress = state.progress.coerceIn(0, segmentCount)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(screenWidth * (90f / 360f))
    ) {
        // 바깥 테두리 게이지
        if (enabled) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .rotate(14f)
            ) {
                val strokeWidth = 9.dp.toPx()
                val padding = strokeWidth / 2
                val arcDiameter = size.minDimension - strokeWidth
                val topLeftOffset = Offset(padding, padding)

                val totalSweep = 360f
                val gapAngle = 20f.coerceAtLeast(12f - (segmentCount - 3))
                val segmentSweep = (totalSweep - (gapAngle * segmentCount)) / segmentCount

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
                .size(screenWidth * (58f / 360f))
                .clip(CircleShape)
                .clickable(enabled = enabled) { onClick() }
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
