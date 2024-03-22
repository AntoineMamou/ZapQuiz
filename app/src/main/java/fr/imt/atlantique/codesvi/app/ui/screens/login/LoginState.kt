package fr.imt.atlantique.codesvi.app.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen

@Composable
fun rememberLoginState(navController: NavHostController): LoginState {
    return remember(navController) {
        LoginState(navController)
    }
}

@Stable
class LoginState(internal val navController: NavHostController) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentSelectedScreen: HomeRootScreen
        @Composable get() = when (currentDestination?.route) {
            HomeRootScreen.Game.route -> HomeRootScreen.Game
            HomeRootScreen.Profile.route -> HomeRootScreen.Profile
            else -> HomeRootScreen.Game
        }

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