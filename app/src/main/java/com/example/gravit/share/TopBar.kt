package com.inuappcenter.gravit.main.User

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.AuthPrefs

@Composable
fun TopBar(
    navController: NavController,
    title: String,
    height: Dp = 48.dp,
    useIcon: Boolean = true,
    useCloseIcon: Boolean = false,
    isOnboarding: Boolean = false
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .height(height)
            .background(Color.White)
    ) {
        if(useIcon){
            if(useCloseIcon){
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .clickable {
                            if(isOnboarding){
                                AuthPrefs.clear(context)
                            }
                            navController.popBackStack()
                        },
                    tint = Color(0xFF4D4D4D)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(12.dp)
                        .size(24.dp)
                        .clickable {
                            if(isOnboarding){
                                AuthPrefs.clear(context)
                            }
                            navController.popBackStack()
                        },
                    tint = AppColor.icon_default
                )
            }
        }

        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                style = AppTypography.Label1,
                color = AppColor.text2,
            )
        }
    }

    HorizontalDivider(
        color = AppColor.divider1,
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
}