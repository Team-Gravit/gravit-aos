package com.example.gravit.main.Study.Lesson

import android.annotation.SuppressLint
import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.gravit.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.ui.theme.pretendard
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.api.ChapterSummary
import com.example.gravit.api.LessonSummaries
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Study.Problem.CustomSnackBar
import kotlinx.coroutines.delay
import kotlin.math.abs


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LessonList(
    unitId: Int,
    onSessionExpired: () -> Unit,
    navController: NavController,
    unitTitle: String
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
                    launchSingleTop = true; restoreState = false
                }
            }
            LessonListVM.UiState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    launchSingleTop = true; restoreState = false
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
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is LessonListVM.UiState.Success -> {
            val s = (ui as LessonListVM.UiState.Success).data
            val chapterSummary = s.chapterSummary
            val chapterId = chapterSummary.chapterId
            val lessonSummaries = s.lessonSummaries
            val bookmarkAccessible = s.bookmarkAccessible
            val wrongAnsweredNoteAccessible = s.wrongAnsweredNoteAccessible
            LessonListUI(
                navController = navController,
                chapterId = chapterId,
                chapterSummary = chapterSummary,
                unitId = unitId,
                lessonSummaries = lessonSummaries,
                bookmarkAccessible = bookmarkAccessible,
                wrongAnsweredNoteAccessible = wrongAnsweredNoteAccessible,
                unitTitle = unitTitle

            )
        }
        else -> Unit
    }
}


