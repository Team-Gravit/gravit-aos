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
import com.inuappcenter.gravit.api.UnitDetailResponses

@Composable
fun PreviousButton(
    chapterId: Int,
    chapterName: String,
    onClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onUnitClick: (UnitDetailResponses) -> Unit,
    progressRate: Float,
    units: List<UnitDetailResponses> = emptyList()
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
            .background(color = Color.White)
            .then(
                if (chapterId == 0) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dw(16f),
                    vertical = dh(16f)
                )
        ) {
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
                    text = "${progressRate.toInt()}%",
                    style = AppTypography.Label1,
                    color = AppColor.Main1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            RoundedGauge(
                height = 10.dp,
                width = 0.dp,
                rate = progressRate,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

                RoundedGauge(
                    height = dh(8f),
                    width = 0.dp,
                    rate = progressRate,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFBF1FF)
                )

                    val unitBorderColor = when {
                        unitProgressRate <= 0.0 -> Color(0xFFF8F8F8)
                        unitProgressRate < 100.0 -> Color(0xFFCE4BFF)
                        else -> Color(0xFFF8F8F8)
                    }

                    val unitBackgroundColor = when {
                        unitProgressRate <= 0.0 -> Color(0xFFF8F8F8)
                        unitProgressRate < 100.0 -> Color.White
                        else -> Color(0xFFF8F8F8)
                    }

                    val unitTextColor = when {
                        unitProgressRate <= 0.0 -> Color(0xFFC6C6C6)
                        unitProgressRate < 100.0 -> Color(0xFF383838)
                        else -> Color(0xFF383838)
                    }

                    val dividerColor = when {
                        unitProgressRate <= 0.0 -> Color(0xFFDCDCDC)
                        unitProgressRate < 100.0 -> Color(0xFFDCDCDC)
                        else -> Color(0xFFDCDCDC)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(unitBackgroundColor)
                            .border(
                                width = 1.dp,
                                color = unitBorderColor,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                onUnitClick(unit)
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = unitOrderText,
                                style = AppTypography.Label2,
                                color = unitTextColor
                            )

                            VerticalDivider(
                                modifier = Modifier.fillMaxHeight(),
                                thickness = 2.dp,
                                color = dividerColor
                            )

                            Text(
                                text = unit.unitSummaryResponse.title,
                                style = AppTypography.Label2,
                                color = unitTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}