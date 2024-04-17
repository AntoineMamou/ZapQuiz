package fr.imt.atlantique.codesvi.app.ui.screens.question

import android.annotation.SuppressLint
import android.icu.text.ListFormatter.Width
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.QCM
import fr.imt.atlantique.codesvi.app.data.model.Answer
import fr.imt.atlantique.codesvi.app.ui.navigation.RootScreen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.Normalizer
import java.util.Timer
import java.util.TimerTask
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

fun removeAccentsAndUpperCase(word: String): String {
    // Convertir le mot en minuscules
    var lowerCaseWord = word.toLowerCase()

    // Retirer les accents en utilisant la décomposition canonique
    val normalizedWord = Normalizer.normalize(lowerCaseWord, Normalizer.Form.NFD)
    val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    return regex.replace(normalizedWord, "")
}


var categorie = ""
var gameMode = ""
var logo = 0

fun StartQuestion(
    theme : String,
    game_mode : String,
    logoId : Int) {
    categorie = removeAccentsAndUpperCase(theme)
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
                .clip(RectangleShape)
        ) {

                Horizontalbar(false, false)

                Horizontalbar(reverse = false, vertical = true )

                Horizontalbar(reverse = true, vertical = true )




            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = Description,
                    color = Color.White,
                    fontFamily = fontPrincipale,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 20.dp, end = 20.dp)

                    // Aligner le texte au centre de la Row
                )
            }


            Horizontalbar(false, true)

            Row(modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start) {
                Horizontalbar(true, false)

            }


        }


    }
}


