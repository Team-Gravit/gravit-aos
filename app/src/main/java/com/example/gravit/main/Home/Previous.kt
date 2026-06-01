package com.inuappcenter.gravit.main.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.api.Units

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
    val config = LocalConfiguration.current
    val designWidth = 360f
    val designHeight = 740f

    val scaleW = config.screenWidthDp.toFloat() / designWidth
    val scaleH = config.screenHeightDp.toFloat() / designHeight

    fun dw(v: Float) = (v * scaleW).dp
    fun dh(v: Float) = (v * scaleH).dp

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
                .padding(
                    horizontal = dw(16f),
                    vertical = dh(16f)
                )
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

                Spacer(modifier = Modifier.height(dh(8f)))

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

                Spacer(modifier = Modifier.height(dh(8f)))

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

                Spacer(modifier = Modifier.height(10.dp))

                RoundedGauge(
                    height = dh(8f),
                    width = 0.dp,
                    rate = progressRate,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFBF1FF)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dh(8f))
                ) {
                    val firstIncompleteIndex = units.indexOfFirst { !it.isCompleted }
                    units.forEachIndexed { index, unit ->
                        val unitOrderText = "Unit %02d".format(index + 1)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dh(42f))
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (index == firstIncompleteIndex) Color(0xFFCE4BFF) else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    onUnitClick(unit)
                                }
                                .background(if (index == firstIncompleteIndex) Color.White else PrimitiveColor.Gray50),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row (
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = unitOrderText,
                                    style = AppTypography.Label2,
                                    color = if (index != firstIncompleteIndex && !unit.isCompleted) PrimitiveColor.Gray400 else PrimitiveColor.Gray900,
                                )
                                VerticalDivider(
                                    modifier = Modifier.fillMaxHeight(),
                                    thickness = 1.dp,
                                    color = AppColor.divider1
                                )
                                Text(
                                    text = unit.title,
                                    style = AppTypography.Label2,
                                    color = if (index != firstIncompleteIndex && !unit.isCompleted) PrimitiveColor.Gray400 else PrimitiveColor.Gray900,
                                )
                            }

                        }
                    }
                }
            //}
        }
    }
}