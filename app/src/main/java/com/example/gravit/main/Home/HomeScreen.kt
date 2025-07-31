package com.example.gravit.main.Home

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen() {
    val dateFormat = remember { SimpleDateFormat("yyyy. MM. dd (E)", Locale.KOREAN) }
    val today = remember { dateFormat.format(Date()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF2F2F2))
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_back),
            contentDescription = "main back",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
        )

        Column {
            Box(modifier = Modifier
                .padding(top = 35.dp)
                .size(322.dp, 48.dp)
                .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    /*** 하단에 블러 안 함 **/
                    painter = painterResource(id = R.drawable.gravit_main_logo),
                    contentDescription = "gravit typo",
                    modifier = Modifier
                        .size(133.dp, 32.22.dp)
                        .align(Alignment.CenterStart)
                )
            }
            Spacer(modifier = Modifier.height(100.dp)) /*** 간격 조절 필요 **/

            Box(
                modifier = Modifier
                    .width(328.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                CustomText(
                    /*** 닉네임 안 넣음 **/ /*** 닉네임 안 넣음 **/
                    text = "어서오세요,",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.White,
                )

                CustomText(
                    text = today,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center

            ) {

                Row {
                    PillShape {
                        Image(
                            painter = painterResource(id = R.drawable.rank_cup),
                            contentDescription = "rank mark",
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        CustomText(
                            text = "임시임",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF8100B3)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    PillShape(width = 76.dp) {
                        Image(
                            painter = painterResource(id = R.drawable.xp_mark),
                            contentDescription = "rank mark",
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        CustomText(
                            text = "123",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF8100B3)
                        )

                        Spacer(modifier = Modifier.width(2.dp))

                        CustomText(
                            text = "XP",
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color(0xFF8100B3)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    PillShape(width = 169.dp) {
                        Text(text = "미완성")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box (
                modifier = Modifier
                    .size(328.dp, 186.dp)
                    .align(Alignment.CenterHorizontally),
            ){
                Row {
                    Box(
                        modifier = Modifier
                            .size(160.dp, 186.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(Color.White)

                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            CustomText(
                                text = "오늘의 미션🔥",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Canvas(
                                modifier = Modifier.size(128.dp, 1.dp)

                            ) {
                                drawLine(
                                    color = Color(0xFFA8A8A8),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 3f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)),
                                    cap = StrokeCap.Butt

                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            CustomText(
                                text = "• 임시\n임시",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color(0xFF222124)
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            CustomText(
                                text = "임시 XP",
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = Color(0xFF494949)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(  /*** 버튼 크기 다시 해야 함 **/
                                onClick = {},
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF2F2F2),
                                    contentColor = Color.Black
                                ),
                            ) {
                                CustomText(
                                    text = "학습하러 가기",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }

                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        RoundRect(
                            imageRes = R.drawable.rocket_main,
                            title = "행성 정복률",
                            value = "임시%"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RoundRect(
                            imageRes = R.drawable.fire_main,
                            title = "연속 학습일",
                            value = "임시일"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            RoundRect(
                width = 328.dp,
                height = 123.dp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                imageRes = R.drawable.clipboard_main,
                title = "미완성",
                titleFontWeight = FontWeight.SemiBold,
                titleFontSize = 20.sp,
                titleColor = Color(0xFF222124),
                value = "▶︎ 임시",
                valueFontWeight = FontWeight.Normal,
                valueFontSize = 14.sp,
                valueColor = Color(0xFF6D6D6D)
            )  /*** 이거 버튼인가? ***/

        }

    }
}

/*
@Preview(showBackground = true)
@Composable
fun View() {
   HomeScreen()
}
 */

@Composable
private fun CustomText (
    modifier: Modifier = Modifier,
    text: String,
    fontFamily: FontFamily = pretendard,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    color: Color = Color.Black,
) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize
        ),
        color = color,
        modifier = modifier
    )
}

@Composable
fun PillShape(
    modifier: Modifier = Modifier,
    width: Dp = 67.dp,
    height: Dp = 25.dp,
    backgroundColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(50),
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .background(backgroundColor, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun RoundRect(
    modifier: Modifier = Modifier,
    width: Dp = 160.dp,
    height: Dp = 89.dp,
    corner: Dp = 16.dp,
    backgroundColor: Color = Color.White,
    imageRes: Int,
    imageSize: Dp = 50.dp,
    title: String,
    value: String,
    titleFontSize: TextUnit = 14.sp,
    valueFontSize: TextUnit = 20.sp,
    titleFontWeight: FontWeight = FontWeight.Normal,
    valueFontWeight: FontWeight = FontWeight.SemiBold,
    titleColor: Color = Color.Black,
    valueColor: Color = Color.Black
) {
    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(corner))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                CustomText(
                    text = title,
                    fontSize = titleFontSize,
                    fontWeight = titleFontWeight,
                    color = titleColor
                )
                Spacer(Modifier.height(8.dp))
                CustomText(
                    text = value,
                    fontSize = valueFontSize,
                    fontWeight = valueFontWeight,
                    color = valueColor
                )
            }
            Spacer(modifier = Modifier.width(29.dp))
        }
    }
}