@Composable
fun Horizontalbar(reverse : Boolean, vertical : Boolean ) {
    var progress by remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }
    var pointx by remember { mutableStateOf(0f) }
    var pointy by remember { mutableStateOf(0f) }
    var taille by remember { mutableStateOf(0) }



    if(!reverse && !vertical){
        rotation = 0f
        taille = 350
    }else if (!reverse && vertical) {
        rotation = 90f
        pointx = 0.028f
        pointy=  0f
        taille = 350
    } else if (reverse && !vertical){
        rotation = 180f
        taille = 350
    } else if (reverse && vertical){
        rotation = 270f
        pointx = 0.972f
        pointy = 0f
        taille = 350

    }


    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(10) // Attendre 1 seconde
            progress += 0.0016f // Augmenter la valeur de remplissage de 0.1 à chaque intervalle
        }
    }

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .width(taille.dp)
            .height(10.dp)
            .graphicsLayer {
                // Rotation de 180 degrés pour inverser la barre verticale
                rotationZ = rotation
                if (vertical) {
                    transformOrigin = TransformOrigin(pointx, pointy)
                }

            },
        trackColor = Color.Transparent
    )

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
fun ChangerQuestion(onClick: () -> Unit) {

         if (Reponse) {
            GenererQuestion (onClick)
             NombreQuestion += 1
         }else if (!Reponse) {
            NombreDeVies = NombreDeVies - 1
            if (NombreDeVies == 0){

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



suspend fun questionFromDatabase(theme: String, randomNumber: Int): QCM? {
    return suspendCancellableCoroutine { continuation ->
        val questionsRef = databaseGlobal.getReference("questions/$theme")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (questionSnapshot in dataSnapshot.children) {
                    val questionId = questionSnapshot.key?.toIntOrNull()
                    if (questionId == randomNumber) {
                        //val question = questionSnapshot.getValue(QCM::class.java)



                        val id = questionSnapshot.child("id").getValue(String::class.java)
                        val type = questionSnapshot.child("type").getValue(String::class.java)
                        val category = questionSnapshot.child("category").getValue(String::class.java)
                        val level = questionSnapshot.child("level").getValue(Int::class.java)
                        val question2 = questionSnapshot.child("question").getValue(String::class.java)
                        val image = questionSnapshot.child("image").getValue(String::class.java)
                        val gap = questionSnapshot.child("gap").getValue(Float::class.java)
                        val explanation = questionSnapshot.child("explanation").getValue(String::class.java)

                        // Récupération de la liste des réponses
                        val answersList = ArrayList<Answer>()
                        val answersSnapshot = questionSnapshot.child("answers")
                        for (answerSnapshot in answersSnapshot.children) {
                            val answer = answerSnapshot.child("answer").getValue(String::class.java)
                            val isRight = answerSnapshot.child("isRight").getValue(Boolean::class.java)
                            if (answer != null && isRight != null) {
                                val reponse= Answer(isRight,answer)
                                answersList.add(reponse)

                            }
                        }

                        // Création de l'objet QCM avec les données récupérées
                        val question = QCM(answersList, id?:"", type?:"", category?:"", level ?: 0, question2 ?: "", image ?: "", gap ?: 0F, explanation ?: "")

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
        val question = questionFromDatabase(categorie, randomIndex)
        qcmState.value = question
    }

    val qcm = qcmState.value
    if (qcm != null) {
        println(qcm)
        val liste = qcm.answers
        val Questiontxt = qcm.question


        PanneauQuestion(350, 350, Questiontxt)
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
                BoutonQCM(liste[0].answer, onClick, liste[0].isRight)
                BoutonQCM(liste[1].answer, onClick, liste[1].isRight)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                BoutonQCM(liste[2].answer, onClick, liste[2].isRight)
                BoutonQCM(liste[3].answer, onClick, liste[3].isRight)
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
fun ProgressBar(coeur: Int, serieQuestion: Int) {


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
                    fontSize = 20.sp
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
fun GameOver(
    navController : NavHostController
) {
    Column(
        modifier = Modifier
            .padding(top = 50.dp)
            .fillMaxWidth(),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 20.dp, end = 10.dp)

        ) {
            Image(
                painter = painterResource(id = R.drawable.coeur),
                contentDescription = "fermer", // Indiquez une description si nécessaire
                modifier = Modifier.size(250.dp) // Ajuster la taille de l'image selon vos besoins
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color.Red)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Vous avez perdu",
                    color = Color.White,
                    fontFamily = fontPrincipale,
                    fontSize = 25.sp,

                    // Aligner le texte au centre de la Row
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .width(300.dp)
                .height(100.dp)
                .background(color = MaterialTheme.colorScheme.secondary)
                .border(width = 2.dp, color = MaterialTheme.colorScheme.primary) // Ajout de la bordure
        ) {
            Column {


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$NombreQuestion questions réussis",
                        color = Color.White,
                        fontFamily = fontPrincipale,
                        fontSize = 15.sp,

                        // Aligner le texte au centre de la Row
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "0 golden questions réussis",
                        color = Color.White,
                        fontFamily = fontPrincipale,
                        fontSize = 15.sp,

                        // Aligner le texte au centre de la Row
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "0 coins gagnés",
                        color = Color.White,
                        fontFamily = fontPrincipale,
                        fontSize = 15.sp,

                        // Aligner le texte au centre de la Row
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically){

            Box(
                modifier = Modifier
                    .size(180.dp, 150.dp)
                    .background(color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp))
                    .border(5.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                    .clickable(onClick = {
                        navController.navigate(RootScreen.Home.route);
                        NombreQuestion = 0
                        NombreDeVies = 4

                    })


            ) {
                Text(
                    text = "Retour au menu",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center),
                    fontFamily = fontPrincipale
                )
            }


        }




    }
}

@Composable
fun Global(
    navController : NavHostController
){

    var currentContent by remember { mutableStateOf(0) }

    if (gameMode == "Solo") {
        var timer by remember { mutableStateOf(Timer()) }
        var time_spend by remember { mutableStateOf(0) }

        if (NombreDeVies > 0){
        DisposableEffect(Unit) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    // Changer la valeur de currentContent toutes les 10 secondes
                    time_spend += 1

                }
            }, 0, 10) // Déclenche la tâche toutes les 10 secondes

            onDispose {
               println("fin") // Arrête le timer lorsque le composable est supprimé
            }
        }

            LaunchedEffect(currentContent) {
                // Réinitialiser time_spend à zéro à chaque changement de currentContent
                time_spend = 0
            }
            // Display content based on currentContent state
            when (currentContent) {
                0 -> ChangerQuestion(onClick = { currentContent = 1 - currentContent })

                1 -> ChangerQuestion(onClick = { currentContent = 1 - currentContent })
                // Add more cases as needed
            }

            if (time_spend == 1000 || time_spend == 1001){
                Reponse = false
                currentContent = 1 - currentContent

            }
        ProgressBar(NombreDeVies, NombreQuestion) } else { GameOver(navController)
        currentContent = 2}




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
    Global(navController)


}