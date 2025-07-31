package com.example.gravit.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.R
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import com.example.gravit.ui.theme.pretendard



@Composable
fun DetailScreen() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        CompositionLocalProvider(
            LocalScreenWidth provides screenWidth,
            LocalScreenHeight provides screenHeight
        ) {
            var text by remember { mutableStateOf("") }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = screenHeight * (51f / 812f))
                ) {
                    ImageButton(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "back arrow",
                        modifier = Modifier
                            .size(screenWidth * (48f / 375f))
                            .padding(start = screenWidth * (14f / 375f)),
                        onClick = {}
                    )

                    Text(
                        text = "로그인",
                        modifier = Modifier.align(Alignment.Center),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )
                }
                ProfileSwitcher()
                Text(
                    text = "닉네임 설정",
                    modifier = Modifier.padding(start = screenWidth * (25f / 375f)),
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                )

                NameInputFiled(
                    text = text,
                    onTextChange = {}
                )

                CustomButton(
                    text = "다음",
                    onClick =
                        {
                            // 다음으로
                        },
                    modifier = Modifier
                        .padding(top = screenHeight * (298f / 812f))
                        .size(screenWidth * (325f / 375f), screenHeight * (60f / 740f))
                        .align(Alignment.CenterHorizontally)

                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    DetailScreen()
}

@Composable
fun ImageButton(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.clickable { onClick() }
    )
}

@Composable
fun ProfileSwitcher() {
    val profileImages = listOf(
        R.drawable.profile1,
        R.drawable.profile2,
        R.drawable.profile3
    )
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    var currentIndex by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = screenWidth * (35f / 375f),
                     vertical = screenHeight * (30f / 812f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        ImageButton(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = "Previous profile",
            modifier = Modifier.size(screenWidth * (48f / 375f)),
            onClick = {
                currentIndex = if (currentIndex == 0) profileImages.lastIndex else currentIndex - 1
            }
        )

        Image(
            painter = painterResource(id = profileImages[currentIndex]),
            contentDescription = "Profile image",
            modifier = Modifier
                .size(screenWidth * (178f / 375f))
                .clip(CircleShape)

        )

        ImageButton(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "Next profile",
            modifier = Modifier.size(screenWidth * (48f / 375f)),
            onClick = {
                currentIndex = if (currentIndex == profileImages.lastIndex) 0 else currentIndex + 1
            }
        )
    }
}

@Composable
fun NameInputFiled (
    text: String,
    onTextChange: (String) -> Unit,
) {
    val isValid = text.length >= 2
    val isError = text.isNotEmpty() && !isValid
    val showErrorMessage = isError
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            isError = isError,
            placeholder = {
                Text(
                    text = "닉네임",
                    color = Color(0xFF868686), // 회색
                    fontFamily = pretendard,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            textStyle = TextStyle(
                color = if (isError) Color.Red else Color.Black,
                fontFamily = pretendard,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .padding(start = screenWidth * (25f / 375f),
                         top = screenHeight * (12f / 815f))
                .size(width = screenWidth * (325f / 375f),
                      height = screenHeight * (50f / 815f)),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedIndicatorColor = if (isError) Color.Red else Color.Black,
                unfocusedIndicatorColor = if (isError) Color.Red else Color(0xFFC3C3C3),
                errorIndicatorColor = Color.Red,
                cursorColor = if (isError) Color.Red else Color.Black
            )
        )

        if (showErrorMessage) {
            Text(
                text = "이름은 두 글자 이상이어야 합니다",
                color = Color(0xFF868686),
                modifier = Modifier.padding(start = screenWidth * (25f / 375f),
                                            top = screenHeight * (8f / 815f)),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            )
        }

    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8100B3),
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}