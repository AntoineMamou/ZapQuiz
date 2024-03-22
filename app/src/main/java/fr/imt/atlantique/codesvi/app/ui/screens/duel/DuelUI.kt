package fr.imt.atlantique.codesvi.app.ui.screens.duel

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.imt.atlantique.codesvi.app.ui.screens.duel.DuelState



@Composable
fun text(){
    Text("Mode de jeu duel")
}

@Composable
fun DuelScreen(
    state : DuelState,
    modifier: Modifier = Modifier
) {
    text()
}