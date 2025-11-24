package com.example.gravit.main.Chapter.Lesson

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.Responsive
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.math.abs

@Composable
fun LessonScreen(
    chapterId: Int,
    onSessionExpired: () -> Unit,
    navController: NavController
) {
    var showSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.unitlesson_back),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Responsive.h(70f))
                    .padding(horizontal = Responsive.w(16f)),
                verticalAlignment = Alignment.CenterVertically
            ){
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
                    text = "자료구조",
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
                        onClick = { showSheet = true},
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
            Selector()
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
                itemsIndexed(List(12) { it }) { index, _ ->

                    val isLastRow = index >= 12 - 3   // 총 12개, 마지막 3개가 마지막 행

                    LessonBox(
                        title = "Lesson01",
                        completed = false,
                        modifier = Modifier.padding(
                            bottom = if (isLastRow) 20.dp else 0.dp
                        )
                    )
                }
            }
        }
        if(showSheet){
            NoteSheet(
                onDismiss = { showSheet = false }
            )
        }
    }

}

@Composable
fun Selector(
){
    val items = listOf(
        "북마크" to "즐겨찾기로 등록해놓은 문제를 풀어요.",
        "오답노트" to "틀린 문제를 복습해요.",
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
        itemsIndexed(items) { index, (title, desc) ->
            val selected = index == centerIndex
            ProblemBox(
                title = title,
                context = desc,
                selected = selected,
            )
        }
    }
}

@Composable
fun ProblemBox(
    title: String,
    context: String,
    selected: Boolean,
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
                    .offset(x = (-12).dp, y = -4.dp)   // 모서리에서 살짝 안쪽으로
            )
        }
    }
}
@Composable
fun LessonBox(
    title: String,
    completed: Boolean,
    modifier: Modifier
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
            .padding(horizontal = Responsive.w(10f), vertical = Responsive.w(10f)),
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
                text = "10문제",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSheet(
    onDismiss: () -> Unit,
){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        val isExpanded = sheetState.currentValue == SheetValue.Expanded

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = LocalConfiguration.current.screenHeightDp.dp * 0.9f)
        ) {
            Text(
                text = "개념노트",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = pretendard,
                color = Color(0xFF222124),
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "배열(Array)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                color = Color(0xFF6D6D6D),
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(
                color = Color(0xFFA8A8A8),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .let {
                        if (isExpanded) {
                            // 확장 모드 → 본문 높이를 늘려서 시트에 맞게 전체 표시됨
                            it
                                .fillMaxHeight(0.8f)
                                .verticalScroll(rememberScrollState())
                        } else {
                            // 부분 모드 → 고정된 작은 height
                            it
                                .height(252.dp)
                                .verticalScroll(rememberScrollState())
                        }
                    }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                // 모든 드래그 제스처 소비해서 bottom sheet 로 전달 안 함
                                awaitPointerEvent(PointerEventPass.Initial)
                            }
                        }
                    }
            ) {
                Text(
                    text = "줄을 서요. 먼저 온 사람이 먼저 들어가요.\n버스를 기다릴 때도, 식당 대기표를 뽑을 때도,\n은행 창구 앞에서도 우리는 줄을 서죠.\n그리고 먼저 온 사람은 먼저 처리되고,\n나중에 온 사람은 그 뒤를 따르게 됩니다.\n이런 구조는 자료구조에서도 그대로 쓰여요.\n이 구조에서는 데이터를 넣으면 맨 뒤로 가고,\n꺼낼 땐 맨 앞에서부터 꺼내야 해요.\n그러니까 가장 먼저 들어온 게 가장 먼저 나가죠.\n이걸 우리는 _______구조라고 부릅니다.\n" +
                            "\n" +
                            "스크롤로 내용 계속 쓰시면 됩니다.아ㅣㄴ어ㅏ너ㅏ머ㅏ어나렅츠쿠챀ㅇ라뫙\n \n \n \n \n \n \n \n\n\n\niosdujkfhjdkfhjskhfkjhekjh",
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    fontSize = 14.sp,
                    color = Color(0xFF383838),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Spacer(Modifier.height(37.dp))

        }
    }
}

@Preview
@Composable
fun Preview(){
    val navController = rememberNavController()
    LessonScreen(1, {}, navController)
}