package com.example.gravit.main.Study.Lesson

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.api.ProblemSubmissionRequests
import com.example.gravit.api.Problems
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Study.Problem.LessonVMFactory
import com.example.gravit.main.Study.Problem.LessonViewModel
import com.example.gravit.main.Study.Problem.LoadingScreen
import com.example.gravit.main.Study.Problem.ProblemUI
import com.example.gravit.main.Study.Problem.StopwatchViewModel
import kotlinx.coroutines.delay

@Composable
fun BookWrongScreen(
    navController: NavController,
    unitId: Int,
    onSessionExpired: () -> Unit,
    type: String
){
    val swVm: StopwatchViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current


    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                swVm.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)

        onDispose {
            swVm.pause()
            lifecycleOwner.lifecycle.removeObserver(obs)
        }
    }

    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

    LaunchedEffect(unitId) {
        vm.load(unitId = unitId, type = type)
        vm.resetProblemSubmit()
    }

    val ui by vm.state.collectAsState()
    val problemSubmit by vm.problemSubmit.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    val navTarget: String? = when {
        ui is LessonViewModel.UiState.SessionExpired ||
                problemSubmit is LessonViewModel.ProblemSubmitState.SessionExpired -> "401"

        ui is LessonViewModel.UiState.NotFound ||
                problemSubmit is LessonViewModel.ProblemSubmitState.NotFound -> "404"

        ui is LessonViewModel.UiState.Failed ||
                problemSubmit is LessonViewModel.ProblemSubmitState.Failed -> "FAILED"

        else -> null
    }

    LaunchedEffect(navTarget) {
        if (navTarget == null || navigated) return@LaunchedEffect
        navigated = true
        when (navTarget) {
            "401" -> {
                navController.navigate("error/401") {
                    launchSingleTop = true
                    restoreState = false
                }
            }
            "404" -> {
                navController.navigate("error/404") {
                    launchSingleTop = true
                    restoreState = false
                }
            }
            "FAILED" -> {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    when (ui) {
        LessonViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is LessonViewModel.UiState.Success -> {
            val s = (ui as LessonViewModel.UiState.Success).data
            val problems = s.problems
            val declaredTotal = s.totalProblems
            val rawTotal = if (declaredTotal > 0) declaredTotal else problems.size
            val problemSlots: List<Problems> = remember(problems, rawTotal) {
                (0 until rawTotal)
                    .mapNotNull { idx -> problems.getOrNull(idx) }
            }
            val total = problemSlots.size

            LaunchedEffect(problems) {
                vm.initBookmarks(problems)
            }

            fun submitSingleProblem(problemId: Int, isCorrect: Boolean) {
                vm.submitProblemResults(
                    ProblemSubmissionRequests(problemId, isCorrect)
                ) { ok ->

                }
            }

            val bookmarkMap by vm.bookmark.collectAsState()
            val unitTitle = s.unitSummary.title

            ProblemUI(
                navController = navController,
                unitTitle = s.unitSummary.title,
                problems = problemSlots,
                total = total,
                swVm = swVm,
                onRecordResult = { id, correct ->
                    submitSingleProblem(id, correct)
                },
                bookmarkMap = bookmarkMap,
                onBookmarkToggle = { problemId -> vm.toggleBookmark(problemId) },
                onFinishLesson = {
                    navController.navigate("lessonList/$unitId/$unitTitle") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                type = type,
                onRemoveWrongNote = { problemId ->
                    if (type == "wrong-answered-notes") {
                        vm.removeWrongAnswered(problemId) { ok ->
                        }
                    }
                },
                unitId = unitId
            )

        }
        else -> Unit
    }

    var showLoading by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        showLoading = false
    }
    if (showLoading) {
        LoadingScreen()
    }
}