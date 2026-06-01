package com.example.gravit.main.User.Support

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.inuappcenter.gravit.main.User.TapButton
import com.inuappcenter.gravit.main.User.TopBar
import androidx.compose.ui.unit.Velocity
import com.inuappcenter.gravit.R
import kotlin.math.sin

enum class SupportTab {
    Support,
    Check
}

@Composable
fun CustomerSupport(
    navController: NavController,
){
    var selectedTab by remember { mutableStateOf(SupportTab.Support) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg2),
    ) {
        item {
            TopBar(
                navController = navController,
                title = "문의하기",
                useCloseIcon = false,
                height = 48.dp
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColor.bg2)
            ) {
                Spacer(Modifier.height(4.dp))
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
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
                            selected = selectedTab == SupportTab.Support,
                            onClick = {
                                selectedTab = SupportTab.Support
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TapButton(
                            text = "문의내역확인",
                            selected = selectedTab == SupportTab.Check,
                            onClick = {
                                selectedTab = SupportTab.Check
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    when (selectedTab) {
                        SupportTab.Support -> SupportUI(
                            expanded = dropdownExpanded,
                            onExpandedChange = {
                                dropdownExpanded = it
                            }
                        )

                        SupportTab.Check -> CheckUI()
                    }
                }
            }
        }
    }
}

@Composable
fun CheckUI() {
    TODO("Not yet implemented")
}

@Composable
fun SupportUI(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
){
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    var selectedType by remember { mutableStateOf("") }

    val listState = rememberLazyListState()


    val inquiryTypes = listOf(
        "버그 신고",
        "콘텐츠 오류",
        "기능 제안",
        "기타",
        "감자",
        "우동"
    )

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
        ){
            Text(
                text = selectedType.ifEmpty { "문의 유형을 선택해주세요." },
                style = AppTypography.Label1,
                color = if (selectedType.isEmpty()) AppColor.text4 else AppColor.text1
            )
            Spacer(Modifier.weight(1f))
            Image(painter = painterResource(id = if(!expanded) R.drawable.chevron_down else R.drawable.chevron_up),
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .background(
                                if (selectedType == type) AppColor.bg3
                                else AppColor.bg0
                            )
                            .clickable {
                                selectedType = type
                                onExpandedChange(false)
                            }
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = type,
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
            placeholder = {
              Text(
                  text = "제목을 입력해주세요.",
                  style = AppTypography.Label1,
                  color = AppColor.text4,
                  modifier = Modifier.verticalScroll(rememberScrollState())
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
            value = text,
            onValueChange = { text = it },
            placeholder = {
                Text(
                    text = "문의 내용을 입력해주세요.",
                    style = AppTypography.Label1,
                    color = AppColor.text4,
                    modifier = Modifier.verticalScroll(rememberScrollState())
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
            onClick = {},
            enabled = (text != "" && title != ""),
            modifier = Modifier.height(56.dp)
        )
    }
}
