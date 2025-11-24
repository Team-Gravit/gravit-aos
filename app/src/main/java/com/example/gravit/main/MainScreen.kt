package com.example.gravit.main

import BottomNavigationBar
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gravit.error.NotFoundScreen
import com.example.gravit.error.UnauthorizedScreen
import com.example.gravit.main.Home.HomeScreen
import com.example.gravit.main.Chapter.ChapterScreen
import com.example.gravit.main.Chapter.Lesson.LessonComplete
import com.example.gravit.main.Chapter.Lesson.ProblemScreen
import com.example.gravit.main.Chapter.Unit.Unit
import com.example.gravit.main.League.LeagueScreen
import com.example.gravit.main.User.Notice.Notice
import com.example.gravit.main.User.Notice.NoticeDetail
import com.example.gravit.main.User.Setting
import com.example.gravit.main.User.Setting.Account
import com.example.gravit.main.User.Setting.DeletionComplete
import com.example.gravit.main.User.Setting.DeletionGuard
import com.example.gravit.main.User.Setting.PrivacyPolicy
import com.example.gravit.main.User.UserScreen

fun NavController.toLesson(
    chapterId: Int,
    unitId: Int,
    lessonId: Int,
    chapterName: String,
    togo: String,
) {
    val encodedName = Uri.encode(chapterName)
    navigate("$togo/$chapterId/$unitId/$lessonId/$encodedName"){
        popUpTo(0) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
fun NavController.toLessonCompleted(
    chapterId: Int,
    unitId: Int,
    lessonId: Int,
    chapterName: String,
    accuracy: Int = 0,
    learningTime: Int = 0
) {
    val encodedName = Uri.encode(chapterName)
    val route = "lesson/complete/$chapterId/$unitId/$lessonId?chapterName=$encodedName&accuracy=$accuracy&learningTime=$learningTime"
    navigate(route) {
        launchSingleTop = true
    }
}

enum class FollowTab { Followers, Following }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(rootNavController: NavController) {
    val innerNavController = rememberNavController()
    val backStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    val hideBottomBar = currentRoute.startsWith("lesson/") ||
                        currentRoute.startsWith("user/notice") ||
                        currentRoute.startsWith("user/account") ||
                        currentRoute.startsWith("user/privacypolicy") ||
                        currentRoute.startsWith("error/") ||
                        currentRoute.startsWith("user/deletion-complete")

    val goToLoginChoice: () -> Unit = {
        rootNavController.navigate("login choice") {
            popUpTo(0) { inclusive = true }   // 백스택 싹 비우기
            launchSingleTop = true
            restoreState = false
        }
    }

    Scaffold(
        bottomBar = { if (!hideBottomBar) { BottomNavigationBar(innerNavController) } },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        DeletionGuard(navController = innerNavController) {
            NavHost(
                navController = innerNavController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen(innerNavController, goToLoginChoice) }

                //chapter
                composable("chapter") { ChapterScreen(innerNavController, goToLoginChoice) }

                composable(
                    route = "units/{chapterId}",
                    arguments = listOf(navArgument("chapterId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val chapterId = backStackEntry.arguments!!.getInt("chapterId")
                    Unit(
                        navController = innerNavController,
                        chapterId = chapterId,
                        onSessionExpired = goToLoginChoice
                    )
                }

                composable(
                    route = "lesson/{chapterId}/{unitId}/{lessonId}/{chapterName}",
                    arguments = listOf(
                        navArgument("chapterId") { type = NavType.IntType },
                        navArgument("unitId") { type = NavType.IntType },
                        navArgument("lessonId") { type = NavType.IntType },
                        navArgument("chapterName") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStackEntry ->
                    val chapterId = backStackEntry.arguments!!.getInt("chapterId")
                    val unitId = backStackEntry.arguments!!.getInt("unitId")
                    val lessonId = backStackEntry.arguments!!.getInt("lessonId")
                    val chapterName = backStackEntry.arguments!!.getString("chapterName").orEmpty()

                    ProblemScreen(
                        navController = innerNavController,
                        chapterId = chapterId,
                        chapterName = chapterName,   // 헤더 표시용
                        unitId = unitId,
                        lessonId = lessonId,
                        onSessionExpired = goToLoginChoice
                    )
                }

                composable(
                    route = "lesson/complete/{chapterId}/{unitId}/{lessonId}?chapterName={chapterName}&accuracy={accuracy}&learningTime={learningTime}",
                    arguments = listOf(
                        navArgument("chapterId") { type = NavType.IntType },
                        navArgument("unitId") { type = NavType.IntType },
                        navArgument("lessonId") { type = NavType.IntType },
                        navArgument("chapterName") { type = NavType.StringType; defaultValue = "" },
                        navArgument("accuracy") { type = NavType.IntType; defaultValue = 0 },
                        navArgument("learningTime") { type = NavType.IntType; defaultValue = 0 }
                    )
                ) { backStackEntry ->
                    val chapterId = backStackEntry.arguments!!.getInt("chapterId")
                    val unitId = backStackEntry.arguments!!.getInt("unitId")
                    val lessonId = backStackEntry.arguments!!.getInt("lessonId")
                    val chapterName = backStackEntry.arguments?.getString("chapterName").orEmpty()
                    val accuracy = backStackEntry.arguments!!.getInt("accuracy")
                    val learningTime = backStackEntry.arguments!!.getInt("learningTime")
                    LessonComplete(
                        navController = innerNavController,
                        chapterId = chapterId,
                        unitId = unitId,
                        lessonId = lessonId,
                        chapterName = chapterName,
                        accuracy = accuracy,
                        learningTime = learningTime,
                        onSessionExpired = goToLoginChoice
                    )
                }

                composable("league") { LeagueScreen(innerNavController, goToLoginChoice) }

                composable("user") { UserScreen(innerNavController, goToLoginChoice) }

                composable("user/setting") {
                    Setting(
                        navController = innerNavController,
                        onLogout = {
                            rootNavController.navigate("login choice") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    )
                }

                composable("user/privacypolicy") { PrivacyPolicy(innerNavController) }

                // account
                composable("user/account") { Account(innerNavController) }

                // notice
                composable("user/notice") { Notice(innerNavController) }
                composable(
                    route = "user/notice/detail/{noticeId}",
                    arguments = listOf(
                        navArgument("noticeId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val id = backStackEntry.arguments!!.getLong("noticeId")
                    NoticeDetail(navController = innerNavController, noticeId = id)
                }

                // 탈퇴 완료 화면
                composable("user/deletion-complete") {
                    DeletionComplete(innerNavController)
                }

                // 친구 추가(검색)
                composable("user/addfriend") {
                    com.example.gravit.main.User.Friend.AddFriend(innerNavController)
                }

                // 팔로워/팔로잉
                composable(
                    route = "user/followList?tab={tab}",
                    arguments = listOf(
                        navArgument("tab") { type = NavType.StringType; defaultValue = "followers" }
                    )
                ) { backStackEntry ->
                    val tabArg = backStackEntry.arguments?.getString("tab") ?: "followers"
                    val tab = if (tabArg.equals("following", true))
                        FollowTab.Following
                    else
                        FollowTab.Followers

                    com.example.gravit.main.User.Friend.FollowList(
                        navController = innerNavController,
                        initialTab = tab
                    )
                }
                //에러
                composable("error/401") { UnauthorizedScreen(navController = innerNavController) }
                composable("error/404") { NotFoundScreen(navController = innerNavController) }
            }
        }
    }
}
