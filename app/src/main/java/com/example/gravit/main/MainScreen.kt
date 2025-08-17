package com.example.gravit.main

import BottomNavigationBar
import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gravit.main.Home.HomeScreen
import com.example.gravit.main.Chapter.ChapterScreen
import com.example.gravit.main.Chapter.Lesson.LessonScreen
import com.example.gravit.main.League.LeagueScreen
import com.example.gravit.main.Chapter.Unit.Unit
import com.example.gravit.main.User.UserScreen

fun build(chapterId: Int, unitId: Int, lessonId: Int, chapterName: String): String {
    val encodedName = Uri.encode(chapterName) // 한글/공백/특수문자 안전
    return "lesson/$chapterId/$unitId/$lessonId/$encodedName"
}

fun NavController.navigateToLesson(
    chapterId: Int,
    unitId: Int,
    lessonId: Int,
    chapterName: String
) {
    navigate(build(chapterId, unitId, lessonId, chapterName))
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }

            //chapter
            composable("chapter") { ChapterScreen(navController) }

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
                    navController = navController,
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
                    navController = navController,
                    chapterId = chapterId,
                    chapterName = chapterName,   // 헤더 표시용
                    unitId = unitId,
                    lessonId = lessonId
                )
            }

            composable("league") { LeagueScreen() }
            composable("user") { UserScreen() }
        }
    }
}