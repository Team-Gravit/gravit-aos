import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gravit.R
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavGraph.Companion.findStartDestination

data class BottomNavItem(
    val route: String,
    val icon: Int,
    val selectedIcon: Int,
    val label: String?)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", icon = R.drawable.unselected_home_button, selectedIcon = R.drawable.selected_home_button,"홈"),
        BottomNavItem("chapter", icon = R.drawable.unselected_study_button, selectedIcon = R.drawable.selected_study_button,"학습"),
        BottomNavItem("league", icon = R.drawable.unselected_league_button, selectedIcon = R.drawable.selected_league_button,"리그"),
        BottomNavItem("user", icon = R.drawable.unselected_user_button, selectedIcon = R.drawable.selected_user_button,"사용자")
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    val inLearnStack = currentRoute.startsWith("chapter")
            || currentRoute.startsWith("units")
            || currentRoute.startsWith("lessonList")
    val inUserStack = currentRoute.startsWith("user")
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp


    NavigationBar(
        modifier = Modifier.height(screenHeight * (100f / 740f)), // 바텀바 높이
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {

        items.forEach { item ->
            val selected = when (item.route) {
                "chapter" -> inLearnStack
                "user" -> inUserStack
                else -> currentRoute == item.route ||
                        currentRoute.startsWith("${item.route}/")
            }
            NavigationBarItem(
                icon = {
                    Image( // Icon -> Image 로 바꿨더니 변경이 됨
                        painter = painterResource(id = if (selected) item.selectedIcon else item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(screenWidth * (24f / 360f), screenHeight * (43f / 740f))
                    )
                },
                selected = selected,
                onClick = {
                    val current = navController.currentDestination?.route
                    val target = item.route

                    when {
                        //홈 버튼 루트로 이동
                        target == "home" -> {
                            navController.navigate("home") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                        //이미 챕터 안이면 root로 초기화
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
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
