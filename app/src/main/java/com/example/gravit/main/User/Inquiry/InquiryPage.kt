package com.example.gravit.main.User.Inquiry

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.inuappcenter.gravit.main.User.TapButton
import com.inuappcenter.gravit.main.User.TopBar
import androidx.compose.ui.unit.Velocity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.InquiryResponses
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Study.Problem.CustomSnackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

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
    val detailUi by vm.inquiryDetailState.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    var selectedInquiryId by remember { mutableStateOf<Long?>(null) }

    var selectedTab by remember { mutableStateOf(InquiryTab.Support) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    
    var showSnackBar by remember { mutableStateOf(false) }
    var snackBarText by remember { mutableStateOf("") }

    val isLoading =
        loadUi == InquiryVM.LoadUiState.Loading || submitUi == InquiryVM.UiState.Loading || detailUi == InquiryVM.InquiryDetailUiState.Loading

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
        selectedInquiryId = null

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
    LaunchedEffect(detailUi) {
        if (navigated) return@LaunchedEffect

        when (detailUi) {
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

    LaunchedEffect(listState) {
        snapshotFlow {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val total = listState.layoutInfo.totalItemsCount
            if (last != null) last to total else null
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (lastVisible >= total - 3) {
                    vm.loadMoreInquiryList()
                }
            }
    }

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg2)
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
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TapButton(
                            text = "문의하기",
                            selected = selectedTab == InquiryTab.Support,
                            onClick = {
                                selectedTab = InquiryTab.Support
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TapButton(
                            text = "문의내역확인",
                            selected = selectedTab == InquiryTab.Check,
                            onClick = {
                                selectedTab = InquiryTab.Check
                            },
                            modifier = Modifier.weight(1f)
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
                                items(state.inquiryList.contents) { inquiry ->
                                    CheckUI(
                                        inquiry = inquiry,
                                        onClick = { inquiryId ->
                                            if (selectedInquiryId == inquiryId) {
                                                selectedInquiryId = null
                                            } else {
                                                selectedInquiryId = inquiryId
                                                vm.loadInquiryDetail(inquiryId)
                                            }
                                        },
                                        expanded = selectedInquiryId == inquiry.id,
                                        detailUi = detailUi,
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

//api 확인용 임시 ui
@Composable
fun CheckUI(
    inquiry: InquiryResponses,
    onClick: (Long) -> Unit,
    expanded: Boolean,
    detailUi: InquiryVM.InquiryDetailUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable { onClick(inquiry.id) }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = inquiry.title,
                modifier = Modifier.weight(1f)
            )

            Image(
                painter = painterResource(
                    id = if (expanded) R.drawable.chevron_up
                    else R.drawable.chevron_down
                ),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            when (detailUi) {
                is InquiryVM.InquiryDetailUiState.Success -> {
                    val detail = detailUi.inquiry

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColor.bg0)
                            .padding(16.dp)
                    ) {
                        Text(text = detail.content)

                        if (detail.status == "ANSWERED") {
                            detail.answer.let { answer ->
                                Text(text = "답변")
                                Text(text = answer.content)
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
        "콘텐츠 오류" to "CONTENT_ERROR",
        "기능 제안" to "FEATURE_SUGGESTION",
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
