package com.example.gravit.main.User

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.api.FriendUser
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun AddFriend(navController: NavController) {

    val context = LocalContext.current
    val vm: AddFriendVM = viewModel(
        factory = AddFriendVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    var query by rememberSaveable { mutableStateOf("") }
    val onQueryChange: (String) -> Unit = { query = it }
    val onSearch: () -> Unit = { vm.search(query) }

    if (ui is AddFriendVM.UiState.SessionExpired) {
        LaunchedEffect(Unit) {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
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
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF222222)
                )
            }

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(74.dp)

            ) {
                TextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = "친구 검색하기",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    },
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
                        errorIndicatorColor = Color.Transparent
                    ),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White, RoundedCornerShape(50)) // 배경색 + 둥근 모양
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(50)) // 테두리
                        .shadow(4.dp, RoundedCornerShape(50))
                )
            }

            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize()
            ) {
                when (val s = ui) {
                    is AddFriendVM.UiState.Success -> {
                        val listState = rememberLazyListState()

                        // 끝 근처 자동 로드
                        LaunchedEffect(listState, s.results.size, s.isLoadingNext, s.hasNext) {
                            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
                                .distinctUntilChanged()
                                .collect { lastIndex ->
                                    if (lastIndex >= s.results.lastIndex - 3 && s.hasNext && !s.isLoadingNext) {
                                        vm.loadNext()
                                    }
                                }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(items = s.results, key = { it.userId }) { user: FriendUser ->
                                FriendCell(
                                    result = user,
                                    inUndo = s.showUndo.contains(user.userId),        // ← 타입 맞추기
                                    loading = s.itemLoading.contains(user.userId),
                                    onToggleFollow = { vm.toggleFollow(user.userId) }
                                )
                            }
                            item {
                                when {
                                    s.isLoadingNext -> {
                                        Box(
                                            Modifier.fillMaxWidth().padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    !s.hasNext && s.results.isNotEmpty() -> {
                                        Box(
                                            Modifier.fillMaxWidth().padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("마지막입니다.")
                                        }
                                    }
                                    s.lastError != null -> {
                                        Column(
                                            Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(s.lastError!!)
                                            Spacer(Modifier.height(8.dp))
                                            OutlinedButton(onClick = { vm.loadNext() }) {
                                                Text("다시 시도")
                                            }
                                        }
                                    }
                                    s.results.isEmpty() -> {
                                        Box(
                                            Modifier.fillMaxWidth().padding(24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("검색 결과가 없습니다")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is AddFriendVM.UiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is AddFriendVM.UiState.Failed -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                            //오류발생
                        }
                    }
                    else -> Unit
                }
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
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ProfilePalette.idToColor(result.profileImgNumber)),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.profile_logo),
                    contentDescription = "profile logo",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = result.nickname,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pretendard,
                    fontSize = 14.sp,
                    color = Color(0xFF222222).copy(alpha = 0.8f)
                )
                Text(
                    text = result.handle,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    fontSize = 14.sp,
                    color = Color(0xFF222222).copy(alpha = 0.8f)
                )
            }

        }
        FollowButton(
            isFollowing = result.isFollowing,
            inUndo = inUndo,
            loading = loading,
            onClick = onToggleFollow
        )
    }
}

@Composable
private fun FollowButton(
    isFollowing: Boolean,
    inUndo: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    val (bg, fg, border, label) = when {
        !isFollowing -> arrayOf(Color(0xFF8100B3), Color.White, null, "팔로우")
        inUndo -> arrayOf(Color.White, Color(0xFF222222).copy(alpha = 0.8f), Color.Black.copy(alpha = 0.1f), "팔로우 취소")
        else -> arrayOf(Color.White, Color(0xFF222222).copy(alpha = 0.8f), Color.Black.copy(alpha = 0.1f), "팔로우")
    }

    Box(
        modifier = Modifier
            .height(36.dp)
            .defaultMinSize(minWidth = 96.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg as Color)
            .then(if (border != null) Modifier.border(1.dp, border as Color, RoundedCornerShape(8.dp)) else Modifier)
            .clickable(enabled = !loading) { onClick() }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
        } else {
            Text(label as String,
                color = fg as Color,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
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