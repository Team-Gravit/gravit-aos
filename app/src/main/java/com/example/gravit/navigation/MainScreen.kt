package com.example.gravit.navigation

import BottomNavigationBar
import android.R.attr.type
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
import com.example.gravit.main.Study.Chapter.ChapterScreen
import com.example.gravit.main.Study.Lesson.LessonComplete
import com.example.gravit.main.League.LeagueScreen
import com.example.gravit.main.Study.Lesson.BookWrongScreen
import com.example.gravit.main.Study.Lesson.LessonList
import com.example.gravit.main.Study.Lesson.LessonScreen
import com.example.gravit.main.Study.Unit.UnitList
import com.example.gravit.main.User.Friend.AddFriend
import com.example.gravit.main.User.Friend.FollowList
import com.example.gravit.main.User.Notice.Notice
import com.example.gravit.main.User.Notice.NoticeDetail
import com.example.gravit.main.User.Setting
import com.example.gravit.main.User.Setting.Account
import com.example.gravit.main.User.Setting.DeletionComplete
import com.example.gravit.main.User.Setting.DeletionGuard
import com.example.gravit.main.User.Setting.PrivacyPolicy
import com.example.gravit.main.User.UserScreen

enum class FollowTab { Followers, Following }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(rootNavController: NavController) {
    val innerNavController = rememberNavController()
    val backStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    val hideBottomBar = currentRoute.startsWith("lesson/") ||
                        currentRoute.startsWith("problem/") ||
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
                    route = "unit/{chapterId}",
                    arguments = listOf(
                        navArgument("chapterId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val chapterId = backStackEntry.arguments!!.getInt("chapterId")

                    UnitList(
                        chapterId = chapterId,
                        navController = innerNavController,
                        onSessionExpired = goToLoginChoice
                    )
                }

                composable(
                    route = "lessonList/{unitId}/{unitTitle}",
                    arguments = listOf(
                        navArgument("unitId") { type = NavType.IntType },
                        navArgument("unitTitle") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val unitId = backStackEntry.arguments!!.getInt("unitId")
                    val unitTitle = backStackEntry.arguments!!.getString("unitTitle").orEmpty()
                    LessonList(
                        navController = innerNavController,
                        onSessionExpired = goToLoginChoice,
                        unitId = unitId,
                        unitTitle = unitTitle
                    )
                }

                composable( //이거 이제 문제집 네비
                    route = "lesson/{lessonId}",
                    arguments = listOf(
                        navArgument("lessonId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val lessonId = backStackEntry.arguments!!.getInt("lessonId")

                    LessonScreen(
                        navController = innerNavController,
                        lessonId = lessonId,
                        onSessionExpired = goToLoginChoice
                    )
                }

                composable(
                    route = "problem/{unitId}/{type}",
                    arguments = listOf(
                        navArgument("unitId") { type = NavType.IntType },
                        navArgument("type") { type = NavType.StringType; defaultValue = "" },
                    )
                ) {  backStackEntry ->
                    val unitId = backStackEntry.arguments!!.getInt("unitId")
                    val type = backStackEntry.arguments!!.getString("type").orEmpty()

                    BookWrongScreen(
                        navController = innerNavController,
                        unitId = unitId,
                        onSessionExpired = goToLoginChoice,
                        type = type
                    )
                }

                composable(
                    route = "lesson/complete/{accuracy}/{learningTime}/{lessonId}",
                    arguments = listOf(
                        navArgument("accuracy") { type = NavType.FloatType },
                        navArgument("learningTime") { type = NavType.IntType },
                        navArgument("lessonId") { type = NavType.IntType },
                    )
                ) { backStackEntry ->
                    val accuracy = backStackEntry.arguments!!.getFloat("accuracy")
                    val learningTime = backStackEntry.arguments!!.getInt("learningTime")
                    val lessonId = backStackEntry.arguments!!.getInt("lessonId")

                    LessonComplete(
                        navController = innerNavController,
                        accuracy = accuracy,
                        learningTime = learningTime,
                        lessonId = lessonId
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

                composable("user/addfriend") {
                    AddFriend(innerNavController)
                }

                composable(
                    route = "user/followList?tab={tab}",
                    arguments = listOf(
                        navArgument("tab") {
                            type = NavType.StringType
                            defaultValue = "followers"
                        }
                    )
                ) { backStackEntry ->
                    val tabArg = backStackEntry.arguments?.getString("tab") ?: "followers"

                    val initialTab = if (tabArg.equals("following", true)) {
                        FollowTab.Following
                    } else {
                        FollowTab.Followers
                    }

                    FollowList(
                        navController = innerNavController,
                        initialTab = initialTab
                    )
                }


                composable("error/401") { UnauthorizedScreen(navController = innerNavController, onSessionExpired = goToLoginChoice) }
                composable("error/404") { NotFoundScreen(navController = innerNavController) }
            }
        }
    }
}
