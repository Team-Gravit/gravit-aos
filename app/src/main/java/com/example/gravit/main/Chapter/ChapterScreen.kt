package com.example.gravit.main.Chapter

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import com.example.gravit.ui.theme.mbc1961
import com.example.gravit.ui.theme.pretendard
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.gravit.api.ChapterPageResponse
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape

@Composable
fun ChapterScreen(navController: NavController){
    val context = LocalContext.current
    val vm: ChapterViewModel = viewModel(
        factory = ChapterVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }
    when (ui) {
        ChapterViewModel.UiState.SessionExpired -> {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        }
        else -> Unit
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CompositionLocalProvider(
            LocalScreenWidth provides screenWidth,
            LocalScreenHeight provides screenHeight
        ){
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .background(Color(0xFFF8F8F8))
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFFF8F8F8)),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "학습",
                        fontFamily = pretendard,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF030303),
                        modifier = Modifier
                            .padding(start = screenWidth * (20f / 360f))
                            .align(Alignment.CenterStart)
                    )
                }

                val chapters: List<ChapterPageResponse> =
                    (ui as? ChapterViewModel.UiState.Success)?.data ?: emptyList()

                val buttons = remember(chapters) { mapToButtons(chapters) }

                Column(
                    modifier = Modifier
                        .padding(
                            start = screenWidth * (16f / 360f),
                            end = screenWidth * (16f / 360f),
                            top = screenHeight * (16f / 740f)
                        )
                        .fillMaxSize()
                        .background(Color(0xFFF2F2F2))
                        .verticalScroll(rememberScrollState())
                ) {

                    buttons.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            pair.forEachIndexed { index, data ->
                                ChapterButton(
                                    description = data.description,
                                    text = data.name,
                                    planet = data.planetRes,
                                    completedUnits = data.completedUnits,
                                    totalUnits = data.totalUnits,
                                    onClick = { navController.navigate(buildUnitsRoute(data)) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(160f / 166f)
                                )
                                if (index == 0 && pair.size > 1) {
                                    Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))
                                }

                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(160f / 166f) // 높이 균형 유지
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(screenHeight * (8f / 740f)))
                    }

                }
            }
        }
    }
}

private fun buildUnitsRoute(ui: ChapterButtonUI): String {
    return buildString {
        append("units/${ui.id}")
        append("?name=${Uri.encode(ui.name)}")
        append("&desc=${Uri.encode(ui.description)}")
        append("&total=${ui.totalUnits}")
        append("&completed=${ui.completedUnits}")
    }
}

data class ChapterButtonUI(
    val id: Int,
    val name: String,
    val description: String,
    val completedUnits: Int,
    val totalUnits: Int,
    val planetRes: Int,
)

private val planetById = mapOf(
    1 to R.drawable.data_structure_planet,
    2 to R.drawable.algorithm_planet,
    3 to R.drawable.computer_network_planet,
    4 to R.drawable.operating_system_planet,
    5 to R.drawable.database_planet,
    6 to R.drawable.computer_security_planet,
    7 to R.drawable.software_engineering_planet,
    8 to R.drawable.programming_language_planet,
)
private fun resolvePlanetRes(id: Int): Int {
    return planetById[id]
        ?: error("알 수 없는 chapterId: $id")
}
fun mapToButtons(chapters: List<ChapterPageResponse>): List<ChapterButtonUI> {
    return chapters.map { c ->
        ChapterButtonUI(
            id = c.chapterId,
            name = c.name,
            description = c.description,
            completedUnits = c.completedUnits,
            totalUnits = c.totalUnits,
            planetRes = resolvePlanetRes(c.chapterId),
        )
    }
}

private object TriangleShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                               density: androidx.compose.ui.unit.Density
    ) = androidx.compose.ui.graphics.Outline.Generic(
        Path().apply {
            moveTo(0f, size.height)         // 왼쪽-아래
            lineTo(size.width, size.height/2) // 오른쪽-중앙(뾰족)
            lineTo(0f, 0f)                   // 왼쪽-위
            close()
        }
    )
}

@Composable
private fun InfoTooltip(
    text: String,
    onDismiss: () -> Unit,
    anchorToRight: Boolean,          // 아이콘의 오른쪽에 꼬리 붙일지 여부
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true) // 바깥 클릭 시 닫힘
    ) {
        // 말풍선 본체
        Column(
            modifier = Modifier
                .padding(start = offsetX, top = offsetY)
        ) {
            // 본문 박스
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF222124))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .widthIn(max = 260.dp),   // 한줄 길이 제한(디자인 감성)
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.info), // 흰 원 안의 i 아이콘
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }

            // 꼬리(삼각형). 오른쪽에 붙도록 정렬
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = if (anchorToRight) Arrangement.End else Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 12.dp)
                        .clip(TriangleShape)
                        .background(Color(0xFF222124))
                )
            }
        }
    }
}

@Composable
fun ChapterButton (
    description: String,
    totalUnits : Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    completedUnits: Int,
    planet: Int
) {
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    var showTooltip by remember { mutableStateOf(false) }

    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box (Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.chapter_button_back),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = screenWidth * (25f / 360f))
                    .graphicsLayer(
                        scaleX = 1.7f,
                        scaleY = 1.7f
                    ),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = screenHeight * (15f / 740f))
            ) {
                Column (
                    modifier = Modifier.padding(horizontal = screenWidth * (10f / 360f))
                ){
                    Row(
                        modifier = Modifier
                            .size(screenWidth * (140f / 360f), screenHeight * (24f / 740f))

                    ) {
                        Text(
                            text = text,
                            fontSize = 20.sp,
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
                                    .size(screenWidth * (24f / 360f))
                                    .clickable { showTooltip = true },
                                tint = Color.White
                            )

                            if (showTooltip) {
                                // 아이콘 바로 왼쪽-아래 방향으로 말풍선 표시
                                InfoTooltip(
                                    text = description,
                                    onDismiss = { showTooltip = false },
                                    anchorToRight = true,          // 꼬리를 오른쪽에
                                    offsetX = (-180).dp,           // 필요시 조절(아이콘 기준 X)
                                    offsetY = 10.dp                // 아이콘 아래로 살짝
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(screenHeight * (12f / 740f)))
                    RoundedGauge(
                        totalUnits = totalUnits,
                        completedUnits = completedUnits,
                        width = screenWidth * (140f / 360f),
                        height = screenHeight * (10f / 740f)
                    )
                    Image(
                        painter = painterResource(planet),
                        contentDescription = null,
                        modifier = Modifier.offset(x = 80.dp, y = 10.dp)
                    )
                }


            }
        }
    }
}

@Composable
fun RoundedGauge(
    totalUnits: Int = 10,
    completedUnits: Int,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val screenHeight = LocalScreenHeight.current

    Column(modifier = Modifier.size(width, height * 3)) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFEEEEEE))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width * (completedUnits.toFloat() / totalUnits))
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFBA00FF))
            )
        }
        Spacer(modifier = Modifier.height(screenHeight * (3f / 740f)))
        Text(
            text = "$completedUnits/$totalUnits",
            fontSize = 15.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            )

    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    val navController = rememberNavController()
    ChapterScreen(navController)
}
