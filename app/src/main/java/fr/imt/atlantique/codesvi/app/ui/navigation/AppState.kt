package fr.imt.atlantique.codesvi.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController(),
): AppState {
    return remember(navController) {
        AppState(navController)
    }
}

@Stable
class AppState(val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentSelectedBottomNav: HomeRootScreen
        @Composable get() = when (currentDestination?.route) {
            HomeRootScreen.Game.route -> HomeRootScreen.Game
            HomeRootScreen.Profile.route -> HomeRootScreen.Profile
            HomeRootScreen.Shop.route -> HomeRootScreen.Shop
            else -> HomeRootScreen.Game
        }

    val isBottomBarVisible: Boolean
        @Composable get() = when (currentDestination?.route) {
            HomeRootScreen.Game.route -> true
            HomeRootScreen.Profile.route -> true
            HomeRootScreen.Shop.route -> true
            HomeRootScreen.Solo.route -> false
            HomeRootScreen.Multi.route -> false
            HomeRootScreen.Duel.route -> false
            else -> false
        }


    fun navigateInsideHome(
        screen : HomeRootScreen
    ) = navController.navigate(screen.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

}