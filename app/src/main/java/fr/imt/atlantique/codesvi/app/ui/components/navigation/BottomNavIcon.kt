package fr.imt.atlantique.codesvi.app.ui.components.navigation

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavIcon(
    icon: ImageVector,
    description: String = ""
) {
    Icon(
        imageVector = icon,
        contentDescription = description
    )
}