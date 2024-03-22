package fr.imt.atlantique.codesvi.app.ui.screens.solo


import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen

@Composable
fun rememberSoloState(navController: NavHostController): SoloState {
    return remember(navController) {
        SoloState(navController)
    }
}

@Stable
class SoloState(private val navController: NavHostController) {

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