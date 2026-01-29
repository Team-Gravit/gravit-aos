package com.inuappcenter.gravit.main.Study.Unit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.UnitDetail
import com.inuappcenter.gravit.api.UnitPageResponse
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

data class UnitUi(
    val unitId: Int,
    val orderText: String,
    val title: String,
    val progressRate: Float
)

fun toUnitUiList(dto: UnitPageResponse): List<UnitUi> {
    return dto.unitDetails.mapIndexed { index, detail: UnitDetail ->
        val summary = detail.unitSummary
        val ratePercent = detail.progressRate
        val rate = (ratePercent / 100.0).toFloat()

        UnitUi(
            unitId = summary.unitId,
            orderText = "Unit%02d".format(index + 1),
            title = summary.title,
            progressRate = rate
        )
    }
}

@Composable
fun UnitList(
    chapterId: Int,
    navController: NavController,
    onSessionExpired: () -> Unit
) {
    val context = LocalContext.current
    val vm: UnitListVM = viewModel(
        factory = UnitListVMFactory(
            api = RetrofitInstance.api,
            appContext = context.applicationContext,
            chapterId = chapterId
        )
    )

    val uiState by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.load()
    }

    when (val state = uiState) {
        UnitListVM.UiState.Loading,
        UnitListVM.UiState.Idle -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        UnitListVM.UiState.SessionExpired -> {
            navController.navigate("error/401") {
                popUpTo(
                    navController.currentBackStackEntry?.destination?.id ?: return@navigate
                ) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        UnitListVM.UiState.NotFound -> {
            navController.navigate("error/404") {
                popUpTo(
                    navController.currentBackStackEntry?.destination?.id ?: return@navigate
                ) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
        UnitListVM.UiState.Failed -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "유닛 정보를 불러오지 못했어요.",
                    color = Color.Black,
                    fontFamily = pretendard
                )
            }
        }

        is UnitListVM.UiState.Success -> {
            val data = state.data
            val units = toUnitUiList(data)

            UnitListContent(
                chapterTitle = data.chapterSummary.title,
                units = units,
                navController = navController
            )
        }
    }
}

@Composable
private fun UnitListContent(
    chapterTitle: String,
    units: List<UnitUi>,
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.unitlesson_back),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .navigationBarsPadding()

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .padding(start = 18.dp)
                            .size(20.dp)
                            .clickable {

                                val popped = navController.popBackStack(
                                    route = "chapter",
                                    inclusive = false
                                )

                                if (!popped) {
                                    navController.navigate("chapter") {
                                        popUpTo("unit/{chapterId}") {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                }
                            },
                        tint = Color.White
                    )

                    Spacer(Modifier.width(18.dp))

                    Text(
                        text = chapterTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = pretendard,
                        color = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)) {
                    Text(
                        text = "유닛 리스트",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        fontFamily = pretendard,
                        textAlign = TextAlign.Start,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        itemsIndexed(units) { _, unit ->
                            UnitItemBox(
                                unit = unit,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitItemBox(
    unit: UnitUi,
    navController: NavController
) {
    val percent = (unit.progressRate * 100).roundToInt().coerceIn(0, 100)
    val rawRate = unit.progressRate.coerceIn(0f, 1f)

    val visualRate = if (rawRate <= 0f) {
        0.03f
    } else {
        max(0.05f, rawRate)
    }

    val angle = Math.toRadians(44.97)
    val endX = cos(angle).toFloat()
    val endY = sin(angle).toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = Color(0xFF8B69FF), RoundedCornerShape(8.dp))
            .clickable {
                navController.navigate("lessonList/${unit.unitId}/${unit.title}")
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.unit_glass),
            contentDescription = "back",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier
            .padding(top = 13.dp, start = 13.dp, end = 13.dp, bottom = 13.dp)
            .padding(10.dp)
        ) {
            Row {
                Text(
                    text = "${unit.orderText}  ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = pretendard,
                    textAlign = TextAlign.Start,
                    color = Color.White,
                )
                Text(
                    text = unit.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    fontFamily = pretendard,
                    textAlign = TextAlign.Start,
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${percent}%",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    fontFamily = pretendard,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(60.dp),
                    style = TextStyle(fontFeatureSettings = "tnum")
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(1.dp)
                            .fillMaxWidth(visualRate)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF8100B3),
                                        Color(0xFFDD00FF)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(endX * 5f, endY * 100f)

                                )
                            )
                    )
                }
            }
        }
    }
}
