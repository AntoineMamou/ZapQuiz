package fr.imt.atlantique.codesvi.app.ui.screens.solo

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.imt.atlantique.codesvi.app.ui.screens.profile.ProfileState



@Composable
fun text(){
    Text("Mode de jeu solo")
}

@Composable
fun SoloScreen(
    state : SoloState,
    modifier: Modifier = Modifier
) {
    text()
}