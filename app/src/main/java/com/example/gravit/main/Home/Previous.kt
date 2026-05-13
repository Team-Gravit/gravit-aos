package com.inuappcenter.gravit.main.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inuappcenter.gravit.api.UnitDetail
import com.inuappcenter.gravit.ui.theme.pretendard

@Composable
fun PreviousButton(
    chapterId: Int,
    chapterName: String?,
    onClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onUnitClick: (UnitDetail) -> Unit,
    progressRate: Float,
    units: List<UnitDetail> = emptyList()
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
            .clip(RoundedCornerShape(16.dp))
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
            if (chapterId == 0) {
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
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomText(
                        text = "이어서 학습하기",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFFA8A8A8)
                    )

                    Text(
                        text = "전체 학습화면 보기",
                        fontSize = 12.sp,
                        color = Color(0xFFC6C6C6),
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
                    CustomText(
                        text = chapterName,
                        fontWeight = FontWeight(600),
                        fontSize = 16.sp,
                        fontFamily = pretendard,
                        color = Color(0xFF383838)
                    )

                    Text(
                        text = "$progressRate%",
                        fontSize = 14.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFBA00FF)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                RoundedGauge(
                    height = dh(10f),
                    width = 0.dp,
                    rate = progressRate,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dh(8f))
                ) {
                    units.forEachIndexed { index, unit ->
                        val unitOrderText = "Unit%02d".format(index + 1)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dh(42f))
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE7E7E7),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(Color.White)
                                .clickable {
                                    onUnitClick(unit)
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "$unitOrderText | ${unit.unitSummary.title}",
                                fontSize = 14.sp,
                                fontFamily = pretendard,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF383838),
                                modifier = Modifier.padding(horizontal = dw(14f))
                            )
                        }
                    }
                }
            }
        }
    }
}