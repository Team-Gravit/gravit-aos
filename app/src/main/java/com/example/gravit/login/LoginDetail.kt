package com.example.gravit.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.example.gravit.ui.theme.pretendard



@Composable
fun DetailScreen() {
    var text by remember { mutableStateOf("") }
    Column (
        modifier = Modifier
            .fillMaxSize()

    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 51.dp)
        ) {
            ImageButton(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "back arrow",
                modifier = Modifier
                    .size(48.dp)
                    .padding(start = 14.dp),
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

        Spacer(modifier = Modifier.height(30.dp))
        ProfileSwitcher()
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "닉네임 설정",
            modifier = Modifier.padding(start = 25.dp),
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        NameInputFiled(
            text = "닉",
            onTextChange = {text = it}
        )
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
        modifier = modifier
            .clickable { onClick() }
    )
}
@Composable
fun ProfileSwitcher() {
    val profileImages = listOf(
        R.drawable.profile1,
        R.drawable.profile2,
        R.drawable.profile3
    )

    var currentIndex by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        ImageButton(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = "Previous profile",
            modifier = Modifier.size(48.dp),
            onClick = {
                currentIndex = if (currentIndex == 0) profileImages.lastIndex else currentIndex - 1
            }
        )

        Spacer(modifier = Modifier.width(32.dp))

        Image(
            painter = painterResource(id = profileImages[currentIndex]),
            contentDescription = "Profile image",
            modifier = Modifier
                .size(178.dp)
                .clip(CircleShape)

        )

        Spacer(modifier = Modifier.width(35.dp))

        ImageButton(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "Next profile",
            modifier = Modifier.size(48.dp),
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

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            isError = isError,
            modifier = Modifier
                .padding(start = 25.dp)
                .size(325.dp, 50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = when {
                    isError -> Color.Red
                    isValid -> Color.Blue
                    else -> Color.Gray
                },
                unfocusedIndicatorColor = when {
                    isError -> Color.Red
                    isValid -> Color.Blue
                    else -> Color.Gray
                },
                errorIndicatorColor = Color.Red
            )
        )
        if (showErrorMessage) {
            Text(
                text = "이름은 두 글자 이상이어야 합니다",
                color = Color(0xFF868686),
                modifier = Modifier.padding(start = 25.dp, top = 8.dp),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            )
        }

    }
}