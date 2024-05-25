package fr.imt.atlantique.codesvi.app.ui.screens.duel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.DuelViewModel
import fr.imt.atlantique.codesvi.app.data.model.GameViewModel
import fr.imt.atlantique.codesvi.app.data.model.InDuelGame
import fr.imt.atlantique.codesvi.app.data.model.QCM
import fr.imt.atlantique.codesvi.app.data.model.User
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.game.currentIndex
import fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale

import fr.imt.atlantique.codesvi.app.ui.screens.question.*

import fr.imt.atlantique.codesvi.app.ui.screens.question.Background
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import javax.inject.Scope


@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeComponent(user:User){

    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(15.dp)
            )
            .fillMaxWidth()
            .height(100.dp)
            .border(
                5.dp,
                color = MaterialTheme.colorScheme.primary,
                RoundedCornerShape(15.dp)
            )
    ) {


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 5.dp)
                .width(100.dp)
        ) {


            Image(
                painter = painterResource(id = user.getImageResourceId(context)),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.username,
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = fontPrincipale
            )
        }

        Text(
            text = user.title,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = fontPrincipale
        )
    }
}


@Composable
fun Home(users: List<User>) {
    Column(verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        HomeComponent(user = users[0])
        HomeComponent(user = users[1])
        HomeComponent(user = users[2])
        HomeComponent(user = users[3])
        HomeComponent(user = users[4])
    }
}


@Composable
fun DisplayWaitingRoom(viewModel: DuelViewModel, user: User?, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Background()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "En attente d'autres joueurs...", fontWeight = FontWeight.Normal,
                fontSize = 24.sp, color = Color.White, fontFamily = fontPrincipale
            )
            Spacer(modifier = Modifier.height(5.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    //.padding(bottom = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        RoundedCornerShape(15.dp)
                    )
                    .border(
                        5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(15.dp)
                    )
                    .clickable(onClick = {
                        if (user != null) {
                            viewModel.leaveWaitingRoom(user, navController);


                        }
                    })

            ){
                Text(text = "Annuler",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontFamily = fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale
                )
            }
        }
    }
}

@Composable
fun DisplayScore(user: User,score : Int) {

    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(15.dp)
            )
            .width(300.dp)
            .height(100.dp)
            .border(
                5.dp,
                color = MaterialTheme.colorScheme.primary,
                RoundedCornerShape(15.dp)
            )
    ) {

        Image(
            painter = painterResource(id = user.getImageResourceId(context)),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = user.username,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = fontPrincipale
        )


        if (score==0) {
            Text(
                text = "$score point",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = fontPrincipale
            )
        }
        else{
            Text(
                text = "$score points",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = fontPrincipale
            )
        }
    }
}


@Composable
fun BoutonQCMMulti(textbouton: String, onClick: () -> Unit, reponsebonne: Boolean) {

    Box(
        modifier = Modifier
            .padding(vertical = 15.dp, horizontal = 30.dp)
            .size(140.dp, 80.dp)
            .background(color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp))
            .border(5.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            .clickable(onClick = {
                onClick()
            })


    ) {
        Text(
            text = textbouton,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            fontFamily = fr.imt.atlantique.codesvi.app.ui.screens.question.fontPrincipale
        )
    }
}

@Composable
fun DisplayQuestion(gameViewModel: InDuelGame, gameId: String, qcm: QCM, user: User?) {
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
            BoutonQCMMulti(liste[0].answer, onClick = { user?.let {
                gameViewModel.submitAnswer(gameId,
                    it.username,liste[0].isRight)
            } }, liste[0].isRight)
            BoutonQCMMulti(liste[1].answer, { user?.let {
                gameViewModel.submitAnswer(gameId,
                    it.username,liste[1].isRight)
            } }, liste[1].isRight)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BoutonQCMMulti(liste[2].answer, { user?.let {
                gameViewModel.submitAnswer(gameId,
                    it.username,liste[2].isRight)
            } }, liste[2].isRight)
            BoutonQCMMulti(liste[3].answer, { user?.let {
                gameViewModel.submitAnswer(gameId,
                    it.username,liste[3].isRight)
            } }, liste[3].isRight)
        }
    }
}


@Composable
fun DisplayQuestionVF(gameViewModel: InDuelGame, gameId: String, qcm: QCM, user: User?) {
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
            BoutonQCMMulti(liste[0].answer, onClick = { user?.let {
                gameViewModel.submitAnswer(gameId,
                    it.username,liste[0].isRight)
            } }, liste[0].isRight)
            BoutonQCMMulti(liste[1].answer, { user?.let {
                gameViewModel.submitAnswer(gameId,
                    it.username,liste[1].isRight)
            } }, liste[1].isRight)
        }

    }
}

@Composable
fun DisplayQuestionInput(gameViewModel: InDuelGame, gameId: String, qcm: QCM, user: User?) {
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
                    fontFamily = fr.imt.atlantique.codesvi.app.ui.screens.question.fontPrincipale, // Définir la police du texte

                )

                // Définir la couleur de fond en blanc
            )

        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            BoutonQCMMulti("Envoyer",{ user?.let {
                gameViewModel.submitAnswer(gameId,
                    it.username,textverification(textValue,liste, qcm.gap))
            } } , textverification(textValue,liste, qcm.gap))
        }
    }
}

