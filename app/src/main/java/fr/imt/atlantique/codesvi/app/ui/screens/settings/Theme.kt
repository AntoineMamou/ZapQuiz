package fr.imt.atlantique.codesvi.app.ui.screens.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.ui.graphics.vector.ImageVector

enum class Theme(
    val themeName: String,
    val icon: ImageVector
) {
    MATERIAL_YOU(
        themeName = "Material You",
        icon = Icons.Outlined.Wallpaper
    ),
    FOLLOW_SYSTEM(
        themeName = "Suivre les paramètres du système",
        icon = Icons.Outlined.SettingsSuggest
    ),
    LIGHT_THEME(
        themeName = "Light Theme",
        icon = Icons.Outlined.LightMode
    ),
    DARK_THEME(
        themeName = "Dark Theme",
        icon = Icons.Outlined.DarkMode
    );
}