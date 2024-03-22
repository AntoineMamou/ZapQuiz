package fr.imt.atlantique.codesvi.app.ui.screens.multi

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.imt.atlantique.codesvi.app.ui.screens.multi.MultiState



@Composable
fun text(){
    Text("Mode de jeu multi")
}

@Composable
fun MultiScreen(
    state : MultiState,
    modifier: Modifier = Modifier
) {
    text()
}