package com.example.gravit.main.User.Friend

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.TopBar
import com.example.gravit.api.FriendItem
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.pretendard

@Composable
fun AddFriend(navController: NavController) {
    val ctx = LocalContext.current
    val vm: AddFriendVM = viewModel(
        factory = AddFriendVMFactory(
            api = RetrofitInstance.api,
            appContext = ctx.applicationContext
        )
    )
    val ui by vm.state.collectAsState()

    if (ui.sessionExpired) {
        navController.navigate("error/404")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            TopBar(navController, title = "친구 추가")

            FriendSearchBar(
                query = ui.query,
                onQueryChange = vm::onQueryChange
            )

            when {
                ui.query.isBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "태그나 닉네임으로 친구를 검색하세요.",
                            fontFamily = pretendard,
                            fontSize = 14.sp,
                            color = Color(0xFF222222).copy(alpha = 0.6f)
                        )
                    }
                }

                ui.loading && ui.items.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFBA00FF))
                    }
                }

                else -> {
                    FriendResultList(
                        items = ui.items,
                        hasNext = ui.hasNext,
                        loadingMore = ui.loading && ui.items.isNotEmpty(),
                        onLoadNext = { vm.loadNext() },
                        onToggleFollow = { friend ->
                            vm.toggleFollow(
                                targetUserId = friend.userId,
                                currentlyFollowing = friend.isFollowing
                            )
                        }
                    )
                }
            }

            if (ui.error != null) {
                Text(
                    text = ui.error ?: "",
                    color = Color.Red,
                    fontFamily = pretendard,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
    if (ui.error != null) {
        Text(
            text = ui.error ?: "",
            color = Color.Red,
            fontFamily = pretendard,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun FriendSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(50.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color(0xFF000000).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50.dp)
                )
                .height(50.dp)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = pretendard,
                    color = Color(0xFF222124)
                ),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "친구 검색하기",
                            fontSize = 15.sp,
                            fontFamily = pretendard,
                            color = Color(0xFF222222).copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(Modifier.width(10.dp))

            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "검색",
                tint = Color(0xFF5E5E5E),
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
            )
        }
    }
}

@Composable
fun FriendResultList(
    items: List<FriendItem>,
    hasNext: Boolean,
    loadingMore: Boolean,
    onLoadNext: () -> Unit,
    onToggleFollow: (FriendItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(items) { friend ->
                FriendRow(
                    friend = friend,
                    onToggleFollow = onToggleFollow
                )
                Divider(color = Color(0xFF000000).copy(alpha = 0.06f))
            }
        }

        if (hasNext || loadingMore) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (loadingMore) {
                    CircularProgressIndicator(
                        color = Color(0xFFBA00FF),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "더 보기",
                        fontSize = 14.sp,
                        fontFamily = pretendard,
                        color = Color(0xFFBA00FF),
                        modifier = Modifier.clickable { onLoadNext() }
                    )
                }
            }
        }
    }
}

@Composable
fun FriendRow(
    friend: FriendItem,
    onToggleFollow: (FriendItem) -> Unit
) {
    val isFollowing = friend.isFollowing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    ProfilePalette.idToColor(friend.profileImgNumber)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile logo",
                modifier = Modifier.size(20.dp, 25.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = friend.nickname,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                color = Color(0xFF222222).copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = friend.handle,
                fontSize = 14.sp,
                fontFamily = pretendard,
                color = Color(0xFF222222).copy(alpha = 0.8f)
            )
        }

        val border =
            if (isFollowing) BorderStroke(1.dp, Color(0xFF000000).copy(alpha = 0.1f)) else null
        val buttonWidth = if (isFollowing) 120.dp else 90.dp
        val buttonHeight = 40.dp

        Button(
            onClick = { onToggleFollow(friend) },
            modifier = Modifier
                .width(buttonWidth)
                .height(buttonHeight),
            shape = RoundedCornerShape(10.dp),
            border = border,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFollowing) Color.White else Color(0xFF8100B3),
                contentColor = if (isFollowing) Color(0xFF222222).copy(alpha = 0.8f) else Color.White
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        ) {
            Text(
                text = if (isFollowing) "팔로우 취소" else "팔로우",
                fontSize = 15.sp,
                fontFamily = pretendard
            )
        }
    }
}
