package com.inuappcenter.gravit.main.Study.Lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.inuappcenter.gravit.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.Cip
import com.example.gravit.ui.theme.CipState
import com.example.gravit.ui.theme.PrimitiveColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.api.LessonSummaries
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.UnitSummaryResponse
import com.inuappcenter.gravit.main.Study.Problem.CustomSnackBar
import com.inuappcenter.gravit.main.User.TopBar
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LessonList(
    unitId: Int,
    onSessionExpired: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val vm: LessonListVM = viewModel(factory = LessonListVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load(unitId) }

    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(ui) {
        if (navigated) return@LaunchedEffect

        when (ui) {
            LessonListVM.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            LessonListVM.UiState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            LessonListVM.UiState.Failed -> {
                navigated = true
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }

    when (ui) {
        LessonListVM.UiState.Loading -> {
            LessonListSkeletonUI(navController)
        }

        is LessonListVM.UiState.Success -> {
            val s = (ui as LessonListVM.UiState.Success).data
            val lessonSummaries = s.lessonSummaries
            val bookmarkAccessible = s.bookmarkAccessible
            val wrongAnsweredNoteAccessible = s.wrongAnsweredNoteAccessible
            LessonListUI(
                navController = navController,
                unitId = unitId,
                lessonSummaries = lessonSummaries,
                bookmarkAccessible = bookmarkAccessible,
                wrongAnsweredNoteAccessible = wrongAnsweredNoteAccessible,
                unitSummary = s.unitSummaryResponse
            )
        }
        else -> Unit
    }
}


@Composable
fun LessonListUI(
    navController: NavController,
    unitId: Int,
    lessonSummaries: List<LessonSummaries>,
    bookmarkAccessible: Boolean,
    wrongAnsweredNoteAccessible: Boolean,
    unitSummary: UnitSummaryResponse
){
    var snackBar by remember { mutableStateOf<String?>(null) }
    var sheetState by remember { mutableStateOf(SheetState.Hidden) }

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg0)
        ) {
            TopBar(
                navController = navController,
                title = unitSummary.title,
                useCloseIcon = false,
                useAlarmIcon = true
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.unitlesson_back),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = unitSummary.title,
                        style = AppTypography.Headline2,
                        color = AppColor.text1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = unitSummary.description,
                        style = AppTypography.Label2,
                        color = AppColor.text3,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(67.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimitiveColor.Purple100)
                            .clickable(onClick = { sheetState = SheetState.Half }),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.book),
                                contentDescription = "개념노트",
                                modifier = Modifier.size(23.dp),
                                tint = AppColor.Main1
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "개념노트",
                                    style = AppTypography.Headline1,
                                    color = AppColor.text2
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "개념노트 설명??",
                                    style = AppTypography.Label2,
                                    color = AppColor.text3
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Image(
                                painter = painterResource(R.drawable.chevron_right),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )

                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(156.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable {
                                    if (bookmarkAccessible) {
                                        navController.navigate("problem/$unitId/bookmarks")
                                    } else {
                                        snackBar = "북마크 문제가 없습니다."
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Column {
                                Row {
                                    Text(
                                        text = "북마크",
                                        style = AppTypography.Headline1,
                                        color = AppColor.text2
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Image(
                                        painter = painterResource(R.drawable.chevron_right),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "북마크한 문제를 풀어요.",
                                    style = AppTypography.Label2,
                                    color = AppColor.text3,
                                    maxLines = 2
                                )
                                Spacer(Modifier.weight(1f))
                                Image(
                                    painter = painterResource(R.drawable.bookmark),
                                    contentDescription = "북마크",
                                    modifier = Modifier
                                        .size(37.dp, 40.dp)
                                        .align(Alignment.End)
                                )

                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(156.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable {
                                    if (wrongAnsweredNoteAccessible) {
                                        navController.navigate("problem/$unitId/wrong-answered-notes")
                                    } else {
                                        snackBar = "오답노트 문제가 없습니다."
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Column {
                                Row {
                                    Text(
                                        text = "오답노트",
                                        style = AppTypography.Headline1,
                                        color = AppColor.text2
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Image(
                                        painter = painterResource(R.drawable.chevron_right),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "틀린 문제를 복습해요.",
                                    style = AppTypography.Label2,
                                    color = AppColor.text3,
                                    maxLines = 2
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    painter = painterResource(R.drawable.book),
                                    contentDescription = "오답노트",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.End),
                                    tint = Color(0xFFFFB608)
                                )

                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColor.bg0)
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "문제 리스트",
                                style = AppTypography.Label2,
                                color = AppColor.text4,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            lessonSummaries.forEachIndexed { index, lesson ->
                                val lessonOrderText = "Lesson%02d".format(index + 1)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(59.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            navController.navigate("lesson/${lesson.lessonId}")
                                        }
                                        .background(PrimitiveColor.Gray200),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = lessonOrderText,
                                                style = AppTypography.Label1,
                                                color = AppColor.text2
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = "${lesson.totalProblem}개",
                                                style = AppTypography.Label2,
                                                color = AppColor.text4
                                            )
                                        }
                                        Spacer(Modifier.weight(1f))
                                        Cip(
                                            text = if(lesson.isSolved) "학습 완료" else "잠김",
                                            onClick = {},
                                            state = if(lesson.isSolved) CipState.Default else CipState.Disabled,
                                            modifier = Modifier.height(22.dp),
                                            style = AppTypography.Caption1
                                        )
                                    }

                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
        NoteSheetCustom(
            unitId = unitId,
            sheetState = sheetState,
            onStateChange = { newState ->
                sheetState = newState
            },
            onDismiss = {
                sheetState = SheetState.Hidden
            }
        )

        if (snackBar != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 15.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                CustomSnackBar(text = snackBar!!)
                LaunchedEffect(snackBar) {
                    delay(2000)
                    snackBar = null
                }
            }
        }
    }
}
