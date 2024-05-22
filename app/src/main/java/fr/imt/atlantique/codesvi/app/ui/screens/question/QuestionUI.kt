package fr.imt.atlantique.codesvi.app.ui.screens.question


import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.Answer
import fr.imt.atlantique.codesvi.app.data.model.GameViewModel
import fr.imt.atlantique.codesvi.app.data.model.QCM
import fr.imt.atlantique.codesvi.app.ui.navigation.RootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.game.user
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
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
    // Convert the word to lowercase
    var lowerCaseWord = word.toLowerCase()

    // Remove accents using canonical decomposition
    val normalizedWord = Normalizer.normalize(lowerCaseWord, Normalizer.Form.NFD)
    val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    val withoutAccents = regex.replace(normalizedWord, "")

    // Replace spaces with underscores
    val withUnderscores = withoutAccents.replace(" ", "_")

    return withUnderscores
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

var NombreQuestion = -1
var NombreDeVies = 3

var QuestionIndexList = mutableStateOf(IntArray(10)  { it }.toMutableList())
// Recuperer la taille de la liste

/*
##################################################
#          GENERATION DES ELEMENTS GRAPHIQUES    #
##################################################
*/

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
fun PanneauQuestion(Hauteur: Int, Largeur: Int, Description: String){

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
            progress += 0.001f // Augmenter la valeur de remplissage  à chaque intervalle
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
                    ScreenTransitor = true
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






/*
##################################################
#          GENERATION DE LA PROCHAINE QUESTION   #
##################################################
*/

var ScreenTransitor = false

@Composable
fun ChangerQuestion(onClick: () -> Unit) {
    if(ScreenTransitor) {
        if(Reponse) {
            TransitorScreen(reponsebonne = true, onClick)
        } else {
            TransitorScreen(reponsebonne = false, onClick)
        }
    } else {
        if (Reponse) {
            GenererQuestion(onClick)
            NombreQuestion += 1
        } else if (!Reponse) {
            NombreDeVies = NombreDeVies - 1
            if (NombreDeVies == 0) {

            } else {
                GenererQuestion(onClick)

            }
        }
    }

}
@Composable
fun TransitorScreen(reponsebonne: Boolean, onClick: () -> Unit) {
    var ID = R.drawable.pouce_haut
    if( reponsebonne == false){
        ID = R.drawable.pouce_baisee
    }
    Column(
        modifier = Modifier
            .padding(top = 150.dp)
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
                painter = painterResource(id = ID),
                contentDescription = "fermer", // Indiquez une description si nécessaire
                modifier = Modifier.size(250.dp) // Ajuster la taille de l'image selon vos besoins
            )
        }

        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 75.dp)
                .background(color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp))
                .border(5.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .clickable(onClick = {
                    onClick()
                    ScreenTransitor = false
                })


        ) {
            Text(
                text = " Question suivante",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontFamily = fontPrincipale
            )
        }
    }
}


@Composable
fun GenererQuestion(onClick: () -> Unit) {

    val qcm = questionGeneration()
    if (qcm != null) {

        if(qcm.type == "qcm_4") { QuestionChoixMultiple(onClick, qcm) }
        if(qcm.type == "vf") { QuestionVraiFaux(onClick, qcm) }
        if(qcm.type == "input") { QuestionText(onClick, qcm) }


    }
}



// ACCES BD

