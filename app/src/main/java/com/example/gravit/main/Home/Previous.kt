package com.inuappcenter.gravit.main.Home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.Cip
import com.example.gravit.ui.theme.CipState
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.api.Units

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun PreviousButton(
    chapterId: Int,
    chapterName: String,
    onClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onUnitClick: (Units) -> Unit,
    progressRate: Float,
    units: List<Units> = emptyList()
) {
    val statusMap = mapOf(
        "NOT_STARTED" to "진행전",
        "IN_PROGRESS" to "진행중",
        "COMPLETED" to "진행됨"
    )
    var selectedUnit by remember { mutableStateOf<Units?>(null) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (chapterId == 0) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            /* if (chapterId == 0) {
                Row {
                    CustomText(
                        text = "새로운 학습을 시작하기",
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomText(
                    text = "최근에 진행한 학습 정보가 없습니다.",
                    fontWeight = FontWeight(500),
                    fontSize = 14.sp,
                    color = Color.White
                )
            } else { */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "이어서 학습하기",
                    style = AppTypography.Label2,
                    color = PrimitiveColor.Gray500
                )

                Text(
                    text = "전체 학습화면 보기",
                    style = AppTypography.Label2,
                    color = PrimitiveColor.Gray400,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        onViewAllClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chapterName,
                    style = AppTypography.Headline2,
                    color = PrimitiveColor.Gray900
                )

                Text(
                    text = "$progressRate%",
                    style = AppTypography.Label1,
                    color = AppColor.Main1
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            RoundedGauge(
                height = 8.dp,
                width = 0.dp,
                rate = progressRate,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFBF1FF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val scrollState = rememberScrollState()
            val preventParentScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        return available
                    }

                    override suspend fun onPostFling(
                        consumed: Velocity,
                        available: Velocity
                    ): Velocity {
                        return available
                    }
                }
            }
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(142.dp)
                    .nestedScroll(preventParentScrollConnection)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(142.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    units.forEachIndexed { index, unit ->
                        val unitOrderText = "Unit %02d".format(index + 1)
                        val isSelected = selectedUnit == unit

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFFCE4BFF) else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    selectedUnit = unit
                                    selectedIndex = index
                                }
                                .background(AppColor.bg1),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = unitOrderText,
                                    style = AppTypography.Label2,
                                    color = if (unit.status == "NOT_STARTED") PrimitiveColor.Gray400 else PrimitiveColor.Gray900,
                                    modifier = Modifier.size(50.dp, 16.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                VerticalDivider(
                                    modifier = Modifier.fillMaxHeight(),
                                    thickness = 1.dp,
                                    color = AppColor.divider1
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = unit.title,
                                    style = AppTypography.Label2,
                                    color = if (unit.status == "NOT_STARTED") PrimitiveColor.Gray400 else PrimitiveColor.Gray900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Cip(
                                    text = statusMap[unit.status],
                                    onClick = {},
                                    state =
                                        when (unit.status) {
                                            "NOT_STARTED" -> CipState.Disabled
                                            "IN_PROGRESS" -> CipState.Active
                                            else -> CipState.Default
                                        },
                                    modifier = Modifier.size(55.dp, 26.dp),
                                    style = AppTypography.App_Caption2
                                )
                            }

                        }
                    }
                }
            }
            //}
            selectedIndex?.let {
                Spacer(modifier = Modifier.height(16.dp))
                BlockButton(
                    text = "${it + 1}강 이어서 학습하기",
                    onClick = {
                        selectedUnit?.let(onUnitClick)
                    }
                )
            }

        }
    }
}