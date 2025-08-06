package com.example.gravit.main.Study

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import com.example.gravit.ui.theme.mbc1961
import com.example.gravit.ui.theme.pretendard

@Composable
fun StudyScreen(navController: NavController){
    BoxWithConstraints (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth
        CompositionLocalProvider(
            LocalScreenWidth provides screenWidth,
            LocalScreenHeight provides screenHeight
        ){
            Column (
                modifier = Modifier.fillMaxSize()
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight * (70f / 740f))
                        .background(Color.White),
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
                Column (
                    modifier = Modifier
                        .padding(
                            start = screenWidth * (16f / 360f),
                            top = screenHeight * (16f / 740f)
                        )
                        .fillMaxSize()
                        .background(Color(0xF2F2F2))
                        .verticalScroll(rememberScrollState())
                ){
                    Row {
                        ChapterButton(
                            planet = R.drawable.earth,
                            filledSegments = 1,
                            text = "자료구조",
                            onClick = { navController.navigate("study/earth") }
                        )
                        Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))
                        ChapterButton(
                            planet = R.drawable.earth,
                            filledSegments = 2,
                            text = "자료구조",
                            onClick = { navController.navigate("study/jupiter") }
                        )
                    }
                    Spacer(modifier = Modifier.height(screenHeight * (8f / 740f)))
                    Row {
                        ChapterButton(
                            planet = R.drawable.earth,
                            filledSegments = 3,
                            text = "자료구조",
                            onClick = { navController.navigate("study/mars") }
                        )
                        Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))
                        ChapterButton(
                            planet = R.drawable.earth,
                            filledSegments = 4,
                            text = "자료구조",
                            onClick = { navController.navigate("study/mercury") }
                        )
                    }
                    Spacer(modifier = Modifier.height(screenHeight * (8f / 740f)))
                    Row {
                        ChapterButton(
                            planet = R.drawable.earth,
                            filledSegments = 5,
                            text = "자료구조",
                            onClick = { navController.navigate("study/moon") }
                        )
                        Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))
                        ChapterButton(
                            planet = R.drawable.earth,
                            filledSegments = 6,
                            text = "자료구조",
                            onClick = { navController.navigate("study/saturn") }
                        )
                    }
                    Spacer(modifier = Modifier.height(screenHeight * (8f / 740f)))
                    Row {
                        ChapterButton(
                            planet = R.drawable.earth,
                            filledSegments = 7,
                            text = "자료구조",
                            onClick = { navController.navigate("study/uranus") }
                        )
                        Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))
                        ChapterButton(
                            planet = R.drawable.earth,
                            filledSegments = 10,
                            text = "자료구조",
                            onClick = { navController.navigate("study/venus") }
                        )
                    }
                    Spacer(modifier = Modifier.height(screenHeight * (16f / 740f)))

                }

            }
        }

    }
}


@Composable
fun ChapterButton (
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filledSegments: Int,
    planet: Int
) {
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    Button(
        modifier = Modifier
            .size(screenWidth * (160f / 360f), screenHeight * (166f / 740f)),
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
                            modifier = Modifier.size(
                                screenWidth * (70f / 360f),
                                screenHeight * (24f / 740f)
                            )
                        )
                        Spacer(modifier = Modifier.width(screenWidth * (46f / 360f)))

                        Icon(
                            painter = painterResource(id = R.drawable.info),
                            contentDescription = null,
                            modifier = Modifier
                                .size(screenWidth * (24f / 360f))
                                .clickable {}
                        )
                    }
                    Spacer(modifier = Modifier.height(screenHeight * (12f / 740f)))
                    RoundedGauge(
                        filledSegments = filledSegments,
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
    totalSegments: Int = 10,
    filledSegments: Int,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val screenHeight = LocalScreenHeight.current

    Column(modifier = modifier.size(width, height * 3)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFEEEEEE))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(width * (filledSegments.toFloat() / totalSegments))
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFBA00FF))
            )
        }
        Spacer(modifier = Modifier.height(screenHeight * (3f / 740f)))
        Text(
            text = "$filledSegments/$totalSegments",
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
    StudyScreen(navController)
}
