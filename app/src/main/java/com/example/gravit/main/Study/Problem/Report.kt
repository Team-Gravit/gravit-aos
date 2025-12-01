package com.example.gravit.main.Study.Problem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.pretendard

@Composable
fun ReportDialog(
    modifier: Modifier = Modifier,
    navController: NavController,
    problemId: Int
){
    val context = LocalContext.current
    val vm: ReportVM = viewModel(
        factory = ReportVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(ui) {
        when (ui) {
            ReportVM.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            ReportVM.UiState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            else -> Unit
        }
    }
    var showDialog by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Image(
        painter = painterResource(id = R.drawable.report),
        contentDescription = "report",
        modifier = modifier
            .size(24.dp)
            .clickable { showDialog = true }
    )

    if(showDialog){
        var text by remember { mutableStateOf("") }
        var selectedIndex by remember { mutableStateOf<Int?>(null) }
        val options = listOf(
            "문제/선지에 오탈자가 있습니다." to "TYPO_ERROR",
            "문제 자체에 오류가 있습니다." to "CONTENT_ERROR",
            "답안에 오류가 있습니다." to "ANSWER_ERROR",
            "기타" to "OTHER_ERROR"
        )
        val canSubmit = selectedIndex != null
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) {
            Surface(
                modifier = Modifier.height(497.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Column (modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.report_color),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "신고하기",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Column(Modifier.padding(vertical = 16.dp)) {
                        options.forEachIndexed { idx, opt ->
                            Option(
                                isChecked = selectedIndex == idx,
                                text = opt.first,
                                onClick = { selectedIndex = idx}
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = {
                                Text(
                                    text = "어떤 부분에 문제가 있었는지 최대한 자세하게 작성해 주세요.\n유형이 두 개 이상인 경우, 기타를 선택하고 자세하게 작성해 주세요.",
                                    color = Color(0xFF868686),
                                    fontFamily = pretendard,
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(102.dp),
                            shape = RoundedCornerShape(10.dp),
                        )
                    }
                    Row {
                        ReportButton(
                            onClick = { showDialog = false },
                            text = "그만두기",
                            bgC = Color(0xFFA8A8A8),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(19.dp))
                        ReportButton(
                            onClick = {
                                val selected = selectedIndex?.let { options[it] }
                                val reportType = selected?.second ?: return@ReportButton
                                val content = text
                                vm.submit(reportType, content, problemId)
                                showDialog = false
                                showConfirm = true },
                            text = "제출하기",
                            bgC = Color(0xFF8100B3),
                            modifier = Modifier.weight(1f),
                            enabled = canSubmit
                        )
                    }
                }
            }
        }
    }

    if(showConfirm && !showDialog){
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) {
            Surface(
                modifier = Modifier.height(294.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 43.dp),
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Image(
                        painter = painterResource(id = R.drawable.check_circle),
                        contentDescription = null,

                        )
                    Spacer(Modifier.height(23.dp))
                    Text(
                        text = "회원님의 신고가 접수되었어요.",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "회원님의 소중한 의견들을 모아\n더욱 쾌적한 앱 환경을 만들겠습니다.\n단, 허위로 신고할 경우 제재 대상이 될 수 있어요.",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color(0xFF868686),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(28.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = { showConfirm = false },
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF8100B3)
                        )
                    ) {
                        Text(
                            text = "확인",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun ReportButton(
    text: String,
    bgC: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgC,
            contentColor = Color.White,
            disabledContainerColor = bgC,
            disabledContentColor = Color.White
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}

@Composable
fun Option(
    text : String,
    onClick: () -> Unit,
    isChecked: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 퍼지는 효과 끔
            ) { onClick() }
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF2F2F2)),
        verticalAlignment = Alignment.CenterVertically
    ){
        Spacer(Modifier.width(16.dp))
        Image(
            painter = painterResource(id = if(isChecked) R.drawable.checked else R.drawable.unchecked),
            contentDescription = if(isChecked) "checked" else "unchecked",
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontFamily = pretendard,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF383838)
        )
    }
}
