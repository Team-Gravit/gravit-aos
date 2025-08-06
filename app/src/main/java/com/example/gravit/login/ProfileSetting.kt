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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import com.example.gravit.ui.theme.pretendard



@Composable
fun ProfileSetting(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        CompositionLocalProvider(
            LocalScreenWidth provides screenWidth,
            LocalScreenHeight provides screenHeight
        ) {
            var text by remember { mutableStateOf("") }

            Box (modifier = Modifier.fillMaxSize()){

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopStart)
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
                            onClick = { navController.popBackStack() }
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
                        onTextChange = { text = it }
                    )

                }
            }

            CustomButton(
                text = "다음",
                onClick = { navController.navigate("profile finish") },
                enabled = isValidNickname(text),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = screenHeight * (34f / 812f)) // 하단 여백 조절
                    .size(screenWidth * (325f / 375f), screenHeight * (60f / 812f))
            )
        }

    }
}

@Composable
fun ImageButton(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.clickable { onClick() }
    )
}

@Composable
fun ProfileSwitcher(
    onProfileSelected: (Int) -> Unit = {} //서버 전송용
) {
    val profileColor = listOf( //컬러 팔레트가 안 보여서 임시로 9가지 색상을 넣어놨음
        Color.Red, Color.Black, Color.Blue, Color.Cyan, Color.Yellow
        ,Color.DarkGray, Color.Gray, Color.Green, Color.Magenta,
    )
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    var currentIndex by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = screenWidth * (35f / 375f),
                vertical = screenHeight * (30f / 812f)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        ImageButton(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = "Previous profile",
            modifier = Modifier.size(screenWidth * (48f / 375f)),
            onClick = {
                currentIndex = if (currentIndex == 0) profileColor.lastIndex else currentIndex - 1
                onProfileSelected(currentIndex + 1)
            }
        )

        Box( //선택에 따라 뒷배경 색이 바뀌도록
            modifier = Modifier
                .size(screenWidth * (178f / 375f))
                .clip(CircleShape)
                .background(profileColor[currentIndex]),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile logo",
                modifier = Modifier.size(screenWidth * (71.96f / 375f), screenHeight * (90.89f / 812f))
            )
        }

        ImageButton(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "Next profile",
            modifier = Modifier.size(screenWidth * (48f / 375f)),
            onClick = {
                currentIndex = if (currentIndex == profileColor.lastIndex) 0 else currentIndex + 1
                onProfileSelected(currentIndex + 1)
            },
        )
    }
}

fun isValidNickname(nickname: String): Boolean { //닉네임 규정
    val regex = "^[가-힣a-zA-Z0-9]{2,8}$".toRegex()
    return regex.matches(nickname)
}

@Composable
fun NameInputFiled (
    text: String,
    onTextChange: (String) -> Unit,
) {
    val isValid = isValidNickname(text)
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
                .padding(
                    start = screenWidth * (25f / 375f),
                    top = screenHeight * (12f / 815f)
                )
                .size(
                    width = screenWidth * (325f / 375f),
                    height = screenHeight * (50f / 815f)
                ),
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

        if (showErrorMessage) { //규정에 맞지 않을 때
            Text(
                text = "공백, 특수문자 없이 2~8자로 입력하세요",
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
fun CustomButton( //profile finish랑 똑같은 버튼이길래 함수로 만들었음
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val activeBackground = Color(0xFF8100B3)
    val inactiveBackground = activeBackground.copy(alpha = 0.5f) // 50% 투명도
    val activeTextColor = Color.White
    val inactiveTextColor = Color.White.copy(alpha = 0.5f)

    Button( //비활성화 조건 추가함
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) activeBackground else inactiveBackground,
            contentColor = if (enabled) activeTextColor else inactiveTextColor,
            disabledContainerColor = inactiveBackground,
            disabledContentColor = inactiveTextColor
        ),
        enabled = enabled
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