suspend fun questionFromDatabase(theme: String, randomNumber: Int): QCM? {  //rajoute une variable en fonction du type
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

suspend fun getQuestionCategorySize(theme: String): Int {
    return suspendCancellableCoroutine { continuation ->
        val questionsRef = databaseGlobal.getReference("questions/$theme")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categorySize = dataSnapshot.childrenCount
                continuation.resume(categorySize.toInt())
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

fun RandomCategorie(): String {
    val categories = listOf(
        "Histoire", "Géographie", "Musique", "Cinéma","Littérature", "Autres arts",
        "Sport", "Jeux vidéo", "Sciences"
    )

    val randomCategoryIndex = (0 until categories.size).random()  // Génère un index aléatoire dans la plage des indices de la liste
    val randomCategory = removeAccentsAndUpperCase(categories[randomCategoryIndex]) // Récupère la catégorie correspondante à l'index aléatoire

    return randomCategory
}



@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun questionGeneration(): QCM? {
    val randomIndex = remember { Random.nextInt(QuestionIndexList.value.size - 1) }
    val randomQuestionIndex = remember { QuestionIndexList.value[randomIndex] }
    val qcmState = remember { mutableStateOf<QCM?>(null) }

    LaunchedEffect(randomIndex) {
        QuestionIndexList.value.removeAt(randomIndex)
        var question : QCM?

        if (categorie != "tout_theme") {
            question = questionFromDatabase(categorie, randomQuestionIndex)

        } else {
            val randomcategory = RandomCategorie()
            question = questionFromDatabase(randomcategory, randomQuestionIndex)
        }
        qcmState.value = question

    }

    val qcm = qcmState.value


    return qcm
}

/*
###############################################
#          TYPE DE QUESTIONS                  #
###############################################
*/


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun QuestionChoixMultiple(onClick: () -> Unit, qcm: QCM ) {
        val liste = qcm.answers.shuffled()
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



@Composable
fun QuestionVraiFaux(onClick: () -> Unit, qcm: QCM) {

        val liste = qcm.answers
        val Questiontxt = qcm.question

        PanneauQuestion(300, 350, Questiontxt)
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

                BoutonQCM(liste[0].answer, onClick, liste[0].isRight)
                BoutonQCM(liste[1].answer, onClick, liste[1].isRight)


                // Autres éléments que vous souhaitez inclure dans la rangée
            }
        }

    }

fun textverification(user_answer : String, list_answers : List<Answer>): Boolean {

    val answer = user_answer.lowercase()
    for (answerItem in list_answers) {
        // Vérifier si la réponse de l'utilisateur correspond à la réponse actuelle de la liste
        if (answer == answerItem.answer.lowercase()) {
            // Si une correspondance est trouvée, mettre reponse à vrai et sortir de la boucle
            return true
        }
    }
    // Si aucune correspondance n'est trouvée, mettre reponse à faux
    return false


}

@Composable
fun QuestionText(onClick: () -> Unit, qcm : QCM) {

    val liste = qcm.answers
    val Questiontxt = qcm.question

    var textValue by remember { mutableStateOf("") }

    PanneauQuestion(Hauteur = 300, Largeur = 350, Questiontxt)
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
            BoutonQCM("Envoyer", onClick, textverification(textValue,liste))
        }
    }
}

/*
##################################################
#          GENERATION DU MENU DE FIN DE PARTIE   #
##################################################
*/
@SuppressLint("UnrememberedMutableState")
@Composable
fun GameOver(
    navController : NavHostController,
    viewModel: GameViewModel
) {
    QuestionIndexList = mutableStateOf( IntArray(20)  { it }.toMutableList())
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
                        text = "${NombreQuestion*5} coins gagnés",
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
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable(onClick = {
                        navController.navigate(RootScreen.Home.route);
                        viewModel.changeAnyAtomicV2(
                            game_played = user!!.game_played + 1,
                            //multiplicateur de money en fonction de difficulté ??
                            money = user!!.money + NombreQuestion*5
                        )
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


/*
##################################################
#                 MODE DE JEU                    #
##################################################
*/

@Composable
fun Global(
    navController : NavHostController,
    viewModel: GameViewModel
){


    var currentContent by remember { mutableStateOf(0) }

    LaunchedEffect(currentContent){
        if (NombreQuestion == 0) {
            QuestionIndexList =  mutableStateOf(IntArray(getQuestionCategorySize(categorie))  { it }.toMutableList())
            println("renitialisation")
        }
        println(QuestionIndexList)
    }

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


            if ((time_spend == 1500 || time_spend == 1501) && ScreenTransitor == false){
                println("plus de temps")
                Reponse = false
                ScreenTransitor = true
                currentContent = 1 - currentContent
            }


        ProgressBar(NombreDeVies, NombreQuestion) } else { GameOver(navController, viewModel)
        currentContent = 2}




    }
    if (gameMode == "Multi") {
        // Button to change the currentContent state
        // Display content based on currentContent state
        when (currentContent) {
            0 -> ChangerQuestion(onClick = { currentContent = 1 - currentContent })

            1 -> ChangerQuestion(onClick = { currentContent = 1 - currentContent })
            // Add more cases as needed
        }
        ProgressBar(NombreDeVies, NombreQuestion)


    }
    if (gameMode == "Duel") {


    }
}





@Composable
fun QuestionScreen(
    state: QuestionState,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val viewModel = GameViewModel()

    /*empeche le retour en arrière*/
    BackHandler(enabled = true) {
        // Ici, vous pouvez ajouter une logique pour décider quand et comment empêcher le retour
        // Laisser ce bloc vide empêchera le retour arrière
        Timber.d("retour empêché")
    }

    Background()
    Global(navController, viewModel)

}