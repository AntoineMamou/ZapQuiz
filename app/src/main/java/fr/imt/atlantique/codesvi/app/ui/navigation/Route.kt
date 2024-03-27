package fr.imt.atlantique.codesvi.app.ui.navigation

sealed class RootScreen(val route: String) {
    data object Home : RootScreen("home_root")
    data object Login : RootScreen("login_root")
    data object Shop : RootScreen("shop_root")

    data object Solo : RootScreen("solo_root")

    data object  Multi : RootScreen("multi_root")

    data object  Duel : RootScreen("duel_root")

    data object Question : RootScreen("question_root")
}

sealed class HomeRootScreen(val route: String) {
    data object Game : HomeRootScreen("game_screen")
    data object Profile : HomeRootScreen("profile_screen")
    data object Shop : HomeRootScreen("shop_screen")

    data object Solo : HomeRootScreen("solo_screen")
    data object Multi : HomeRootScreen("multi_screen")
    data object Duel : HomeRootScreen("duel_screen")

    data object Question : HomeRootScreen("question_screen")
}
