package com.example.gravit.main.Study.Stage

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import com.example.gravit.ui.theme.pretendard


data class PlanetState(
    val name: String,
    val isUnlocked: Boolean, // selected 상태 여부
    val progress: Int // 진행도
)

@Composable
fun Earth(navController: NavController){
    var showPopupForPlanet by remember { mutableStateOf<String?>(null) }

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
            Box (
                modifier = Modifier.fillMaxSize()
            ){
                Image( //베경 이미지
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column { //설명 텍스트
                        Text(
                            text = "자료구조",
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
                            text = "설명",
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
                    //활성화 조건을 넣지 않아서 임의로 넣어둠 (UI 확인용)
                    val planets = listOf(
                        PlanetState("1", isUnlocked = true, progress = 0),
                        PlanetState("2", isUnlocked = true, progress = 0),
                        PlanetState("3", isUnlocked = true, progress = 0),
                        PlanetState("4", isUnlocked = true, progress = 0),
                        PlanetState("5", isUnlocked = true, progress = 0),
                        PlanetState("6", isUnlocked = true, progress = 0),
                        PlanetState("7", isUnlocked = true, progress = 0),
                        PlanetState("8", isUnlocked = true, progress = 0),
                        PlanetState("9", isUnlocked = true, progress = 0),
                        PlanetState("10", isUnlocked = true, progress = 0),
                        PlanetState("11", isUnlocked = true, progress = 0),
                        PlanetState("12", isUnlocked = true, progress = 0)


                    )
                    //위치
                    val planetPositions = mapOf(
                        "1" to Modifier.padding(start = screenWidth * (239f / 360f), top = screenHeight * (175f / 1290f)),
                        "2" to Modifier.padding(start = screenWidth * (136f / 360f), top = screenHeight * (277f / 1290f)),
                        "3" to Modifier.padding(start = screenWidth * (37f / 360f), top = screenHeight * (390f / 1290f)),
                        "4" to Modifier.padding(start = screenWidth * (136 / 360f), top = screenHeight * (504f / 1290f)),
                        "5" to Modifier.padding(start = screenWidth * (228f / 360f), top = screenHeight * (616f / 1290f)),
                        "6" to Modifier.padding(start = screenWidth * (136f / 360f), top = screenHeight * (800f / 1290f)),
                        "7" to Modifier.padding(start = screenWidth * (37f / 360f), top = screenHeight * (914f / 1290f)),
                        "8" to Modifier.padding(start = screenWidth * (136f / 360f), top = screenHeight * (1027f / 1290f)),
                        "9" to Modifier.padding(start = screenWidth * (228f / 360f), top = screenHeight * (1139f / 1290f)),
                        "10" to Modifier.padding(start = screenWidth * (136f / 360f), top = screenHeight * (1270f / 1290f)),
                        "11" to Modifier.padding(start = screenWidth * (37f / 360f), top = screenHeight * (1380f / 1290f)),
                        "12" to Modifier.padding(start = screenWidth * (136f / 360f), top = screenHeight * (1490f / 1290f))


                    )
                    //생성
                    Box {
                        planets.forEach { planet ->
                            Planet(
                                state = planet,
                                modifier = planetPositions[planet.name] ?: Modifier,
                                onClick = { showPopupForPlanet = planet.name } //클릭하면 팝업 나옴
                            )
                        }
                    }
                }
                //팝업 위치
                val popupStartPositions = mapOf(
                    "1" to 239,
                    "2" to 136,
                    "3" to 37,
                    "4" to 136,
                    "5" to 228,
                    "6" to 136,
                    "7" to 37,
                    "8" to 136,
                    "9" to 228,
                    "10" to 136,
                    "11" to 37,
                    "12" to 136
                )
                val popupTopPositions = mapOf(
                    "1" to 175,
                    "2" to 277,
                    "3" to 390,
                    "4" to 504,
                    "5" to 616,
                    "6" to 800,
                    "7" to 914,
                    "8" to 1027,
                    "9" to 1139,
                    "10" to 1270,
                    "11" to 1380,
                    "12" to 1490
                )
                //팝업
                showPopupForPlanet?.let { planetName ->

                    val top = popupTopPositions[planetName] ?: 0
                    val start = popupStartPositions[planetName] ?: 0

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .clickable { showPopupForPlanet = null }, //다른 부분 누르면 팝업 닫힘
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val triangleWidth = 61f / 360f

                        Box {
                            Canvas( //삼각형
                                modifier = Modifier
                                    .padding(
                                        start = screenWidth * (start / 360f) - screenWidth * (10f / 360f),
                                        top = (screenHeight * (top / 1290f)) + screenHeight * (153f / 1290f)
                                    )
                                    .size(screenWidth * triangleWidth, screenHeight * (27f / 740f))
                            ) {
                                val width = size.width
                                val height = size.height

                                val path = Path().apply {
                                    moveTo(width / 2f, 0f)      // 위쪽 중앙 꼭짓점 (가로 중앙, y=0)
                                    lineTo(0f, height)          // 왼쪽 아래 꼭짓점 (x=0, y=height)
                                    lineTo(width, height)       // 오른쪽 아래 꼭짓점 (x=width, y=height)
                                    close()
                                }
                                drawPath(path, Color(0xFFFFB608))
                            }
                            Box(
                                Modifier
                                    .padding(top = (screenHeight * (top / 1290f)) + screenHeight * (180f / 1290f))
                                    .size(screenWidth * (316f / 360f), screenHeight * (118f / 740f))
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFFB608))
                            ) {
                                Column (modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        horizontal = screenWidth * (16f / 360f),
                                        vertical = screenHeight * (16f / 740f)),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        text = "자료구조: ${planetName}챕터",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        fontFamily = pretendard,
                                        color = Color.White,
                                        modifier = Modifier
                                            .size(screenWidth * (284f / 360f), screenHeight * (30f / 740f))
                                            .align(Alignment.Start)
                                    )

                                    Spacer(modifier = Modifier.height(screenHeight * (8f / 740f)))

                                    Button(
                                        onClick = { navController.navigate("")  }, //네비 연결 X
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

@Composable
private fun Planet(
    state: PlanetState,
    planetImage: Int = R.drawable.moon,
    selectedPlanetImage: Int = R.drawable.color_moon,
    modifier: Modifier = Modifier,
    onClick: () -> Unit

) {
    val enabled = state.isUnlocked
    val screenWidth = LocalScreenWidth.current

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
                val segmentCount = 3
                val gapAngle = 25f
                val segmentSweep = (totalSweep - (gapAngle * segmentCount)) / segmentCount

                repeat(segmentCount) { i ->
                    val startAngle = -90f + i * (segmentSweep + gapAngle)
                    drawArc(
                        color = if (i < state.progress) Color(0xFF81DACD)
                                else Color.White.copy(alpha = 0.6f),
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





//지구
@Preview(showBackground = true)
@Composable
fun Preview() {
    val navController = rememberNavController()
    Earth(navController)
}