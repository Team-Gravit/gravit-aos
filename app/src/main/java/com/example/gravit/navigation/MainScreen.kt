package com.inuappcenter.gravit.navigation

import com.example.gravit.navigation.BottomNavigationBar
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.error.NotFoundScreen
import com.inuappcenter.gravit.error.UnauthorizedScreen
import com.inuappcenter.gravit.main.Home.HomeScreen
import com.inuappcenter.gravit.main.League.LeagueScreen
import com.inuappcenter.gravit.main.Study.Lesson.BookWrongScreen
import com.inuappcenter.gravit.main.Study.Lesson.LessonComplete
import com.inuappcenter.gravit.main.Study.Lesson.LessonList
import com.inuappcenter.gravit.main.Study.Lesson.LessonScreen
import com.inuappcenter.gravit.main.Study.Unit.UnitList
import com.inuappcenter.gravit.main.User.Friend.AddFriend
import com.inuappcenter.gravit.main.User.Friend.FollowList
import com.inuappcenter.gravit.main.User.Notice.Notice
import com.inuappcenter.gravit.main.User.Notice.NoticeDetail
import com.inuappcenter.gravit.main.User.Setting
import com.inuappcenter.gravit.main.User.Setting.Account
import com.inuappcenter.gravit.main.User.Setting.DeletionComplete
import com.inuappcenter.gravit.main.User.Setting.DeletionGuard
import com.inuappcenter.gravit.main.User.Setting.PrivacyPolicy
import com.inuappcenter.gravit.ui.theme.statusBarStyleForMainRoute
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.navigation
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Ai.Home.AiHomePage
import com.inuappcenter.gravit.main.Ai.Home.FeedBackReport
import com.inuappcenter.gravit.main.Ai.Home.RecentAllSessions
import com.inuappcenter.gravit.main.Ai.Interview.AiAnswerCheckPage
import com.inuappcenter.gravit.main.Ai.Interview.AiInterviewPage
import com.inuappcenter.gravit.main.Ai.Interview.AiInterviewVM
import com.inuappcenter.gravit.main.Ai.Interview.AiInterviewVMFactory
import com.inuappcenter.gravit.main.Ai.Selected.AiSelectedPage
import com.inuappcenter.gravit.main.Ai.Selected.AnswerMode
import com.inuappcenter.gravit.main.Study.Chapter.Learning
import com.inuappcenter.gravit.main.Study.Chapter.LearningTab
import com.inuappcenter.gravit.main.Study.Lesson.NotePage
import com.inuappcenter.gravit.main.User.Inquiry.Inquiry
import com.inuappcenter.gravit.main.User.MyPage
import com.inuappcenter.gravit.main.User.Notice.Notification


enum class FollowTab { Followers, Following }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(rootNavController: NavController) {
    val context = LocalContext.current
    val innerNavController = rememberNavController()
    val backStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    val hideBottomBar = currentRoute.startsWith("lesson/") ||
                        currentRoute.startsWith("problem/") ||
                        currentRoute.startsWith("user/account") ||
                        currentRoute.startsWith("user/privacypolicy") ||
                        currentRoute.startsWith("error/") ||
                        currentRoute.startsWith("user/deletion-complete") ||
                        currentRoute.startsWith("inquiry") ||
                        currentRoute.startsWith("user/notification") ||
                        currentRoute.startsWith("user/setting") ||
                        currentRoute.startsWith("user/notice") ||
                        currentRoute.startsWith("cs-note") ||
                        currentRoute.startsWith("ai")

    val goToLoginChoice: () -> Unit = {
        rootNavController.navigate("login choice") {
            popUpTo(0) { inclusive = true }   // 백스택 싹 비우기
            launchSingleTop = true
            restoreState = false
        }
    }

