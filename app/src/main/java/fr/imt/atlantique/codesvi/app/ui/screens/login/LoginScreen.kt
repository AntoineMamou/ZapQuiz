package fr.imt.atlantique.codesvi.app.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.imt.atlantique.codesvi.app.R
import androidx.compose.material3.SearchBar as SearchBar1



@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    state : LoginState,
    onNavigateToHome: () -> Unit
) {
    AnimatedVisibility(visible = true) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,

            ) {

            Image(
                painter = painterResource(id = R.drawable.fond),
                contentDescription = null, // Indiquez une description si nécessaire
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column {
                Row {
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo_zap_quiz),
                        contentDescription = null, // Description de l'image si nécessaire
                        modifier = Modifier.width(250.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                TextField(value = "Pseudo :", onValueChange = {}, shape = RoundedCornerShape(8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = "Mot de passe :", onValueChange = {}, shape = RoundedCornerShape(8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = onNavigateToHome) {
                        Text(text = "Se Connecter ")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onNavigateToHome) {
                        Text(text = "Créer un compte ")
                    }
                }
            }
        }
    }
}