package com.example.gravit.main.User

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.ui.theme.pretendard

@Composable
fun AddFriend(navController: NavController) {

    // ✅ 검색어 상태 & 핸들러
    var query by rememberSaveable { mutableStateOf("") }
    val onQueryChange: (String) -> Unit = { query = it }
    val onSearch: () -> Unit = {
        // TODO: query로 검색 실행
        // 예: viewModel.search(query)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "뒤로가기",
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(30.dp)
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    text = "친구추가",
                    fontSize = 20.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(text = "친구 검색하기",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    trailingIcon = {
                        IconButton(onClick = onSearch) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "검색",
                                tint = Color.Gray
                            )
                        }
                    },

                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(50)) // 배경색 + 둥근 모양
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(50)) // 테두리
                        .shadow(4.dp, RoundedCornerShape(50))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddFriendPreview() {
    // 프리뷰용 NavController 생성
    val navController = rememberNavController()
    AddFriend(navController = navController)
}