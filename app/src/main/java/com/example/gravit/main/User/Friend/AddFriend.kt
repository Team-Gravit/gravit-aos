package com.example.gravit.main.User.Friend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.FriendUser
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.pretendard

@Composable
fun AddFriend(navController: NavController) {
    val context = LocalContext.current
    val vm: AddFriendVM = viewModel(factory = AddFriendVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()

    var query by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "뒤로가기",
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(30.dp)
                        .clickable { navController.popBackStack() }
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "친구추가",
                    fontSize = 20.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222124)
                )
            }

            Divider(color = Color.Black.copy(alpha = 0.1f))

            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(
                    "친구 검색하기",
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF222222).copy(alpha = 0.6f)
                ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        val q = query.trim()
                        if (q.isNotEmpty()) vm.search(q)
                    }) {
                        Icon(Icons.Filled.Search, contentDescription = "검색", tint = Color.Gray)
                    }
                },
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(16.dp)
                    .shadow(4.dp, RoundedCornerShape(50))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(50))
            )

            when (val s = ui) {
                is AddFriendVM.UiState.Success -> {
                    val listState = rememberLazyListState()
                    LazyColumn(state = listState) {
                        items(s.results, key = { it.userId }) { user: FriendUser ->
                            FriendCell(
                                result = user,
                                inUndo = s.showUndo.contains(user.userId),
                                loading = s.itemLoading.contains(user.userId),
                                onToggleFollow = { vm.toggleFollow(user.userId) }
                            )
                        }
                    }
                }
                is AddFriendVM.UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun FriendCell(
    result: FriendUser,
    inUndo: Boolean,
    loading: Boolean,
    onToggleFollow: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ProfilePalette.idToColor(result.profileImgNumber)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile logo",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                result.nickname,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard
            )
            Text(
                text = if (result.handle.startsWith("@")) result.handle else "@${result.handle}",
                fontFamily = pretendard,
                color = Color.Gray
            )
        }
        Spacer(Modifier.weight(1f))
        FollowButton(result.isFollowing, inUndo, loading, onToggleFollow)
    }
}

@Composable
fun FollowButton(
    isFollowing: Boolean,
    inUndo: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    val bg: Color
    val fg: Color
    val borderColor: Color?
    val label: String
    val fontSize: TextUnit

    when {
        !isFollowing -> {
            bg = Color(0xFF8100B3)
            fg = Color.White
            borderColor = null
            label = "팔로우"
            fontSize = 15.sp
        }
        inUndo -> {
            bg = Color.White
            fg = Color(0xFF4E4E4E)
            borderColor = Color(0xFFE6E6E6)
            label = "팔로우 취소"
            fontSize = 15.sp
        }
        else -> {
            bg = Color.White
            fg = Color(0xFF4E4E4E)
            borderColor = Color(0xFFE6E6E6)
            label = "팔로우 취소"
            fontSize = 15.sp
        }
    }

    Box(
        modifier = Modifier
            .height(36.dp)
            .defaultMinSize(minWidth = 96.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .then(
                if (borderColor != null)
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp))
                else Modifier
            )
            .clickable(enabled = !loading) { onClick() }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(
                text = label,
                color = fg,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = fontSize
            )
        }
    }
}