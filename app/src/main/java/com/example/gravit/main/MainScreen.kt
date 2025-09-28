package com.example.gravit.main

import BottomNavigationBar
import android.net.Uri
import android.os.Build
import android.util.Log
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
import com.example.gravit.main.Chapter.Lesson.LessonScreen
import com.example.gravit.main.League.LeagueScreen
import com.example.gravit.main.Chapter.Unit.Unit
import com.example.gravit.main.User.Setting.Account
import com.example.gravit.main.User.AddFriend
import com.example.gravit.main.User.FollowList
import com.example.gravit.main.User.Setting
import com.example.gravit.main.User.Notice
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
    Log.d("toLessonCompleted", "navigate 호출: $route") // 👈 경로 확인
    navigate(route) {
        launchSingleTop = true
    }
}
fun NavController.navigateToAccount(nickname: String) {
    navigate("user/account?nickname=${Uri.encode(nickname)}")
}

enum class FollowTab { Followers, Following }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(rootNavController: NavController) {
    val innerNavController = rememberNavController()
    val backStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    val hideBottomBar = currentRoute.startsWith("lesson/")

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
                    navArgument("chapterId") {type=NavType.IntType},
                    navArgument("unitId") {type=NavType.IntType},
                    navArgument("lessonId"){type=NavType.IntType},
                    navArgument("chapterName"){type=NavType.StringType; defaultValue = ""}
                )
            ) { backStackEntry ->
                val chapterId = backStackEntry.arguments!!.getInt("chapterId")
                val unitId = backStackEntry.arguments!!.getInt("unitId")
                val lessonId = backStackEntry.arguments!!.getInt("lessonId")
                val chapterName = backStackEntry.arguments!!.getString("chapterName").orEmpty()

                LessonScreen(
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
            //composable("user/setting/account") { Account(innerNavController) }

            composable("user/addfriend") { AddFriend(innerNavController) }
            composable("user/notice") { Notice(innerNavController) }

            //account 화면에 닉네임 인자 전달
            composable(
                route = "user/account?nickname={nickname}",
                arguments = listOf(
                    navArgument("nickname") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val nickname = backStackEntry.arguments?.getString("nickname").orEmpty()
                Account(
                    navController = innerNavController,
                    nickname = nickname,
                    onLogout = {
                        rootNavController.navigate("login choice") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
            }

            composable(
                route ="user/followList?tab={tab}",
                arguments = listOf(
                    navArgument("tab") { type = NavType.StringType; defaultValue = "followers"}
                )
            ) { backStackEntry ->
                val tabArg  = backStackEntry.arguments?.getString("tab") ?: "followers"
                val tab = if (tabArg.equals("following", true)) FollowTab.Following else FollowTab.Followers
                FollowList(
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
