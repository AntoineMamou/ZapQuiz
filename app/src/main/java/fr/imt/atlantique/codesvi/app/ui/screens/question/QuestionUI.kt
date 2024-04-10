package fr.imt.atlantique.codesvi.app.ui.screens.question

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Spacer

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.QCM
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random



val databaseGlobal = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")


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

var Reponse by mutableStateOf(true)

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

            }
        }


    }
}



@Composable
fun BoutonQCM(textbouton: String, onClick: () -> Unit, reponsebonne: Boolean) {

        Box(
            modifier = Modifier
                .padding(vertical = 15.dp, horizontal = 30.dp)
                .size(140.dp, 80.dp)
                .background(color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp))
                .border(5.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .clickable(onClick = {
                    onClick()
                    Reponse = reponsebonne
                })


        ) {
            Text(
                text = textbouton,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontFamily = fontPrincipale
            )
        }
    }



var NombreQuestion = -1
var NombreDeVies = 3

@Composable
fun GenererQuestion(onClick: () -> Unit){
    val randomIndex = Random.nextInt(3) // Génère un nombre aléatoire entre 0 et 9
    // Utilisez randomIndex pour changer la question en fonction du nombre aléatoire généré

    if (randomIndex == 0) {
        QuestionChoixMultiple(onClick)
    }
    if (randomIndex == 1) {
        QuestionChoixMultiple(onClick)
    }
    if (randomIndex == 2) {
        QuestionChoixMultiple(onClick)
    }
}

@Composable
fun ReponseQuestion(textreponse : String){
    PanneauQuestion(Hauteur = 400 , Largeur = 450, Description = textreponse )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ){

    }
}




@Composable
fun ChangerQuestion(onClick: () -> Unit) {

         if (Reponse) {
            GenererQuestion (onClick)
             NombreQuestion += 1
         }else if (!Reponse) {
            NombreDeVies = NombreDeVies - 1
            if (NombreDeVies == 0){
                GameOver()
            }else {
                GenererQuestion(onClick)
            }
        }



}

@Composable
fun QuestionVraiFaux(onClick: () -> Unit) {

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
                .clickable(onClick = onClick)

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
                    .clickable(onClick = onClick)
            ) {
                Text(text = "FAUX", color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.Center), fontFamily = fontPrincipale)
            }
            // Autres éléments que vous souhaitez inclure dans la rangée
        }
    }

    }


fun questions(ref : Int): List<String> {
    if (ref == 0){
        return listOf("Quel est la capital de la France ?", "Paris", "Marseille", "Lyon", "Lille")
    }
    if (ref == 1){
        return listOf("Quel fleuve n'est pas présent en France ?", "Le Danube", "Le Rhin", "La Seine", "La Garonne" )
    }
    if (ref == 2){
        return listOf("Quel est le pays le plus peuplée ?", "Inde", "Andorre", "Chine", "Russie" )
    }
    return TODO("Provide the return value")
}

suspend fun questionFromDatabase(theme: String, randomNumber: Int): QCM? {
    return suspendCancellableCoroutine { continuation ->
        val questionsRef = databaseGlobal.getReference("questions/$theme")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (questionSnapshot in dataSnapshot.children) {
                    val questionId = questionSnapshot.key?.toIntOrNull()
                    if (questionId == randomNumber) {
                        val question = questionSnapshot.getValue(QCM::class.java)
                        continuation.resume(question)
                        return
                    }
                }
                continuation.resume(null)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                continuation.resumeWithException(databaseError.toException())
            }
        }

        questionsRef.addListenerForSingleValueEvent(valueEventListener)

        // Annuler l'écouteur de valeurs lorsque la coroutine est annulée
        continuation.invokeOnCancellation {
            questionsRef.removeEventListener(valueEventListener)
        }
    }
}



@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun QuestionChoixMultiple(onClick: () -> Unit) {
    val randomIndex = remember { Random.nextInt(19) }
    val qcmState = remember { mutableStateOf<QCM?>(null) }

    LaunchedEffect(randomIndex) {
        val question = questionFromDatabase("geographie_questions", randomIndex)
        qcmState.value = question
    }

    val qcm = qcmState.value
    if (qcm != null) {
        println(qcm)
        val liste = qcm.answers
        val Questiontxt = qcm.question
        println(liste)


        PanneauQuestion(300, 350, Questiontxt)
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
                BoutonQCM(liste[0].reponse, onClick, liste[0].bonne_réponse)
                BoutonQCM(liste[1].reponse, onClick, liste[1].bonne_réponse)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                BoutonQCM(liste[2].reponse, onClick, liste[2].bonne_réponse)
                BoutonQCM(liste[3].reponse, onClick, liste[3].bonne_réponse)
            }
        }
    }
}



/*
@Composable

fun QuestionLiens(onClick: () -> Unit) {
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
            BoutonQCM(text = "Choix 1", onClick, true)
            BoutonQCM(text = "Choix 2", onClick, true)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 3", onClick, true)
            BoutonQCM(text = "Choix 4", onClick, true)


            // Autres éléments que vous souhaitez inclure dans la rangée
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 5", onClick, true)
            BoutonQCM(text = "Choix 6", onClick, true)


            // Autres éléments que vous souhaitez inclure dans la rangée
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCM(text = "Choix 7", onClick, true)
            BoutonQCM(text = "Choix 8", onClick, true)


            // Autres éléments que vous souhaitez inclure dans la rangée
        }
    }
}
*/

@Composable
fun QuestionText(onClick: () -> Unit) {
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
            BoutonQCM("Envoyer", onClick, true)
        }
    }
}

@Composable
fun ProgressBar(coeur : Int, serieQuestion : Int) {

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
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = " $serieQuestion questions réussis",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = fontPrincipale,
                    // Aligner le texte au centre de la Row
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(bottom = 20.dp, end = 10.dp)
                    .align(Alignment.CenterEnd)
            ) {
                Image(
                    painter = painterResource(id = logo),
                    contentDescription = "femer", // Indiquez une description si nécessaire
                    modifier = Modifier
                        .size(60.dp) // Ajuster la taille de l'image selon vos besoins

                )
            }

            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .align(Alignment.BottomCenter)
            ) {
                repeat(coeur) {
                    Image(
                        painter = painterResource(id = R.drawable.coeur),
                        contentDescription = "fermer", // Indiquez une description si nécessaire
                        modifier = Modifier.size(40.dp) // Ajuster la taille de l'image selon vos besoins
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }


        }

}

@Composable
fun GameOver(){
    PanneauQuestion(Hauteur = 450, Largeur = 400, Description = "Vous avez perdu")
}

@Composable
fun Global(){

    var currentContent by remember { mutableStateOf(0) }

    if (gameMode == "Solo") {


            // Button to change the currentContent state
            // Display content based on currentContent state
            when (currentContent) {
                0 -> ChangerQuestion(onClick = { currentContent = 1 - currentContent })

                1 -> ChangerQuestion(onClick = { currentContent = 1 - currentContent })
                // Add more cases as needed
            }
            println(NombreDeVies)
            ProgressBar(NombreDeVies, NombreQuestion)



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