@Composable
fun LessonListUI(
    navController: NavController,
    chapterId: Int,
    chapterSummary: ChapterSummary,
    unitId: Int,
    lessonSummaries: List<LessonSummaries>,
    bookmarkAccessible: Boolean,
    wrongAnsweredNoteAccessible: Boolean,
    unitTitle: String
){
    var snackBar by remember { mutableStateOf<String?>(null) }
    var sheetState by remember { mutableStateOf(SheetState.Hidden) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.unitlesson_back),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.navigate("unit/$chapterId") },
                    tint = Color.White
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = chapterSummary.title,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pretendard,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(92.dp, 40.dp)
                        .glow(
                            color = Color(0xFFC52AFF),
                            radius = 28.dp,
                            cornerRadius = 9.dp
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(9.dp)
                        )
                ) {
                    Button(
                        onClick = { sheetState = SheetState.Half },
                        shape = RoundedCornerShape(9.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC52AFF),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "개념노트",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.25f),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 1f
                                )
                            )
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "나의 문제",
                fontWeight = FontWeight.SemiBold,
                fontFamily = pretendard,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            Selector(
                unitId = unitId,
                navController = navController,
                bookmarkAccessible = bookmarkAccessible,
                wrongAnsweredNoteAccessible = wrongAnsweredNoteAccessible,
                onShowSnackBar = { t -> snackBar = t }
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "문제 리스트",
                fontWeight = FontWeight.SemiBold,
                fontFamily = pretendard,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(Modifier.height(20.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(lessonSummaries) { index, lesson ->
                    val columnCount = 3
                    val isLastRow = index >= lessonSummaries.size - columnCount

                    LessonBox(
                        title = if ((index + 1) < 10) "Lesson0${index + 1}" else "Lesson${index + 1}",
                        completed = lesson.isSolved,
                        modifier = Modifier.padding(bottom = if (isLastRow) 20.dp else 0.dp),
                        totalProblem = lesson.totalProblem,
                        onClick = {
                            navController.navigate(
                                "lesson/${lesson.lessonId}"
                            )
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = sheetState != SheetState.Hidden,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NoteSheetCustom(
                unitId = unitId,
                title = unitTitle,
                sheetState = sheetState,
                onStateChange = { newState -> sheetState = newState },
                onDismiss = { sheetState = SheetState.Hidden }
            )
        }

        if (snackBar != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 19.dp),
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

data class SelectorItem(
    val title: String,
    val desc: String,
    val type: String,
    val enabled: Boolean,
    val snackbar: String
)

@Composable
fun Selector(
    unitId: Int,
    navController: NavController,
    bookmarkAccessible: Boolean,
    wrongAnsweredNoteAccessible: Boolean,
    onShowSnackBar: (String) -> Unit
){
    val items = listOf(
        SelectorItem(
            title = "북마크",
            desc = "즐겨찾기로 등록해놓은 문제를 풀어요.",
            type = "bookmarks",
            enabled = bookmarkAccessible,
            snackbar = "북마크 문제가 없습니다."
        ),
        SelectorItem(
            title = "오답노트",
            desc = "틀린 문제를 복습해요.",
            type = "wrong-answered-notes",
            enabled = wrongAnsweredNoteAccessible,
            snackbar = "오답노트 문제가 없습니다."
        )
    )

    val listState = rememberLazyListState()
    val fling = rememberSnapFlingBehavior(listState)

    val centerIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val vpCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2
            info.visibleItemsInfo.minByOrNull { item ->
                val center = item.offset + item.size / 2
                abs(center - vpCenter)
            }?.index ?: 0
        }
    }
    var userHasScrolled by remember { mutableStateOf(false) }
    var lastAppliedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(0, items.size) {
        if (0 in items.indices) {
            listState.scrollToItem(0)
            lastAppliedIndex = 0
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { moving ->
            if (moving) {
                userHasScrolled = true
            } else if (userHasScrolled && centerIndex != lastAppliedIndex) {
                lastAppliedIndex = centerIndex
            }
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp),
        state = listState,
        flingBehavior = fling,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        itemsIndexed(items) { index, item ->
            val selected = index == centerIndex
            ProblemBox(
                title = item.title,
                context = item.desc,
                selected = selected,
                onClick = {
                    navController.navigate(
                        "problem/$unitId/${item.type}"
                    )
                },
                enabled = item.enabled,
                text = item.snackbar,
                onShowSnackBar = onShowSnackBar
            )
        }
    }
}

@Composable
fun ProblemBox(
    title: String,
    context: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    text: String,
    onShowSnackBar: (String) -> Unit,
) {
    Box(modifier = Modifier.size(229.dp, 112.dp)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = if (selected) Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            Color.White
                        )
                    )
                    else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF240031).copy(alpha = 0.8f),
                                Color(0xFF240031).copy(alpha = 0.2f)
                            )
                        )
                    }
                )
                .border(1.dp, Color(0xFF6D6D6D), RoundedCornerShape(8.dp))
                .padding(10.dp)
                .clickable {
                    if (enabled) {
                        if (selected) {
                            onClick()
                        } else {
                        }
                    } else {
                        onShowSnackBar(text)
                    }
                }
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = pretendard,
                    color = if (selected) Color(0xFF222124) else Color.White
                )
                Text(
                    text = context,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    color = if (selected) Color(0xFF222124).copy(0.8f) else Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "문제 풀러 가기",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (selected) Color(0xFF222124).copy(alpha = 0.8f) else Color.White.copy(
                            alpha = 0.6f
                        )
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.left_line),
                        contentDescription = "to problem",
                        modifier = Modifier.size(14.dp),
                        tint = if (selected) Color(0xFF222124).copy(alpha = 0.6f) else Color.White.copy(
                            alpha = 0.6f
                        )
                    )
                }
            }
        }
        if (selected) {
            Image(
                painter = painterResource(R.drawable.bokmark),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = -4.dp)
            )
        }
    }
}


@Composable
fun LessonBox(
    title: String,
    completed: Boolean,
    modifier: Modifier,
    totalProblem: Int,
    onClick: () -> Unit
){
    Box(
        modifier = modifier
            .size(104.dp, 129.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF8B69FF), RoundedCornerShape(8.dp))
            .clickable{onClick()},
        contentAlignment = Alignment.Center,
    ){
        Image(
            painter = painterResource(id = R.drawable.glass),
            contentDescription = "back",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        Column (modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if(completed) "학습 완료" else "학습 전",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                color = if(completed) Color.White else Color.White.copy(alpha = 0.6f)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${totalProblem}문제",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                color = Color.White
            )
        }
    }
}

@SuppressLint("SuspiciousModifierThen")
fun Modifier.glow(
    color: Color,
    radius: Dp = 24.dp,
    cornerRadius: Dp = 18.dp
): Modifier = this.then(
    drawBehind {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()

        frameworkPaint.color = color.toArgb()
        frameworkPaint.maskFilter =
            BlurMaskFilter(radius.toPx(), BlurMaskFilter.Blur.NORMAL)

        drawIntoCanvas { canvas ->
            canvas.drawRoundRect(
                0f,
                0f,
                size.width,
                size.height,
                cornerRadius.toPx(),
                cornerRadius.toPx(),
                paint
            )
        }
    }
)
