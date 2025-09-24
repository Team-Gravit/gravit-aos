package com.example.gravit.main

import BottomNavigationBar
import android.net.Uri
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
import com.example.gravit.main.Home.HomeScreen
import com.example.gravit.main.Chapter.ChapterScreen
import com.example.gravit.main.Chapter.Lesson.LessonComplete
import com.example.gravit.main.Chapter.Lesson.LessonScreen
import com.example.gravit.main.League.LeagueScreen
import com.example.gravit.main.Chapter.Unit.Unit
import com.example.gravit.main.User.Account
import com.example.gravit.main.User.AddFriend
import com.example.gravit.main.User.FollowList
import com.example.gravit.main.User.Setting
import com.example.gravit.main.User.Setting.Notice
import com.example.gravit.main.User.Setting.PrivacyPolicy
import com.example.gravit.main.User.Setting.ScreenSetting
import com.example.gravit.main.User.Setting.Service
import com.example.gravit.main.User.Setting.ToS
import com.example.gravit.main.User.UserScreen

fun build(togo: String, chapterId: Int, unitId: Int, lessonId: Int, chapterName: String): String {
    val encodedName = Uri.encode(chapterName) // 한글/공백/특수문자
    return "$togo/$chapterId/$unitId/$lessonId/$encodedName"
}

fun NavController.navigateTo(
    chapterId: Int,
    unitId: Int,
    lessonId: Int,
    chapterName: String,
    togo: String
) {
    navigate(build(togo, chapterId, unitId, lessonId, chapterName)){
        popUpTo(0) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

fun NavController.navigateToAccount(nickname: String) {
    navigate("user/account?nickname=${Uri.encode(nickname)}")
}

enum class FollowTab { Followers, Following }

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
            composable("home") { HomeScreen(innerNavController) }

            //chapter
            composable("chapter") { ChapterScreen(innerNavController) }

            composable(
                route = "units/{chapterId}" +
                        "?name={name}&desc={desc}&total={total}&completed={completed}",
                arguments = listOf(
                    navArgument("chapterId") { type = NavType.IntType },
                    navArgument("name") { type = NavType.StringType; defaultValue = "" },
                    navArgument("desc") { type = NavType.StringType; defaultValue = "" },
                    navArgument("total") { type = NavType.IntType; defaultValue = 10 },
                    navArgument("completed") { type = NavType.IntType; defaultValue = 0 },
                )
            ) { backStackEntry ->
                val chapterId   = backStackEntry.arguments!!.getInt("chapterId")
                val name        = backStackEntry.arguments!!.getString("name").orEmpty()
                val desc        = backStackEntry.arguments!!.getString("desc").orEmpty()
                val total       = backStackEntry.arguments!!.getInt("total")
                val completed   = backStackEntry.arguments!!.getInt("completed")

                //Unit 화면으로 즉시 표시용 인자 전달
                Unit(
                    navController = innerNavController,
                    chapterId = chapterId,
                    initialName = name,
                    initialDesc = desc,
                    initialTotalUnits = total,
                    initialCompletedUnits = completed
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
                val chapterId   = backStackEntry.arguments!!.getInt("chapterId")
                val unitId      = backStackEntry.arguments!!.getInt("unitId")
                val lessonId    = backStackEntry.arguments!!.getInt("lessonId")
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
                route =  "lesson/complete/{chapterId}/{unitId}/{lessonId}/{chapterName}",
                arguments = listOf(
                    navArgument("chapterId") { type = NavType.IntType },
                    navArgument("unitId") { type = NavType.IntType },
                    navArgument("lessonId") { type = NavType.IntType },
                    navArgument("chapterName") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val chapterId   = backStackEntry.arguments!!.getInt("chapterId")
                val unitId      = backStackEntry.arguments!!.getInt("unitId")
                val lessonId    = backStackEntry.arguments!!.getInt("lessonId")
                val chapterName = backStackEntry.arguments!!.getString("chapterName").orEmpty()

                LessonComplete(
                    navController = innerNavController,
                    chapterId = chapterId,
                    chapterName = chapterName,   // 헤더 표시용
                    unitId = unitId,
                    lessonId = lessonId
                )
            }

            composable("league") { LeagueScreen(innerNavController) }

            composable("user") { UserScreen(innerNavController) }
            composable("user/setting") { Setting(innerNavController) }
            composable("user/addfriend") { AddFriend(innerNavController) }
            composable("user/screensetting") { ScreenSetting(innerNavController) }
            composable("user/notice") { Notice(innerNavController) }
            composable("user/service") { Service(innerNavController) }
            composable("user/tos") { ToS(innerNavController) }
            composable("user/privacypolicy") { PrivacyPolicy(innerNavController) }

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
        }
    }
}
