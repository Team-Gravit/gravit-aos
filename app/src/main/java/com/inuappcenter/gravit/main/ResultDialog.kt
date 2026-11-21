package com.inuappcenter.gravit.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.inuappcenter.gravit.ui.theme.AppColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.ui.theme.pretendard

@Composable
fun ResultDialog(
    onDismiss: () -> Unit,
    titleText: String,
    descriptionText: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val dialogWidth = when {
                maxWidth < 360.dp -> maxWidth - 32.dp
                maxWidth < 600.dp -> 360.dp.coerceAtMost(maxWidth - 32.dp)
                maxWidth < 840.dp -> 480.dp
                else -> 560.dp
            }

            Surface(
                modifier = Modifier
                    .width(dialogWidth)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.report_check),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = titleText,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        color = AppColor.text1,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = descriptionText,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = AppColor.text4,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.6.sp
                    )

                    Spacer(Modifier.height(19.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        onClick = {
                            onButtonClick()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8100B3),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = buttonText,
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