    val systemUiController = rememberSystemUiController()
    val (statusBarColor, darkIcons) =
        statusBarStyleForMainRoute(currentRoute)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = darkIcons
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DeletionGuard(navController = innerNavController) {
            NavHost(
                navController = innerNavController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    BottomBarScreen {
                        HomeScreen(innerNavController)
                    }
                }
                //chapter
                composable(
                    route = "chapter?tab={tab}",
                    arguments = listOf(
                        navArgument("tab") {
                            type = NavType.StringType
                            defaultValue = "chapter"
                        }
                    )
                ) { backStackEntry ->

                    val initialTab = when (
                        backStackEntry.arguments?.getString("tab")
                    ) {
                        "ai" -> LearningTab.AI
                        else -> LearningTab.Chapter
                    }

                    BottomBarScreen {
                        Learning(
                            navController = innerNavController,
                            initialTab = initialTab
                        )
                    }
                }

                composable(
                    route = "unit/{chapterId}",
                    arguments = listOf(
                        navArgument("chapterId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val chapterId = backStackEntry.arguments!!.getLong("chapterId")
                    BottomBarScreen {
                        UnitList(
                            chapterId = chapterId,
                            navController = innerNavController,
                            onSessionExpired = goToLoginChoice
                        )
                    }
                }

                composable(
                    route = "lessonList/{unitId}",
                    arguments = listOf(navArgument("unitId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val unitId = backStackEntry.arguments!!.getLong("unitId")

                    BottomBarScreen{
                        LessonList(
                            navController = innerNavController,
                            onSessionExpired = goToLoginChoice,
                            unitId = unitId
                        )
                    }
                }

                composable(
                    route = "cs-note/{unitId}/{title}",
                    arguments = listOf(
                        navArgument("unitId") { type = NavType.LongType },
                        navArgument("title") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val unitId = backStackEntry.arguments!!.getLong("unitId")
                    val title = backStackEntry.arguments!!.getString("title").orEmpty()

                    NotePage(
                        navController = innerNavController,
                        unitId = unitId,
                        title = title
                    )
                }

                composable( //이거 문제집 네비
                    route = "lesson/{lessonId}/{chapterId}",
                    arguments = listOf(
                        navArgument("lessonId") { type = NavType.LongType },
                        navArgument("chapterId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val lessonId = backStackEntry.arguments!!.getLong("lessonId")
                    val chapterId = backStackEntry.arguments!!.getLong("chapterId")

                    LessonScreen(
                        navController = innerNavController,
                        lessonId = lessonId,
                        onSessionExpired = goToLoginChoice,
                        chapterId = chapterId
                    )
                }

                composable(
                    route = "problem/{unitId}/{type}",
                    arguments = listOf(
                        navArgument("unitId") { type = NavType.LongType },
                        navArgument("type") { type = NavType.StringType; defaultValue = "" },
                    )
                ) {  backStackEntry ->
                    val unitId = backStackEntry.arguments!!.getLong("unitId")
                    val type = backStackEntry.arguments!!.getString("type").orEmpty()

                    BookWrongScreen(
                        navController = innerNavController,
                        unitId = unitId,
                        onSessionExpired = goToLoginChoice,
                        type = type
                    )
                }

                composable(
                    route = "lesson/complete/{accuracy}/{learningTime}/{lessonId}/{chapterId}",
                    arguments = listOf(
                        navArgument("accuracy") { type = NavType.IntType },
                        navArgument("learningTime") { type = NavType.IntType },
                        navArgument("lessonId") { type = NavType.LongType },
                        navArgument("chapterId") { type = NavType.LongType },
                    )
                ) { backStackEntry ->
                    val accuracy = backStackEntry.arguments!!.getInt("accuracy")
                    val learningTime = backStackEntry.arguments!!.getInt("learningTime")
                    val lessonId = backStackEntry.arguments!!.getLong("lessonId")
                    val chapterId = backStackEntry.arguments!!.getLong("chapterId")

                    LessonComplete(
                        navController = innerNavController,
                        accuracy = accuracy,
                        learningTime = learningTime,
                        lessonId = lessonId,
                        chapterId = chapterId
                    )
                }

                composable("league") { BottomBarScreen { LeagueScreen(innerNavController, goToLoginChoice) } }

                composable("user") { BottomBarScreen { MyPage(innerNavController, goToLoginChoice) } }

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

                composable("inquiry") { Inquiry(innerNavController, goToLoginChoice) }

                // notice
                composable("user/notice") { Notice(innerNavController) }
                composable("user/notification") { Notification(innerNavController) }
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

                    BottomBarScreen {
                        FollowList(
                            navController = innerNavController,
                            initialTab = initialTab
                        )
                    }
                }
                composable("error/401") { UnauthorizedScreen(navController = innerNavController, onSessionExpired = goToLoginChoice) }
                composable("error/404") { NotFoundScreen(navController = innerNavController) }

                //ai
                composable("ai/home") { AiHomePage(navController = innerNavController)}
                composable("ai/home/recentAllSessions") { RecentAllSessions(navController = innerNavController) }
                composable(
                    route = "ai/feedback/{sessionId}",
                    arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val sessionId = backStackEntry.arguments!!.getLong("sessionId")
                    FeedBackReport(
                        navController = innerNavController,
                        sessionId = sessionId
                    )
                }
                navigation(
                    startDestination = "ai/selected",
                    route = "ai/interview-flow"
                ) {
                    composable("ai/selected") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            innerNavController.getBackStackEntry("ai/interview-flow")
                        }

                        val interviewVm: AiInterviewVM = viewModel(
                            parentEntry,
                            factory = AiInterviewVMFactory(
                                api = RetrofitInstance.api,
                                context = context
                            )
                        )

                        AiSelectedPage(
                            navController = innerNavController,
                            interviewVm = interviewVm
                        )
                    }

                    composable("ai/interview/{answerMode}") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            innerNavController.getBackStackEntry("ai/interview-flow")
                        }

                        val interviewVm: AiInterviewVM = viewModel(
                            parentEntry,
                            factory = AiInterviewVMFactory(
                                api = RetrofitInstance.api,
                                context = context
                            )
                        )

                        val answerMode = backStackEntry.arguments
                            ?.getString("answerMode")
                            ?.let {
                                runCatching {
                                    AnswerMode.valueOf(it)
                                }.getOrNull()
                            }
                            ?: AnswerMode.Voice

                        AiInterviewPage(
                            navController = innerNavController,
                            mode = answerMode,
                            vm = interviewVm
                        )
                    }
                    composable("ai/answer-check/{answerMode}") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            innerNavController.getBackStackEntry("ai/interview-flow")
                        }

                        val interviewVm: AiInterviewVM = viewModel(
                            parentEntry,
                            factory = AiInterviewVMFactory(
                                api = RetrofitInstance.api,
                                context = context
                            )
                        )

                        val answerMode = backStackEntry.arguments
                            ?.getString("answerMode")
                            ?.let {
                                runCatching {
                                    AnswerMode.valueOf(it)
                                }.getOrNull()
                            }
                            ?: AnswerMode.Voice

                        AiAnswerCheckPage(
                            navController = innerNavController,
                            mode = answerMode,
                            vm = interviewVm
                        )
                    }
                }

            }
        }

        if (!hideBottomBar) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                BottomNavigationBar(innerNavController)
            }
        }
    }
}

@Composable
fun BottomBarScreen(
    showBottomBar: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(
                bottom = if (showBottomBar) 76.dp else 0.dp
            )
            .background(Color.Transparent)
    ) {
        content()
    }
}