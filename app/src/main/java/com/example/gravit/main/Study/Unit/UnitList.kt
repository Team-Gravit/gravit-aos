package com.inuappcenter.gravit.main.Study.Unit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.UnitPageResponse
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.ChapterSummaryResponse
import com.inuappcenter.gravit.api.UnitDetailResponses
import com.inuappcenter.gravit.main.User.TopBar
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

data class UnitUi(
    val unitId: Int,
    val orderText: String,
    val title: String,
    val progressRate: Float,
    val description: String
)

fun toUnitUiList(dto: UnitPageResponse): List<UnitUi> {
    return dto.unitDetailResponses.mapIndexed { index, detail: UnitDetailResponses ->
        val summary = detail.unitSummaryResponse
        val ratePercent = detail.progressRate
        val rate = (ratePercent / 100.0).toFloat()
        val description = detail.unitSummaryResponse.description

        UnitUi(
            unitId = summary.unitId,
            orderText = "Unit%02d".format(index + 1),
            title = summary.title,
            progressRate = rate,
            description = description
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
                chapterData = data.chapterSummaryResponse,
                units = units,
                navController = navController
            )
        }
    }
}

@Composable
private fun UnitListContent(
    chapterData: ChapterSummaryResponse,
    units: List<UnitUi>,
    navController: NavController,
) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg0)
    ) {
        TopBar(
            navController = navController,
            title = chapterData.title,
            useCloseIcon = false,
            useAlarmIcon = true
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColor.bg1)
                .weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.unitlesson_back),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            ) {
                Text(
                    text = chapterData.title,
                    style = AppTypography.Headline2,
                    color = AppColor.text1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chapterData.description,
                    style = AppTypography.Label2,
                    color = AppColor.text3,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 40.dp)
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
            .clickable {
                navController.navigate("lessonList/${unit.unitId}")
            }
            .background(AppColor.bg0)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Row {
                Text(
                    text = "${unit.orderText} - ${unit.title}",
                    style = AppTypography.Headline2,
                    color = AppColor.text2
                )
                Spacer(Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.chevron_right),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.5.dp))
            Text(
                text = unit.description,
                style = AppTypography.Label2,
                color = AppColor.text4,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${percent}%",
                    style = AppTypography.Headline2,
                    color = AppColor.text4
                )

                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColor.bg3)
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
