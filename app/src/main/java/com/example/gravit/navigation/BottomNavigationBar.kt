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
import android.util.Log
import androidx.compose.foundation.Image

data class BottomNavItem(
    val route: String,
    val icon: Int,
    val selectedIcon: Int,
    val label: String?)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", icon = R.drawable.unselected_home_button, selectedIcon = R.drawable.selected_home_button,"홈"),
        BottomNavItem("study", icon = R.drawable.unselected_study_button, selectedIcon = R.drawable.selected_study_button,"학습"),
        BottomNavItem("league", icon = R.drawable.unselected_league_button, selectedIcon = R.drawable.selected_league_button,"리그"),
        BottomNavItem("user", icon = R.drawable.unselected_user_button, selectedIcon = R.drawable.selected_user_button,"사용자")
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    NavigationBar(
        modifier = Modifier.height(100.dp), // 바텀바 높이
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {

        items.forEach { item ->
            val selected = currentRoute == item.route || currentRoute?.startsWith("${item.route}/") == true
            NavigationBarItem(
                icon = {
                    Image( // Icon -> Image 로 바꿨더니 변경이 됨
                        painter = painterResource(id = if (selected) item.selectedIcon else item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp, 43.dp)
                    )
                },
                selected = selected,
                onClick = {
                    val currentRoute = navController.currentDestination?.route

                    val targetRoute = item.route

                    if (currentRoute?.startsWith(targetRoute) == true) {
                        // 이미 선택된 상태일 때만 study 초기화 (예: study/earth → study)
                        if (targetRoute == "study") {
                            navController.navigate("study") {
                                launchSingleTop = true
                            }
                        }
                    } else {
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            // 학습 탭만 복원하지 않음, 추후에 다른 페이지 생기면 수정할 예정
                            restoreState = item.route != "study"
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
