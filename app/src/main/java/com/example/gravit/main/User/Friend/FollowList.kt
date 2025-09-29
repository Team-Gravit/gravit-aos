package com.example.gravit.main.User.Friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.FriendItem
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.FollowTab
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.pretendard

@Composable
fun FollowList(
    navController: NavController,
    initialTab: FollowTab
) {
    var tab by remember { mutableStateOf(initialTab) }

    val context = LocalContext.current
    val vm: FollowListVM = viewModel(factory = FollowListVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()
    val followerCount by vm.followerCount.collectAsState()
    val followingCount by vm.followingCount.collectAsState()

    LaunchedEffect(tab) {
        when (tab) {
            FollowTab.Followers -> vm.loadFollower()
            FollowTab.Following -> vm.loadFollowing()
        }
    }

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
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(30.dp)
                        .clickable { navController.popBackStack() },
                    tint = Color.Black
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    "팔로워/팔로잉",
                    fontSize = 16.sp,
                    fontFamily = pretendard,
                    color = Color(0xFF222124),
                    fontWeight = FontWeight.SemiBold
                )
            }

            FollowTabBar(
                tab = tab,
                onTabChange = { tab = it },
                followerCount = followerCount,
                followingCount = followingCount
            )

            when (val s = ui) {
                is FollowListVM.UiState.Success -> {
                    FriendList(s.data)
                }
                is FollowListVM.UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is FollowListVM.UiState.Failed -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(s.message ?: "불러오지 못했습니다.")
                    }
                }
                is FollowListVM.UiState.SessionExpired -> {
                }
            }
        }
    }
}

@Composable
fun FriendList(users: List<FriendItem>) {
    LazyColumn {
        items(users) { user ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(ProfilePalette.idToColor(user.profileImgNumber)),
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
                        user.nickname,
                        fontFamily = pretendard
                    )
                    Text(
                        text = if (user.handle.startsWith("@")) user.handle else "@${user.handle}",
                        fontFamily = pretendard,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowTabBar(
    tab: FollowTab,
    onTabChange: (FollowTab) -> Unit,
    followerCount: Int,
    followingCount: Int
) {
    PrimaryTabRow(
        selectedTabIndex = tab.ordinal,
        containerColor = Color.White,
        indicator = {
            Box(
                Modifier
                    .tabIndicatorOffset(tab.ordinal, matchContentSize = false)
                    .height(2.dp)
                    .background(Color.Black)
            )
        }
    ) {
        Tab(
            selected = tab == FollowTab.Followers,
            onClick = { onTabChange(FollowTab.Followers) },
            modifier = Modifier.height(41.dp)
        ) {
            Text(
                text = "$followerCount 팔로워",
                fontSize = 14.sp,
                fontFamily = pretendard,
                color = if (tab == FollowTab.Followers) Color(0xFF030303) else Color(0xFFCCCCCC),
                fontWeight = FontWeight.SemiBold
            )
        }
        Tab(
            selected = tab == FollowTab.Following,
            onClick = { onTabChange(FollowTab.Following) },
            modifier = Modifier.height(41.dp)
        ) {
            Text(
                text = "$followingCount 팔로잉",
                fontSize = 14.sp,
                fontFamily = pretendard,
                color = if (tab == FollowTab.Following) Color(0xFF030303) else Color(0xFFCCCCCC),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}