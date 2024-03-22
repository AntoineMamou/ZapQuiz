package fr.imt.atlantique.codesvi.app.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen

@Composable
fun rememberGameState(navController: NavHostController): GameState {
    return remember(navController) {
        GameState(navController)
    }
}

@Stable
class GameState(private val navController: NavHostController) {



}