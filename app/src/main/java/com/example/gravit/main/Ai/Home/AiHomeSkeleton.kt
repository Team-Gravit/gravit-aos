package com.inuappcenter.gravit.main.Ai.Home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.PrimitiveColor

@Composable
private fun AiSkeletonBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(AppColor.bg3)
    )
}
@Composable
fun AiHomeSkeletonUI(
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg2),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            AiHomeHeader(navController)
        }

        item {
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColor.bg0)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AiSkeletonBox(
                                    modifier = Modifier.size(40.dp, 20.dp)
                                )

                                Spacer(Modifier.height(6.dp))

                                AiSkeletonBox(
                                    modifier = Modifier.size(55.dp, 12.dp)
                                )
                            }

                            VerticalDivider(
                                modifier = Modifier.height(40.dp),
                                thickness = 1.dp,
                                color = AppColor.divider1
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AiSkeletonBox(
                                    modifier = Modifier.size(40.dp, 20.dp)
                                )

                                Spacer(Modifier.height(6.dp))

                                AiSkeletonBox(
                                    modifier = Modifier.size(70.dp, 12.dp)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColor.bg0)
                            .padding(16.dp)
                    ) {
                        Column {
                            Spacer(Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AiSkeletonBox(
                                    modifier = Modifier.size(80.dp, 14.dp)
                                )

                                Spacer(Modifier.weight(1f))

                                AiSkeletonBox(
                                    modifier = Modifier.size(45.dp, 14.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            repeat(2) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimitiveColor.Gray200)
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AiSkeletonBox(
                                        modifier = Modifier.size(32.dp),
                                        shape = RoundedCornerShape(50f)
                                    )

                                    Spacer(Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AiSkeletonBox(
                                            modifier = Modifier.size(85.dp, 14.dp)
                                        )

                                        Spacer(Modifier.height(6.dp))

                                        AiSkeletonBox(
                                            modifier = Modifier.size(130.dp, 12.dp)
                                        )
                                    }

                                    AiSkeletonBox(
                                        modifier = Modifier.size(42.dp, 22.dp),
                                        shape = RoundedCornerShape(60.dp)
                                    )
                                }

                                if (it == 0) {
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColor.bg0)
                            .padding(16.dp)
                    ) {
                        Column {
                            Spacer(Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AiSkeletonBox(
                                    modifier = Modifier.size(75.dp, 14.dp)
                                )

                                Spacer(Modifier.weight(1f))

                                AiSkeletonBox(
                                    modifier = Modifier.size(45.dp, 14.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            repeat(3) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimitiveColor.Gray200)
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AiSkeletonBox(
                                            modifier = Modifier.size(70.dp, 18.dp)
                                        )

                                        Spacer(Modifier.weight(1f))

                                        AiSkeletonBox(
                                            modifier = Modifier.size(75.dp, 14.dp)
                                        )
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    AiSkeletonBox(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                }

                                if (it != 2) {
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColor.bg0)
                            .padding(16.dp)
                    ) {
                        Column {
                            Spacer(Modifier.height(4.dp))

                            AiSkeletonBox(
                                modifier = Modifier.size(55.dp, 14.dp)
                            )

                            Spacer(Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(212.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    repeat(3) {
                                        AiSkeletonBox(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp),
                                            shape = RoundedCornerShape(0.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(45.dp))
                }
            }
        }
    }
}

@Composable
fun RecentAllSessionsSkeletonUI() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg0)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AiSkeletonBox(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(50f)
                )

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    AiSkeletonBox(
                        modifier = Modifier.size(90.dp, 14.dp)
                    )

                    Spacer(Modifier.height(6.dp))

                    AiSkeletonBox(
                        modifier = Modifier.size(140.dp, 12.dp)
                    )
                }

                AiSkeletonBox(
                    modifier = Modifier.size(42.dp, 22.dp),
                    shape = RoundedCornerShape(60.dp)
                )
            }
        }
    }
}
