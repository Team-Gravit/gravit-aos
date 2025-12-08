package com.example.gravit.main.Study.Lesson

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    navController: NavController,
    lessonId: Int,
    onSessionExpired: () -> Unit
){
    val swVm: StopwatchViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    var bookmark by remember { mutableStateOf(false) }

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
    val resultsMap = remember { mutableStateMapOf<Int, ProblemSubmissionRequests>() }
    var submitting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

    LaunchedEffect(lessonId) {
        resultsMap.clear()
        vm.load(lessonId = lessonId, type = "normal")
    }

    val ui by vm.state.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(ui) {
        when (ui) {
            LessonViewModel.UiState.SessionExpired ->  {
                navigated = true
                navController.navigate("error/401") {
                    launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.UiState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.UiState.Failed -> {
                navigated = true
                onSessionExpired()
            }
            else -> Unit
        }
    }


    var showLoading by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        showLoading = false
    }
    if (showLoading) {
        LoadingScreen()
        return
    }

    val s = ui as? LessonViewModel.UiState.Success?: return
    val problems = s.data.problems
    val declaredTotal = s.data.totalProblems
    val rawTotal = if (declaredTotal > 0) declaredTotal else problems.size
    val problemSlots: List<Problems> = remember(problems, rawTotal) {
        (0 until rawTotal)
            .mapNotNull { idx -> problems.getOrNull(idx) }
    }
    val total = problemSlots.size

    LaunchedEffect(problems) {
        vm.initBookmarks(problems)
    }

    fun recordResult(problemId: Int, isCorrect: Boolean) {
        resultsMap[problemId] = ProblemSubmissionRequests(
            problemId = problemId,
            isCorrect = isCorrect
        )
    }

    fun finishLesson() {
        swVm.pause()
        if (submitting) return
        submitting = true

        val problemSubmissionRequests = resultsMap.values.toList()
        if (problemSubmissionRequests.isEmpty()) {
            submitting = false
            return
        }
        val learningTime = (swVm.state.value.elapsedMillis / 1000).toInt()
        val correctCount = problemSubmissionRequests.count { it.isCorrect }
        val accuracy: Int = if(total > 0) { ((correctCount.toDouble()/total) * 100).roundToInt() } else 0


        navController.currentBackStackEntry?.savedStateHandle?.set("problemList", problemSubmissionRequests)
        navController.navigate("lesson/complete/${s.data.unitSummary.title}/${accuracy}/${learningTime}/${lessonId}")

    }
    val bookmarkMap by vm.bookmark.collectAsState()

    ProblemUI(
        navController = navController,
        unitTitle = s.data.unitSummary.title,
        problems = problemSlots,
        total = total,
        swVm = swVm,
        bookmarkMap = bookmarkMap,
        onBookmarkToggle = { problemId -> vm.toggleBookmark(problemId) },
        onRecordResult = ::recordResult,
        onFinishLesson = ::finishLesson,
        unitId = s.data.unitSummary.unitId
    )
}