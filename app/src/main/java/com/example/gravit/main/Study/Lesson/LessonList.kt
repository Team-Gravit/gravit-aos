package com.example.gravit.main.Study.Lesson

import android.annotation.SuppressLint
import android.graphics.BlurMaskFilter
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
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.gravit.ui.theme.Responsive
import com.example.gravit.ui.theme.pretendard
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Study.Problem.CustomSnackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import kotlin.math.abs
import androidx.compose.material.BottomSheetScaffold


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LessonList(
    unitId: Int,
    onSessionExpired: () -> Unit,
    navController: NavController,
    unit: String,
    unitTitle: String
) {
    val context = LocalContext.current
    val vm: LessonListVM = viewModel(factory = LessonListVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load(unitId) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val scope = rememberCoroutineScope()

    val s = (ui as? LessonListVM.UiState.Success)?.data
    val chapterSummary = s?.chapterSummary
    val chapterName = chapterSummary?.title ?: ""
    val lessonSummaries = s?.lessonSummaries ?: emptyList()
    val bookmarkAccessible = s?.bookmarkAccessible ?: false
    val wrongAnsweredNoteAccessible = s?.wrongAnsweredNoteAccessible ?: false

    var snackBar by remember { mutableStateOf<String?>(null) }

    when (ui) {
        LessonListVM.UiState.SessionExpired -> {
            navController.navigate("error/401") {
                launchSingleTop = true; restoreState = false
            }
        }
        LessonListVM.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        LessonListVM.UiState.NotFound -> {
            navController.navigate("error/404") {
                launchSingleTop = true; restoreState = false
            }
        }
        else -> Unit
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetGesturesEnabled = false,
        sheetElevation = 0.dp,
        sheetBackgroundColor = Color.Transparent,
        sheetContent = {
            NoteSheetM2(
                scaffoldState = scaffoldState,
                onDismiss = {
                    scope.launch { scaffoldState.bottomSheetState.collapse() }
                },
                chapter = "자료구조",
                unit = unit,
                title = unitTitle
            )
        },
    ) {
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
                        .height(Responsive.h(70f))
                        .padding(horizontal = Responsive.w(16f)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "back",
                        modifier = Modifier
                            .size(Responsive.w(24f))
                            .clickable { navController.popBackStack() },
                        tint = Color.White
                    )
                    Spacer(Modifier.width(Responsive.w(16f)))
                    Text(
                        text = chapterSummary?.title ?: "",
                        fontWeight = FontWeight.Bold,
                        fontFamily = pretendard,
                        fontSize = Responsive.spW(20f),
                        color = Color.White
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(Responsive.w(92f), Responsive.w(40f))
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
                            onClick = {
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            shape = RoundedCornerShape(Responsive.w(9f)),
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
                                fontSize = Responsive.spW(15f),
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
                Spacer(Modifier.height(Responsive.w(20f)))
                Text(
                    text = "나의 문제",
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = pretendard,
                    fontSize = Responsive.spW(20f),
                    color = Color.White,
                    modifier = Modifier.padding(start = Responsive.w(16f))
                )
                Spacer(Modifier.height(Responsive.w(16f)))
                Selector(
                    unitId = unitId,
                    chapterName = chapterName,
                    navController = navController,
                    bookmarkAccessible = bookmarkAccessible,
                    wrongAnsweredNoteAccessible = wrongAnsweredNoteAccessible,
                    onShowSnackBar = { t -> snackBar = t }
                )
                Spacer(Modifier.height(Responsive.w(20f)))
                Text(
                    text = "문제 리스트",
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = pretendard,
                    fontSize = Responsive.spW(20f),
                    color = Color.White,
                    modifier = Modifier.padding(start = Responsive.w(16f))
                )
                Spacer(Modifier.height(Responsive.w(20f)))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(Responsive.h(8f)),
                    horizontalArrangement = Arrangement.spacedBy(Responsive.w(8f)),
                ) {
                    itemsIndexed(lessonSummaries) { index, lesson ->
                        val columnCount = 3
                        val isLastRow = index >= lessonSummaries.size - columnCount

                        LessonBox(
                            title = if ((index + 1) < 10) "Lesson0${index + 1}" else "Lesson${index + 1}",
                            completed = lesson.isSolved,
                            modifier = Modifier.padding(
                                bottom = if (isLastRow) 20.dp else 0.dp
                            ),
                            totalProblem = lesson.totalProblem,
                            onClick = {
                                navController.navigate(
                                    "lesson/${lesson.lessonId}/${URLEncoder.encode(chapterName)}"
                                )
                            }
                        )
                    }
                }
            }
            if (snackBar != null) {
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
    chapterName: String,
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
                onClick = {navController.navigate("problem/$unitId/${URLEncoder.encode(chapterName)}/${item.type}")},
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
)
{
    Box (modifier = Modifier.size(Responsive.h(229f), Responsive.h(112f))) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(Responsive.w(8f)))
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
                .clickable{
                    if(enabled){
                        onClick()
                    } else{
                        onShowSnackBar(text)
                    }
                }
        ) {
            Column(modifier = Modifier.padding(Responsive.w(8f))) {
                Text(
                    text = title,
                    fontSize = Responsive.spW(14f),
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = pretendard,
                    color = if (selected) Color(0xFF222124) else Color.White
                )
                Text(
                    text = context,
                    fontSize = Responsive.spW(12f),
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    color = if (selected) Color(0xFF222124).copy(0.8f) else Color.White.copy(alpha = 0.8f)
                )
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Responsive.w(18f)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "문제 풀러 가기",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Medium,
                        fontSize = Responsive.spW(12f),
                        color = if (selected) Color(0xFF222124).copy(alpha = 0.8f) else Color.White.copy(
                            alpha = 0.6f
                        )
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.left_line),
                        contentDescription = "to problem",
                        modifier = Modifier.size(Responsive.w(14f)),
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
            .size(Responsive.w(104f), Responsive.w(129f))
            .clip(RoundedCornerShape(Responsive.w(8f)))
            .border(1.dp, Color(0xFF8B69FF), RoundedCornerShape(8.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.15f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                ),
                shape = RoundedCornerShape(Responsive.h(8f))
            )
            .padding(horizontal = Responsive.w(10f), vertical = Responsive.w(10f))
            .clickable{onClick()},
        contentAlignment = Alignment.Center,
    ){
        Column (modifier = Modifier.padding(horizontal = Responsive.w(10f), vertical = Responsive.w(10f))) {
            Text(
                text = title,
                fontSize = Responsive.spW(14f),
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(Responsive.h(4f)))
            Text(
                text = if(completed) "학습 완료" else "학습 전",
                fontSize = Responsive.spW(12f),
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                color = if(completed) Color.White else Color.White.copy(alpha = 0.6f)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${totalProblem}문제",
                fontSize = Responsive.spW(12f),
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