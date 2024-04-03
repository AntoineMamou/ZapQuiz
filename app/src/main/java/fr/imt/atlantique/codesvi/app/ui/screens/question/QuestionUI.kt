package fr.imt.atlantique.codesvi.app.ui.screens.question

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column



import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import kotlin.random.Random


@Composable
fun Background()
{
    Image(
        painter = painterResource(id = R.drawable.fond),
        contentDescription = null, // Indiquez une description si nécessaire
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}


var categorie = ""
var gameMode = ""
var logo = 0

fun StartQuestion(
    theme : String,
    game_mode : String,
    logoId : Int) {
    categorie = theme
    gameMode = game_mode
    logo=logoId
}

val  fontPrincipale = FontFamily(Font(R.font.bubble_bobble))
@Composable
fun PanneauQuestion(Hauteur : Int, Largeur : Int, Description : String){
    Column ( modifier = Modifier
        .padding(top = 140.dp)
        .fillMaxWidth(),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Box(
            modifier = Modifier
                .width(Largeur.dp)
                .height(Hauteur.dp)
                .background(color = MaterialTheme.colorScheme.secondary)
                .border(
                    10.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = Description,
                    color = Color.White,
                    fontFamily = fontPrincipale,
                    // Aligner le texte au centre de la Row
                )
            }
            Row( modifier = Modifier
                .fillMaxSize()
                .offset(x = -20.dp, y = -20.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
            ){
                Image(
                    painter = painterResource(id = logo),
                    contentDescription = "femer", // Indiquez une description si nécessaire
                    modifier = Modifier
                        .size(40.dp) // Ajuster la taille de l'image selon vos besoins

                )
            }
        }


    }
}

@Composable
fun BoutonQCM(text: String) {
        Box(
            modifier = Modifier
                .padding(vertical = 15.dp, horizontal = 30.dp)
                .size(140.dp, 80.dp)
                .background(color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp))
                .border(5.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))


        ) {
            Text(
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontFamily = fontPrincipale
            )
        }
    }


@Composable
fun ChangerQuestion() {
    val randomIndex = Random.nextInt(4) // Génère un nombre aléatoire entre 0 et 9
    // Utilisez randomIndex pour changer la question en fonction du nombre aléatoire généré
    if (randomIndex == 1) {
        QuestionVraiFaux()
    }
    if (randomIndex == 2) {
        QuestionChoixMultiple()
    }
    if (randomIndex == 3) {
        QuestionLiens()
    }
    if (randomIndex == 4) {
        QuestionText()
    }
}

@Composable
fun QuestionVraiFaux() {

    PanneauQuestion(300, 350, "Répondre par Vrai ou Faux ?")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 140.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier
                .padding(30.dp)
                .size(140.dp, 80.dp)
                .background(color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp))
                .border(5.dp, color = Color.Green, RoundedCornerShape(20.dp))

                )

             {
                Text(text = "VRAI", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center),  fontFamily = fontPrincipale)
            }

            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .size(140.dp, 80.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        RoundedCornerShape(20.dp)
                    )
                    .border(5.dp, color = Color.Red, RoundedCornerShape(20.dp))
            ) {
                Text(text = "FAUX", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center), fontFamily = fontPrincipale)
            }
            // Autres éléments que vous souhaitez inclure dans la rangée
        }
    }

    }




@Composable
fun QuestionChoixMultiple(){
    PanneauQuestion(300,350, "Choisissez la bonne réponse")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 1")
            BoutonQCM(text = "Choix 2")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 3")
            BoutonQCM(text = "Choix 4")


            // Autres éléments que vous souhaitez inclure dans la rangée
        }
    }

}

@Composable
fun QuestionLiens() {
    PanneauQuestion(Hauteur = 100, Largeur = 350, "Assembler les colonnes ensembles")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 1")
            BoutonQCM(text = "Choix 2")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 3")
            BoutonQCM(text = "Choix 4")


            // Autres éléments que vous souhaitez inclure dans la rangée
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 5")
            BoutonQCM(text = "Choix 6")


            // Autres éléments que vous souhaitez inclure dans la rangée
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 7")
            BoutonQCM(text = "Choix 8")


            // Autres éléments que vous souhaitez inclure dans la rangée
        }
    }
}
@Composable
fun QuestionText() {
    PanneauQuestion(Hauteur = 300, Largeur = 350, "Ecriver la bonne réponse")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            var textValue by remember { mutableStateOf("Votre réponse :") }

            TextField(
                shape = RoundedCornerShape(20.dp),
                value = textValue,
                onValueChange = { textValue = it},
                modifier = Modifier
                    .border(5.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                textStyle = TextStyle(
                    color = Color.Black, // Définir la couleur du texte
                    fontFamily = fontPrincipale, // Définir la police du texte

                )

                     // Définir la couleur de fond en blanc
            )

        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            BoutonQCM(text = "Envoyer")
        }
    }
}

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth() // Prend toute la largeur disponible
            .height(100.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
            )
        // Crée une forme avec des bords arrondis seulement en haut
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Prend 50% de la largeur de la Box parent
                .height(20.dp) // Hauteur de la barre de progression
                .background(color = Color.LightGray)
                .align(Alignment.TopStart) // Aligner la barre de progression verte en bas à gauche

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Prend 80% de la largeur de la Box parent
                    .fillMaxHeight() // Hauteur de la barre de progression
                    .background(color = Color.Green)

            ) {

            }

        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f) // Prend 30% de la largeur de la Box parent
                .height(20.dp) // Hauteur de la barre de progression
                .background(color = Color.LightGray)
                .align(Alignment.BottomStart) // Aligner la barre de progression rouge en bas à droite
        ) {Box(
            modifier = Modifier
                .fillMaxWidth(0.8f) // Prend 80% de la largeur de la Box parent
                .fillMaxHeight() // Hauteur de la barre de progression
                .background(color = Color.Red)
        )}


    }

}

@Composable
fun Global(){

    if (gameMode == "Solo") {
        ProgressBar()
        ChangerQuestion()



    }
    if (gameMode == "Multi") {


    }
    if (gameMode == "Duel") {


    }
}





@Composable
fun QuestionScreen(
    state : QuestionState,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Background()
    Global()


}