package com.example.gravit.main.Study.Lesson

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlin.math.min

@Composable
fun BookWrongScreen(
    navController: NavController,
    chapterName: String,
    unitId: Int,
    onSessionExpired: () -> Unit,
    type: String
){

    //스톱워치
    val swVm: StopwatchViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    var bookmark by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        // 앱이 백그라운드로 가면 멈춤
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                swVm.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)

        // 컴포저블이 사라질 때(네비게이션 이동 등)도 멈춤
        onDispose {
            swVm.pause()
            lifecycleOwner.lifecycle.removeObserver(obs)
        }
    }

    var resultNext by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var submitting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

    LaunchedEffect(unitId) {
        vm.load(unitId = 1, type = type)
        vm.resetProblemSubmit()
    }

    val ui by vm.state.collectAsState()
    val problemSubmit by vm.problemSubmit.collectAsState()

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

    LaunchedEffect(problemSubmit) {
        when (problemSubmit) {
            LessonViewModel.SubmitState.SessionExpired ->  {
                navigated = true
                navController.navigate("error/401") {
                    launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.SubmitState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.SubmitState.Failed -> {
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
    val total = if (declaredTotal > 0) min(declaredTotal, problems.size) else problems.size
    val problemSlots: List<Problems> = remember(problems, total) {
        List(total) { idx -> problems[idx] }
    }

    LaunchedEffect(problems) {
        vm.initBookmarks(problems)
    }

    //제출
    fun submitSingleProblem(problemId: Int, isCorrect: Boolean) {
        vm.submitProblemResults(
            ProblemSubmissionRequests(problemId, isCorrect)
        ) { ok ->

        }
    }

    val bookmarkMap by vm.bookmark.collectAsState()
    val unit = 1
    ProblemUI(
        navController = navController,
        chapterName = chapterName,
        problems = problemSlots,
        total = total,
        swVm = swVm,
        onRecordResult = { id, correct ->
            submitSingleProblem(id, correct)
        },
        bookmarkMap = bookmarkMap,
        onBookmarkToggle = { problemId -> vm.toggleBookmark(problemId) },
        onFinishLesson = {
            navController.navigate("lessonList/$unit") {
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
    )

}