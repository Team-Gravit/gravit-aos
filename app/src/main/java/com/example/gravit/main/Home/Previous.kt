package com.example.gravit.main.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.gravit.R
import com.example.gravit.ui.theme.Responsive
import com.example.gravit.ui.theme.mbc1961

val previousImg: Map<Int, Int> = mapOf(
    0 to R.drawable.previous_null,
    1 to R.drawable.data_structure,
    2 to R.drawable.algorithm,
    3 to R.drawable.computer_network,
    4 to R.drawable.operating_system,
    5 to R.drawable.database,
    6 to R.drawable.computer_security,
    7 to R.drawable.programming_language,
    8 to R.drawable.programming_language
)

@Composable
fun PreviousButton(
    chapterId: Int,
    backgroundImg: Int,
    chapterName: String?,
    onClick: () -> Unit,
    progressRate: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Responsive.h(131f))
            .clip(RoundedCornerShape(Responsive.h(16f)))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = backgroundImg),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(
                horizontal = Responsive.w(16f),
                vertical = Responsive.h(16f)
            )
        ) {
            if (chapterId == 0) {
                Row {
                    CustomText(
                        text = "새로운 학습을 시작하기",
                        fontSize = Responsive.spH(24f),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(Responsive.h(8f)))
                CustomText(
                    text = "최근에 진행한 학습 정보가 없습니다.",
                    fontWeight = FontWeight.Medium,
                    fontSize = Responsive.spH(15f),
                    color = Color.White
                )
            } else {
                CustomText(
                    text = "직전학습 이어서 하기",
                    fontSize = Responsive.spH(24f),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(Responsive.h(8f)))

                CustomText(
                    text = chapterName,
                    fontWeight = FontWeight.Medium,
                    fontSize = Responsive.spH(16f),
                    fontFamily = mbc1961,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(Responsive.h(13f)))

                RoundedGauge(
                    height = Responsive.h(10f),
                    width = Responsive.w(271f),
                    rate = progressRate
                )
            }
        }
    }
}