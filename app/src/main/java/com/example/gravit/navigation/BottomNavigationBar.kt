
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.inuappcenter.gravit.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.navigation.NavGraph.Companion.findStartDestination


data class BottomNavItem(
    val route: String,
    val icon: Int,
    val selectedIcon: Int,
    val label: String?)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", R.drawable.unselected_home_button, R.drawable.selected_home_button, "홈"),
        BottomNavItem("chapter", R.drawable.unselected_study_button, R.drawable.selected_study_button, "학습"),
        BottomNavItem("league", R.drawable.unselected_league_button, R.drawable.selected_league_button, "리그"),
        BottomNavItem("user", R.drawable.unselected_user_button, R.drawable.selected_user_button, "사용자")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    val inLearnStack = currentRoute.startsWith("chapter")
            || currentRoute.startsWith("unit")
            || currentRoute.startsWith("lessonList")
    val inUserStack = currentRoute.startsWith("user")

    Surface(
        shadowElevation = 26.dp,
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 30.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .height(60.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val selected = when (item.route) {
                        "chapter" -> inLearnStack
                        "user" -> inUserStack
                        else -> currentRoute == item.route ||
                                currentRoute.startsWith("${item.route}/")
                    }
                    Box(
                        modifier = Modifier
                            .height(43.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                val current = navController.currentDestination?.route
                                val target = item.route
                                when {
                                    target == "home" -> {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = false
                                            }
                                            launchSingleTop = true
                                            restoreState = false
                                        }
                                    }

                                    target == "chapter" && current?.startsWith("chapter") == true -> {
                                        navController.navigate("chapter") {
                                            launchSingleTop = true
                                        }
                                    }

                                    target == "user" && inUserStack -> {
                                        navController.navigate("user") {
                                            launchSingleTop = true
                                            restoreState = false
                                        }
                                    }

                                    else -> {
                                        navController.navigate(target) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = false
                                                saveState = false
                                            }
                                            launchSingleTop = true
                                            restoreState = false
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (selected) item.selectedIcon else item.icon
                            ),
                            contentDescription = item.label,
                            modifier = Modifier.height(43.dp)
                        )
                    }

                    if (index < items.lastIndex) {
                        val space = if (index == items.lastIndex - 1) 58.dp else 60.dp
                        Spacer(modifier = Modifier.width(space))

                    }

                }
            }
        }
    }

}