@Composable
fun ShowQuestion(gameViewModel: InDuelGame, gameId: String, user: User?){
    val questionState by gameViewModel.questionState.collectAsState()
    Background()
    when(questionState) {
        true ->

            if (gameViewModel.questions.value!![gameViewModel.nbQuestion].type == "qcm_4"){
                DisplayQuestion(
                    gameViewModel,
                    gameId,
                    qcm = gameViewModel.questions.value!![gameViewModel.nbQuestion],
                    user
                )
            }
            else{
                if(gameViewModel.questions.value!![gameViewModel.nbQuestion].type == "vf"){
                    DisplayQuestionVF(gameViewModel,
                        gameId,
                        qcm = gameViewModel.questions.value!![gameViewModel.nbQuestion],
                        user)
                }

                else{
                    DisplayQuestionInput(gameViewModel,
                        gameId,
                        qcm = gameViewModel.questions.value!![gameViewModel.nbQuestion],
                        user)
                }

            }



        false -> {gameViewModel.updateQuestionState();ShowQuestion(gameViewModel, gameId, user)}
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun StartGame(navController: NavHostController, gameId: String, user: User?) {

    val gameModel: InDuelGame = viewModel()

    val players = gameModel.players.observeAsState(initial = emptyList())
    val currentRound = gameModel.round.collectAsState().value

    val shouldDisplayFunction = gameModel.displayFunction.collectAsState().value
    val endGame = gameModel.endGame.collectAsState().value
    println("endgame : $endGame")
    println(shouldDisplayFunction)

    if(!endGame){
        if (shouldDisplayFunction) {
            ShowQuestion(
                gameModel,
                gameId,
                user
            )  // Appelle la fonction DisplayFunction si l'état est vrai
        } else {
            Background()

            LaunchedEffect(gameId) {
                gameModel.fetchPlayers(gameId)
                gameModel.fetchQuestions(gameId)
                //gameModel.fetchRound(gameId)
                gameModel.initWaitingPlayer(gameId)
                gameModel.startGame()
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Round n° : ${currentRound}", fontWeight = FontWeight.Normal,
                    fontSize = 24.sp, color = Color.White, fontFamily = fontPrincipale
                )
                players.value.forEach { player ->
                    DisplayScore(player.user, player.score)
                }
            } // Sinon, continue d'afficher l'interface utilisateur actuelle du jeu


        }
    }
    else{

        val gameResult = gameModel.gameStat
        val updateModel : GameViewModel = viewModel()
        val userState = updateModel.userState.collectAsState().value
        if (gameResult != null) {
            LaunchedEffect(Unit){
                updateModel.loadUser(gameResult.username)

                updateModel.loadUserAndUpdate(gameResult.username,trophies=  gameResult.trophies, money =  gameResult.coins)

            }

        }




        gameResult?.let {
            EndGameScreen(navController= navController,
                imageUrl= R.drawable.victoire,
                result = it.result,
                score=gameModel.score,
                trophies=it.trophies,
                coins=it.coins
            )
        }
    }
}


@Composable
fun EndGameScreen(
    navController: NavHostController,
    imageUrl: Int,
    result : String,
    score: Int,
    trophies: Int,
    coins: Int
) {
    val windowWidth = 250.dp
    val windowHeight = 350.dp

    Background()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(windowWidth, windowHeight)
                .border(5.dp, colorResource(id = R.color.yellow_1), RoundedCornerShape(15.dp))
                .background(colorResource(id = R.color.blue_2), RoundedCornerShape(15.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .padding(bottom = 5.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = result + "!",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontPrincipale,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Score: $score",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = fontPrincipale,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = if (trophies >= 0) "Trophées: + $trophies" else "Trophées: $trophies",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontFamily = fontPrincipale,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.couronne_laurier), // Remplacez par votre ressource d'image pour les trophées
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pièces: $coins ",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontFamily = fontPrincipale,
                        fontWeight = FontWeight.Medium,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.piece), // Remplacez par votre ressource d'image pour les pièces
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .background(color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(15.dp))
                .border(5.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(15.dp))
                .clickable(onClick = { navController.navigate(HomeRootScreen.Game.route) }) // Adjust the navigation route as needed
        ) {
            Text(
                text = "Retourner au menu",
                color = Color.White,
                fontSize = 25.sp,
                fontFamily = fontPrincipale
            )
        }
    }
}



@Composable
fun DuelScreen(
    state: DuelState,
    modifier: Modifier = Modifier,
    navController: NavHostController ) {
        val viewModel: DuelViewModel = viewModel()
        val gameState by viewModel.gameState.collectAsState()

        val context = LocalContext.current
        val sharedPreferences: SharedPreferences by lazy {
            context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        }

        /*empeche le retour en arrière*/
        BackHandler(enabled = true) {
            // Ici, vous pouvez ajouter une logique pour décider quand et comment empêcher le retour
            // Laisser ce bloc vide empêchera le retour arrière
            Timber.d("retour empêché")
        }

        val username = sharedPreferences.getString("username", "")

        // Déclenchez le chargement de l'utilisateur lors du premier rendu ou lorsque le nom d'utilisateur change
        LaunchedEffect(username) {
            viewModel.loadUser(username)
        }

        val user = viewModel.userState.collectAsState().value

        if (user != null) {
            viewModel.addUserToWaitingRoom(user)
        }

        when (gameState) {
            "waiting" -> DisplayWaitingRoom(viewModel, user, navController)
            "startGame" -> (viewModel.gameId.collectAsState().value?.let {
                StartGame(
                    navController,
                    it,
                    user
                )
            })
        }
    }

