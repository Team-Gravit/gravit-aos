package com.example.gravit.main.User.Friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.gravit.TopBar
import com.example.gravit.api.FriendUserSummary
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.navigation.FollowTab
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.pretendard

@Composable
fun FollowList(
    navController: NavController,
    initialTab: FollowTab
) {
    val ctx = LocalContext.current
    val vm: FriendListVM = viewModel(
        factory = FriendListVMFactory(
            api = RetrofitInstance.api,
            appContext = ctx.applicationContext
        )
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(initialTab) {
        vm.init()

        val mappedTab = when (initialTab) {
            FollowTab.Followers -> FriendTab.Follower
            FollowTab.Following -> FriendTab.Following
        }

        vm.setTab(mappedTab)
    }

    if (ui.sessionExpired) {
        navController.navigate("error/404")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar(navController, title = "팔로우 / 팔로잉")

        FriendTabBar(
            selectedTab = ui.selectedTab,
            followerCount = ui.followerCount,
            followingCount = ui.followingCount,
            onTabSelected = vm::setTab
        )

        if (ui.loading && ui.followerItems.isEmpty() && ui.followingItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFBA00FF))
            }
        } else {
            when (ui.selectedTab) {
                FriendTab.Follower -> {
                    FollowerListContent(
                        items = ui.followerItems,
                        hasNext = ui.followerHasNext,
                        loadingMore = ui.loading,
                        onLoadNext = { vm.loadFollowerNext() },
                        onReject = { vm.rejectFollower(it) }
                    )
                }
                FriendTab.Following -> {
                    FollowingListContent(
                        items = ui.followingItems,
                        hasNext = ui.followingHasNext,
                        loadingMore = ui.loading,
                        onLoadNext = { vm.loadFollowingNext() },
                        onUnfollow = { vm.unfollowFromFollowing(it) }
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
}

@Composable
private fun FriendTabBar(
    selectedTab: FriendTab,
    followerCount: Int,
    followingCount: Int,
    onTabSelected: (FriendTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FriendTabItem(
            text = "${followerCount} 팔로워",
            selected = selectedTab == FriendTab.Follower,
            onClick = { onTabSelected(FriendTab.Follower) },
            modifier = Modifier.weight(1f)
        )
        FriendTabItem(
            text = "${followingCount} 팔로잉",
            selected = selectedTab == FriendTab.Following,
            onClick = { onTabSelected(FriendTab.Following) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FriendTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontFamily = pretendard,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color(0xFF222222) else Color(0xFF222222).copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(
                    if (selected) Color(0xFF030303) else Color(0xFFDCDCDC)
                )
        )
    }
}

@Composable
private fun FollowerListContent(
    items: List<FriendUserSummary>,
    hasNext: Boolean,
    loadingMore: Boolean,
    onLoadNext: () -> Unit,
    onReject: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(items) { user ->
                FollowerRow(user = user, onReject = { onReject(user.id) })
            }
            item {
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
private fun FollowingListContent(
    items: List<FriendUserSummary>,
    hasNext: Boolean,
    loadingMore: Boolean,
    onLoadNext: () -> Unit,
    onUnfollow: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(items) { user ->
                FollowingRow(user = user, onUnfollow = { onUnfollow(user.id) })
            }
            item {
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
private fun FollowerRow(
    user: FriendUserSummary,
    onReject: () -> Unit
) {
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
                .background(ProfilePalette.idToColor(user.profileImgNumber)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile",
                tint = Color.White,
                modifier = Modifier.size(20.dp, 25.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.nickname,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                color = Color(0xFF222124)
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = "@" + user.handle,
                fontSize = 14.sp,
                fontFamily = pretendard,
                color = Color(0xFF494949)
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable { onReject() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "팔로워 삭제",
                tint = Color(0xFF494949),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FollowingRow(
    user: FriendUserSummary,
    onUnfollow: () -> Unit
) {
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
                .background(ProfilePalette.idToColor(user.profileImgNumber)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile",
                tint = Color.White,
                modifier = Modifier.size(20.dp, 25.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.nickname,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                color = Color(0xFF222124)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "@" + user.handle,
                fontSize = 14.sp,
                fontFamily = pretendard,
                color = Color(0xFF494949)
            )
        }

        Button(
            onClick = onUnfollow,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF222222).copy(alpha = 0.8f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFF000000).copy(alpha = 0.1f)
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = "팔로우 취소",
                fontSize = 15.sp,
                fontFamily = pretendard
            )
        }
    }
}