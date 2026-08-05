package com.inuappcenter.gravit.main.Study.Problem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.main.ResultDialog
import com.example.gravit.ui.theme.AppColor
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R

@Composable
fun ReportDialog(
    modifier: Modifier = Modifier,
    navController: NavController,
    problemId: Long,
    onOverlayOpened: () -> Unit = {},
    onOverlayClosed: () -> Unit = {},
){
    val context = LocalContext.current
    val vm: ReportVM = viewModel(
        factory = ReportVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(ui) {
        when (ui) {
            ReportVM.UiState.SessionExpired -> {
                navController.navigate("error/401") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            ReportVM.UiState.NotFound -> {
                navController.navigate("error/404") {
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
    var showDialog by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Icon(
        painter = painterResource(id = R.drawable.report),
        contentDescription = "report",
        modifier = modifier
            .size(24.dp)
            .clickable {
                showDialog = true
                onOverlayOpened()
            },
        tint = AppColor.icon_default
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
            onDismissRequest = {
                showDialog = false
                onOverlayClosed()
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8F8F8)
            ){
                Column {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 20.dp
                            )
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "신고하기",
                                fontFamily = pretendard,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFA8A8A8)
                            )


                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable(
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        },
                                        indication = null
                                    ) {
                                        showDialog = false
                                        onOverlayClosed()
                                    }
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color(0xFFE5E5E5)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            )
                    ) {

                        options.forEachIndexed { idx, opt ->

                            Option(
                                isChecked = selectedIndex == idx,
                                text = opt.first,
                                onClick = {
                                    selectedIndex = idx
                                }
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 10.dp
                                )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "신고내용",
                                    fontFamily = pretendard,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = AppColor.divider1
                                )

                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                BasicTextField(
                                    value = text,
                                    onValueChange = {
                                        text = it
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),

                                    textStyle = TextStyle(
                                        color = Color.Black,
                                        fontFamily = pretendard,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                    ) {
                        ReportButton(
                            onClick = {

                                showDialog = false
                                onOverlayClosed()

                            },

                            text = "그만두기",

                            bgC = Color(0xFFA8A8A8),

                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(19.dp)
                        )


                        ReportButton(
                            onClick = {

                                val selected =
                                    selectedIndex?.let { options[it] }

                                val reportType =
                                    selected?.second
                                        ?: return@ReportButton

                                vm.submit(
                                    reportType,
                                    text,
                                    problemId
                                )


                                showDialog = false
                                showConfirm = true
                            },


                            text = "제출하기",

                            bgC = Color(0xFF8100B3),

                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp),

                            enabled = canSubmit
                        )
                    }
                }
            }
        }
    }

    if(showConfirm && !showDialog){
        Dialog(
            onDismissRequest = {
                showConfirm = false
                onOverlayClosed()
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
                usePlatformDefaultWidth = false
            )
        ) {
            ResultDialog(
                onDismiss = {
                    showDialog = false
                },
                titleText = "회원님의 신고가\n접수되었어요.",
                descriptionText = "회원님의 소중한 의견들을 모아\n더욱 쾌적한 앱 환경을 만들겠습니다.\n단, 허위로 신고할 경우 제재 대상이 될 수 있어요.",
                buttonText = "확인",
                onButtonClick = {
                    showConfirm = false
                    onOverlayClosed()
                }
            )
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
            .height(42.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = AppColor.divider1,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ){

        Spacer(
            Modifier.width(12.dp)
        )

        Image(
            painter = painterResource(
                id = if(isChecked)
                    R.drawable.checked
                else
                    R.drawable.unchecked
            ),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            Modifier.width(10.dp)
        )

        Text(
            text = text,
            fontFamily = pretendard,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF383838)
        )
    }
}