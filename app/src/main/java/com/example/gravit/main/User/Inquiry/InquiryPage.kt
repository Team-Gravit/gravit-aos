package com.inuappcenter.gravit.main.User.Inquiry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.inuappcenter.gravit.main.User.TapButton
import com.inuappcenter.gravit.main.User.TopBar
import androidx.compose.ui.unit.Velocity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gravit.ui.theme.Cip
import com.example.gravit.ui.theme.CipState
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.InquiryListResponses
import com.inuappcenter.gravit.api.InquiryResponses
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Study.Problem.CustomSnackBar
import kotlinx.coroutines.delay

enum class InquiryTab {
    Support,
    Check
}
@Composable
fun Inquiry(
    navController: NavController,
    onSessionExpired: () -> Unit
){
    val context = LocalContext.current
    val vm: InquiryVM = viewModel(factory = InquiryVMFactory(RetrofitInstance.api, context))
    val loadUi by vm.loadState.collectAsState()
    val submitUi by vm.submitState.collectAsState()
    val detailStates by vm.inquiryDetailStates.collectAsState()
    var expandedInquiryIds by remember { mutableStateOf(setOf<Long>()) }

    var navigated by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    var selectedTab by remember { mutableStateOf(InquiryTab.Support) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    
    var showSnackBar by remember { mutableStateOf(false) }
    var snackBarText by remember { mutableStateOf("") }

    val isLoading =
        loadUi == InquiryVM.LoadUiState.Loading ||
                submitUi == InquiryVM.UiState.Loading ||
                detailStates.values.any {
                    it == InquiryVM.InquiryDetailUiState.Loading
                }
    var resetSupportForm by remember { mutableStateOf(false) }

    LaunchedEffect(submitUi) {
        if (navigated) return@LaunchedEffect

        when (submitUi) {
            InquiryVM.UiState.Success -> {
                snackBarText = "문의가 등록되었습니다."
                showSnackBar = true
                dropdownExpanded = false
                resetSupportForm = true
                vm.resetSubmitState()
            }
            InquiryVM.UiState.Failed -> {
                snackBarText = "오류가 발생했습니다."
                showSnackBar = true
                vm.resetSubmitState()
            }
            InquiryVM.UiState.SessionExpired -> {
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
            InquiryVM.UiState.NotFound -> {
                navigated = true
                navController.navigate("error/404"){
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }

    LaunchedEffect(selectedTab) {
        dropdownExpanded = false
        vm.resetSubmitState()

        if (selectedTab == InquiryTab.Check) {
            vm.loadInquiryList()
        }
    }
    LaunchedEffect(loadUi) {
        if (navigated) return@LaunchedEffect

        when (loadUi) {
            InquiryVM.LoadUiState.Failed -> {
                snackBarText = "오류가 발생했습니다."
                showSnackBar = true
            }
            InquiryVM.LoadUiState.SessionExpired -> {
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
            InquiryVM.LoadUiState.NotFound -> {
                navigated = true
                navController.navigate("error/404"){
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }
    LaunchedEffect(detailStates) {
        if (navigated) return@LaunchedEffect

        when (detailStates) {
            InquiryVM.InquiryDetailUiState.Failed -> {
                snackBarText = "오류가 발생했습니다."
                showSnackBar = true
            }
            InquiryVM.InquiryDetailUiState.SessionExpired -> {
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
            InquiryVM.InquiryDetailUiState.NotFound -> {
                navigated = true
                navController.navigate("error/404"){
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }

    Box (
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg0)
        ){
            TopBar(
                navController = navController,
                title = "문의하기",
                useCloseIcon = false,
                height = 48.dp
            )
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColor.bg2)
                    .padding(horizontal = 16.dp),
            ) {
                item {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColor.bg1)
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TapButton(
                            text = "문의하기",
                            selected = selectedTab == InquiryTab.Support,
                            onClick = {
                                selectedTab = InquiryTab.Support
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .weight(1f)
                        )
                        TapButton(
                            text = "문의내역확인",
                            selected = selectedTab == InquiryTab.Check,
                            onClick = {
                                selectedTab = InquiryTab.Check
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
                when (selectedTab) {
                    InquiryTab.Support -> {
                        item {
                            SupportUI(
                                expanded = dropdownExpanded,
                                onExpandedChange = {
                                    dropdownExpanded = it
                                },
                                onSubmit = { title, typeCode, content ->
                                    vm.submit(title, typeCode, content)
                                },
                                resetForm = resetSupportForm,
                                onResetFormDone = {
                                    resetSupportForm = false
                                },
                            )
                        }
                    }

                    InquiryTab.Check -> {
                        when (val state = loadUi) {
                            is InquiryVM.LoadUiState.Success -> {
                                item {
                                    CheckUI(
                                        inquiry = state.inquiryList,
                                        expandedInquiryIds = expandedInquiryIds,
                                        detailStates = detailStates,
                                        onInquiryClick = { inquiryId ->
                                            if (inquiryId in expandedInquiryIds) {
                                                expandedInquiryIds =
                                                    expandedInquiryIds - inquiryId
                                            } else {
                                                expandedInquiryIds =
                                                    expandedInquiryIds + inquiryId

                                                vm.loadInquiryDetail(inquiryId)
                                            }
                                        },
                                        onPageClick = { page ->
                                            expandedInquiryIds = emptySet()
                                            vm.loadInquiryList(page)
                                        },
                                        onSupportClick = {
                                            selectedTab = InquiryTab.Support
                                        }
                                    )
                                }
                            }

                            else -> Unit
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        if (showSnackBar) {
            CustomSnackBar(
                text = snackBarText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            )

            LaunchedEffect(snackBarText) {
                delay(2000)
                showSnackBar = false
            }
        }
    }
}

@Composable
fun CheckUI(
    inquiry: InquiryListResponses,
    expandedInquiryIds: Set<Long>,
    detailStates: Map<Long, InquiryVM.InquiryDetailUiState>,
    onInquiryClick: (Long) -> Unit,
    onPageClick: (Int) -> Unit,
    onSupportClick: () -> Unit
) {
    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column {
            Row (verticalAlignment = Alignment.CenterVertically){
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = AppColor.text1)) {
                            append("문의 내역")
                        }
                        append(" ")
                        withStyle(SpanStyle(color = AppColor.Main1)) {
                            append("${inquiry.totalElements}")
                        }
                    },
                    style = AppTypography.Headline2
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "최신순",
                    style = AppTypography.Caption1,
                    color = AppColor.text4
                )
            }
            Spacer(Modifier.height(8.dp))
            if(inquiry.totalElements.toInt() == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "등록된 문의 내역이 없어요",
                            style = AppTypography.Headline1,
                            color = AppColor.text2,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "궁금한 점이 있다면 문의하기를 통해 남겨주세요.",
                            style = AppTypography.Label1,
                            color = AppColor.text3w,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(32.dp))
                        BlockButton(
                            modifier = Modifier
                                .size(136.dp, 47.dp),
                            text = "문의하기",
                            onClick = onSupportClick,
                            style = AppTypography.Headline2
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColor.bg0)
            ){
                Column {
                    inquiry.contents.forEachIndexed { index, item ->
                        InquiryItem(
                            inquiry = item,
                            onClick = onInquiryClick,
                            expanded = item.id in expandedInquiryIds,
                            detailUi = detailStates[item.id] ?: InquiryVM.InquiryDetailUiState.Idle
                        )
                        if (index < inquiry.contents.lastIndex) {
                            HorizontalDivider(
                                color = AppColor.divider1
                            )
                        }
                    }
                }
            }
            if (inquiry.totalElements.toInt() > 0) {
                InquiryPagination(
                    currentPage = inquiry.page,
                    totalPages = inquiry.totalPages,
                    onPageClick = onPageClick
                )
            }
        }
    }
}
@Composable
fun InquiryItem(
    inquiry: InquiryResponses,
    onClick: (Long) -> Unit,
    expanded: Boolean,
    detailUi: InquiryVM.InquiryDetailUiState,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(inquiry.id) }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Cip(
                        state = CipState.Default,
                        text = inquiry.type,
                        onClick = {},
                        style = AppTypography.Caption1,
                        modifier = Modifier.height(22.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = inquiry.title,
                        style = AppTypography.Headline2,
                        color = AppColor.text1
                    )
                    Text(
                        text = inquiry.createdAt.substringBefore("T"),
                        style = AppTypography.Caption1,
                        color = AppColor.text4
                    )
                }
                Spacer(Modifier.weight(1f))
                Cip(
                    state = if (inquiry.status == "PENDING") CipState.Disabled else CipState.Active,
                    text = if (inquiry.status == "PENDING") "답변 대기" else "답변 완료",
                    onClick = {},
                    style = AppTypography.Caption1,
                    modifier = Modifier.height(22.dp)
                )
                Spacer(Modifier.width(4.dp))
                Image(
                    painter = painterResource(
                        id = if (expanded) R.drawable.chevron_up
                        else R.drawable.chevron_down
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            AnimatedVisibility(visible = expanded) {
                when (detailUi) {
                    is InquiryVM.InquiryDetailUiState.Success -> {
                        val detail = detailUi.inquiry
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .background(AppColor.bg0)
                                    .border(1.dp, AppColor.divider1, RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "문의내용",
                                        style = AppTypography.Caption1,
                                        color = AppColor.text4
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = detail.content,
                                        style = AppTypography.Body2_Reading,
                                        color = AppColor.text1
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppColor.bg1),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.timer),
                                        contentDescription = "stopwatch",
                                        modifier = Modifier.size(32.dp),
                                        tint = AppColor.icon_default

                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "답변 대기 중입니다",
                                        style = AppTypography.Label1,
                                        color = AppColor.text3,
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "문의해주신 내용을 확인하고 있어요.\n순차적으로 답변드릴게요.",
                                        style = AppTypography.Caption1,
                                        color = AppColor.text4,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            if (detail.status == "ANSWERED") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimitiveColor.Purple200)
                                        .padding(16.dp)
                                ) {
                                    detail.answer.let { answer ->
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "답변",
                                                    style = AppTypography.Caption1,
                                                    color = AppColor.Main1
                                                )
                                                Text(
                                                    text = answer.answeredAt.substringBefore("T"),
                                                    style = AppTypography.Caption1,
                                                    color = AppColor.text4
                                                )
                                            }
                                            Text(
                                                text = answer.content,
                                                style = AppTypography.Body2_Reading,
                                                color = AppColor.text1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    InquiryVM.InquiryDetailUiState.Failed -> {
                        Text(
                            text = "문의 내용을 불러오지 못했습니다.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun SupportUI(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSubmit: (title: String, typeCode: String, content: String) -> Unit,
    resetForm: Boolean,
    onResetFormDone: () -> Unit,
){
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var selectedTypeText by remember { mutableStateOf("") }
    var selectedTypeCode by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    val inquiryTypes = listOf(
        "버그 신고" to "BUG_REPORT",
        "기능 제안" to "FEATURE_SUGGESTION",
        "콘텐츠 오류" to "CONTENT_ERROR",
        "기타" to "OTHER"
    )

    LaunchedEffect(resetForm) {
        if (resetForm) {
            title = ""
            content = ""
            selectedTypeText = ""
            selectedTypeCode = ""
            onExpandedChange(false)
            onResetFormDone()
        }
    }

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            Text(
                text = "문의유형을 선택해주세요",
                style = AppTypography.Headline2
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .border(1.dp, AppColor.divider1, RoundedCornerShape(8.dp))
                    .clickable {
                        onExpandedChange(!expanded)
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedTypeText.ifEmpty { "문의 유형을 선택해주세요." },
                    style = AppTypography.Label1,
                    color = if (selectedTypeText.isEmpty()) AppColor.text4 else AppColor.text1
                )
                Spacer(Modifier.weight(1f))
                Image(
                    painter = painterResource(id = if (!expanded) R.drawable.chevron_down else R.drawable.chevron_up),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

            val preventParentScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        return available
                    }

                    override suspend fun onPostFling(
                        consumed: Velocity,
                        available: Velocity
                    ): Velocity {
                        return available
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(216.dp)
                        .nestedScroll(preventParentScrollConnection)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0),
                    userScrollEnabled = true
                ) {
                    items(inquiryTypes) { type ->
                        val typeText = type.first
                        val typeCode = type.second
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .background(
                                    if (selectedTypeText == typeText) AppColor.bg3
                                    else AppColor.bg0
                                )
                                .clickable {
                                    selectedTypeText = typeText
                                    selectedTypeCode = typeCode
                                    onExpandedChange(false)
                                }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = typeText,
                                style = AppTypography.Body2_Nomal,
                                color = AppColor.text1
                            )
                        }
                    }
                }
            }
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                placeholder = {
                    Text(
                        text = "제목을 입력해주세요.",
                        style = AppTypography.Label1,
                        color = AppColor.text4
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .border(1.dp, AppColor.divider1, RoundedCornerShape(8.dp)),
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                singleLine = false,
                minLines = 5,
                maxLines = 5,
                placeholder = {
                    Text(
                        text = "문의 내용을 입력해주세요.",
                        style = AppTypography.Label1,
                        color = AppColor.text4
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .border(1.dp, AppColor.divider1, RoundedCornerShape(8.dp)),
            )

            BlockButton(
                text = "등록하기",
                onClick = {
                    onSubmit(title, selectedTypeCode, content)
                },
                enabled = title.isNotBlank() &&
                        content.isNotBlank() &&
                        selectedTypeCode.isNotBlank(),
                modifier = Modifier.height(56.dp)
            )
        }
    }
}
@Composable
fun PageNumber(
    page: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .then(
                if (selected) {
                    Modifier
                        .border(1.dp, AppColor.Main1, RoundedCornerShape(6.dp))
                        .background(PrimitiveColor.Purple50)
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = page.toString(),
            style = AppTypography.Body2_Reading,
            color = if (selected) {
                AppColor.Main1
            } else {
                AppColor.text3
            }
        )
    }
}
@Composable
fun InquiryPagination(
    currentPage: Int,
    totalPages: Int,
    onPageClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.chevron_left),
            contentDescription = "이전 페이지",
            tint = AppColor.icon_disabled,
            modifier = Modifier
                .size(24.dp)
                .clickable(enabled = currentPage > 1) {
                    onPageClick(currentPage - 1)
                }
        )
        Spacer(Modifier.width(8.dp))

        val startPage = when {
            totalPages <= 5 -> 1
            currentPage <= 3 -> 1
            currentPage >= totalPages - 2 -> totalPages - 4
            else -> currentPage - 2
        }

        val endPage = minOf(
            startPage + 4,
            totalPages
        )

        for (page in startPage..endPage) {
            PageNumber(
                page = page,
                selected = page == currentPage,
                onClick = {
                    onPageClick(page)
                }
            )

            Spacer(Modifier.width(8.dp))
        }

        if (endPage < totalPages) {
            Text(
                text = "•••",
                style = AppTypography.Caption1,
                color = AppColor.text3
            )

            Spacer(Modifier.width(8.dp))

            PageNumber(
                page = totalPages,
                selected = currentPage == totalPages,
                onClick = {
                    onPageClick(totalPages)
                }
            )
            Spacer(Modifier.width(8.dp))
        }

        Icon(
            painter = painterResource(R.drawable.chevron_right),
            contentDescription = "다음 페이지",
            tint = AppColor.icon_disabled,
            modifier = Modifier
                .size(24.dp)
                .clickable(enabled = currentPage < totalPages) {
                    onPageClick(currentPage + 1)
                }
        )
    }
}
