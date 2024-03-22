package fr.imt.atlantique.codesvi.app.ui.screens.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen

@Composable
fun rememberShopState(navController: NavHostController): ShopState {
    return remember(navController) {
        ShopState(navController)
    }
}

@Stable
class ShopState(private val navController: NavHostController) {

    fun navigateTo(
        screen : HomeRootScreen
    ) = navController.navigate(screen.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

}