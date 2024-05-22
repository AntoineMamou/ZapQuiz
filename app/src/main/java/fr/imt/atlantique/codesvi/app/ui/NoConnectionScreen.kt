package fr.imt.atlantique.codesvi.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale
import fr.imt.atlantique.codesvi.app.ui.screens.question.Background


@Composable
fun NoConnectionScreen() {
    Background()  // Supposons que vous avez déjà un composable Background() qui définit l'arrière-plan de l'écran

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.no_wifi),  // Assurez-vous que cette ressource existe
                contentDescription = "No Internet Connection",
                modifier = Modifier.size(120.dp),
            )
            Text(
                text = "Oups ! Pas de connexion Internet.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = fontPrincipale,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Veuillez vérifier vos paramètres réseau",
                fontSize = 18.sp,
                color = Color.DarkGray,
                fontFamily = fontPrincipale,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}