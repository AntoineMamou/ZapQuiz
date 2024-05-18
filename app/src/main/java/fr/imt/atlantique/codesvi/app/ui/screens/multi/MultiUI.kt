package fr.imt.atlantique.codesvi.app.ui.screens.multi

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ServerValue
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.snapshots
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.Answer
import fr.imt.atlantique.codesvi.app.data.model.Game
import fr.imt.atlantique.codesvi.app.data.model.Player
import fr.imt.atlantique.codesvi.app.data.model.QCM
import fr.imt.atlantique.codesvi.app.data.model.User
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale
import fr.imt.atlantique.codesvi.app.ui.screens.question.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber


@Composable
fun ScreenChanger(
    usersScores: List<Pair<User, Int>>,
    nextScreen: String,
    current: String,
    navController: NavHostController
) {
    var currentScreen by remember { mutableStateOf(current) }
    val users = usersScores.map { it.first }
    // Utilisation de LaunchedEffect pour changer l'écran après 5 secondes
    LaunchedEffect(true) {
        delay(5000) // Délai de 5000 ms (5 secondes)
        currentScreen = nextScreen // Changer l'écran
    }

    // Afficher le contenu en fonction de l'écran actuel
    when (currentScreen) {
        "Home" -> Home(users)
        "nextRound" -> WaitingRound(1,usersScores,navController)

    }
}





@Composable
fun WaitingRound(
    roundNumber: Int,
    usersScores: List<Pair<User, Int>>,
    navController: NavHostController
) {
    var currentScreen by remember { mutableStateOf("nextRound") }
    // Utilisation de LaunchedEffect pour changer l'écran après 5 secondes
    LaunchedEffect(true) {
        delay(5000) // Délai de 5000 ms (5 secondes)
        currentScreen = "Question" // Changer l'écran
    }

    // Afficher le contenu en fonction de l'écran actuel
    when (currentScreen) {
        "nextRound" -> Round(roundNumber, usersScores)
        "Question" -> PlayQuestion(usersScores,navController)

    }
}

@Composable
fun PlayQuestion(usersScores: List<Pair<User, Int>>, navController: NavHostController) {
    navController.navigate(HomeRootScreen.Question.route);
    StartQuestion("Histoire", "Multi", R.drawable.multi)
}

@Composable
fun Round(roundNumber: Int, usersScores: List<Pair<User, Int>>) {

    val sortedScores = usersScores.sortedByDescending { it.second }
    Column(verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Manche $roundNumber",
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = fontPrincipale
        )
        sortedScores.forEach { x ->
            DisplayScore(x.first,x.second)
        }
    }
}



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

            /*Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,) {
                Text(
                    text = user.trophies.toString(),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontFamily = fontPrincipale,
                    modifier = Modifier.padding(end = 10.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.couronne_laurier),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }*/


        }

        //Spacer(modifier = Modifier.height(32.dp)) // Espacement vertical uniforme
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


class MultiViewModel : ViewModel() {
    private val _gameState = MutableStateFlow("waiting")  // Initial state
    val gameState: StateFlow<String> = _gameState.asStateFlow()


    private val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    private val waitingRoomRef = database.getReference("waiting_room")


    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private var isUserAddedToWaitingRoom = false

    private val _gameId = MutableStateFlow<String?>(null)
    val gameId: StateFlow<String?> = _gameId.asStateFlow()

    private val _gameStarted = MutableStateFlow(false) // État initial de la partie


    /*init {
        checkWaitingRoom()
    }*/

    fun loadUser(username: String?) {
        viewModelScope.launch {
            username?.let {
                val user = getUserInfoDatabase(it)
                _userState.value = user
            }
        }
    }


    fun startNewGame(players: List<User>) {
        val gameIdValue = waitingRoomRef.push().key ?: return
        _gameId.value = gameIdValue
        val gamePlayers = players.map { Player(it, 0) }
        val initialRound = 1
        val questions = generateQuestions()
        println(questions)
        val newGame = Game(gameIdValue, gamePlayers, initialRound, questions, gameState = "waiting")
        val gameRef = database.getReference("games").child(gameIdValue)

        gameRef.setValue(newGame)
            .addOnSuccessListener {
                println("New game started successfully with ID $gameIdValue")
                gameRef.child("gameState")
                    .setValue("startGame")  // Mise à jour de l'état du jeu dans Firebase
                broadcastGameStarted(gameIdValue)

            }
            .addOnFailureListener {
                println("Failed to start new game")
            }

    }

    private fun broadcastGameStarted(gameId: String) {
        waitingRoomRef.child("gameStarted").setValue(true)
        waitingRoomRef.child("gameId").setValue(gameId)
    }

    fun listenForGameStart() {
        waitingRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameStarted = snapshot.child("gameStarted").getValue(Boolean::class.java) ?: false
                if (gameStarted) {
                    val gameId = snapshot.child("gameId").getValue(String::class.java)
                    if (gameId != null) {
                        _gameId.value = gameId  // Sauvegarde l'ID de la partie pour tous les joueurs
                        _gameState.value = "startGame"
                        waitingRoomRef.removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Failed to read game start info: ${error.message}")
            }
        })
    }

    private fun generateQuestions(): List<QCM> {
        // Implémentez la logique pour générer ou sélectionner des questions
        return listOf(
            QCM(answers=listOf(Answer(isRight=true, answer="Qin Shi Huang"), Answer(isRight=false, answer="Qin Shi Huangdi"), Answer(isRight=false, answer="Liu Bang"), Answer(isRight=false, answer="Han Wudi")), id="histoire_17", type="qcm_4", category="histoire", level=5, question="Qui était le premier empereur de Chine ?", image="", gap=0F, explanation=""),

        QCM(answers=listOf(Answer(isRight=true, answer="Louis-Napoléon Bonaparte"), Answer(isRight=false, answer="Adolphe Thiers"), Answer(isRight=false, answer="Jules Grévy"), Answer(isRight=false, answer="Louis-Philippe")), id="histoire_34", type="qcm_4", category="histoire", level=4, question="Qui a été élu président de la République française lors de la première élection présidentielle en 1848 ?", image="", gap=0F, explanation=""),

        QCM(answers=listOf(Answer(isRight=false, answer="Auguste"), Answer(isRight=false, answer="Néron"), Answer(isRight=true, answer="Vespasien"), Answer(isRight=false, answer="Trajan")), id="histoire_2", type="qcm_4", category="histoire", level=4, question="Quel empereur romain a ordonné la construction du Colisée ?", image="", gap=0F, explanation=""),

        QCM(answers=listOf(Answer(isRight=true, answer="États-Unis"), Answer(isRight=false, answer="Canada"), Answer(isRight=false, answer="Australie"), Answer(isRight=false, answer="Argentine")), id="geographie_17", type="qcm_4", category="géographie", level=2, question="Dans quel pays se trouve le parc national de Yellowstone ?", image="", gap=0F, explanation=""),

        QCM(answers=listOf(Answer(isRight=false, answer="Lac Supérieur"), Answer(isRight=false, answer="Lac Victoria"), Answer(isRight=true, answer="Lac Baïkal"), Answer(isRight=false, answer="Lac Tanganyika")), id="geographie_19", type="qcm_4", category="géographie", level=5, question="Quel est le plus grand lac du monde en volume d'eau ?", image="", gap=0F, explanation=""),

            QCM(answers=listOf(Answer(isRight=true, answer="Qin Shi Huang"), Answer(isRight=false, answer="Qin Shi Huangdi"), Answer(isRight=false, answer="Liu Bang"), Answer(isRight=false, answer="Han Wudi")), id="histoire_17", type="qcm_4", category="histoire", level=5, question="Qui était le premier empereur de Chine ?", image="", gap=0F, explanation=""),

            QCM(answers=listOf(Answer(isRight=true, answer="Louis-Napoléon Bonaparte"), Answer(isRight=false, answer="Adolphe Thiers"), Answer(isRight=false, answer="Jules Grévy"), Answer(isRight=false, answer="Louis-Philippe")), id="histoire_34", type="qcm_4", category="histoire", level=4, question="Qui a été élu président de la République française lors de la première élection présidentielle en 1848 ?", image="", gap=0F, explanation=""),

            QCM(answers=listOf(Answer(isRight=false, answer="Auguste"), Answer(isRight=false, answer="Néron"), Answer(isRight=true, answer="Vespasien"), Answer(isRight=false, answer="Trajan")), id="histoire_2", type="qcm_4", category="histoire", level=4, question="Quel empereur romain a ordonné la construction du Colisée ?", image="", gap=0F, explanation=""),

            QCM(answers=listOf(Answer(isRight=true, answer="États-Unis"), Answer(isRight=false, answer="Canada"), Answer(isRight=false, answer="Australie"), Answer(isRight=false, answer="Argentine")), id="geographie_17", type="qcm_4", category="géographie", level=2, question="Dans quel pays se trouve le parc national de Yellowstone ?", image="", gap=0F, explanation=""),

            QCM(answers=listOf(Answer(isRight=false, answer="Lac Supérieur"), Answer(isRight=false, answer="Lac Victoria"), Answer(isRight=true, answer="Lac Baïkal"), Answer(isRight=false, answer="Lac Tanganyika")), id="geographie_19", type="qcm_4", category="géographie", level=5, question="Quel est le plus grand lac du monde en volume d'eau ?", image="", gap=0F, explanation="")

        )
    }

    private var waitingRoomListener: ValueEventListener? = null

    private fun checkWaitingRoom() {
        // Assurez-vous qu'il n'y a pas déjà un listener actif
        waitingRoomListener?.let {
            waitingRoomRef.removeEventListener(it)
        }

        waitingRoomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.child("user").children.mapNotNull { it.getValue(User::class.java) }
                if (users.size == 2) { // Vérifier si le joueur actuel est le deuxième à rejoindre
                    if (isUserAddedToWaitingRoom && !snapshot.hasChild("gameStarted")) {
                        // Vérifier si le jeu n'a pas déjà été démarré par un autre joueur
                        stopListeningToWaitingRoom()
                        waitingRoomRef.child("gameStarted").setValue(true)
                        waitingRoomRef.child("gameId").setValue(_gameId.value)
                        startNewGame(users)
                        _gameState.value = "startGame"
                        _gameStarted.value = true

                    }

                    // Supprimer le listener après l'utilisation
                    waitingRoomListener?.let { listener ->
                        waitingRoomRef.removeEventListener(listener)
                        waitingRoomListener = null
                    }
                }
                else{
                    waitingRoomListener?.let { listener ->
                        waitingRoomRef.removeEventListener(listener)
                        waitingRoomListener = null
                    }
                    listenForGameStart()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error reading waiting room: ${error.message}")
            }
        }.also { listener ->
            waitingRoomRef.addValueEventListener(listener)
        }
    }

    fun stopListeningToWaitingRoom() {
        // Méthode pour arrêter manuellement d'écouter la salle d'attente
        waitingRoomListener?.let {
            waitingRoomRef.removeEventListener(it)
            waitingRoomListener = null
        }
    }

    fun addUserToWaitingRoom(user:User) {
        println(isUserAddedToWaitingRoom)
        if (!isUserAddedToWaitingRoom && !_gameStarted.value) {
            isUserAddedToWaitingRoom = true
            val key = waitingRoomRef.child("user").push().key
            if (key != null) {
                waitingRoomRef.child("user").child(key).setValue(user)
                    .addOnSuccessListener {

                        println("User added to waiting room successfully")
                        checkWaitingRoom()
                        //listenForGameStart()

                    }
                    .addOnFailureListener {
                        println("Failed to add user to waiting room")
                        isUserAddedToWaitingRoom = false
                    }
            }
        } else {
            println("User already added to the waiting room or game has started")
        }
    }

    fun leaveWaitingRoom(user: User, navController: NavHostController) {
        if (!_gameStarted.value) { // Vérifie si le jeu n'a pas commencé
            waitingRoomRef.child("user").orderByChild("username").equalTo(user.username).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValue() // Supprime l'utilisateur de la salle d'attente
                    }
                    //isUserAddedToWaitingRoom = false
                    navController.navigate(HomeRootScreen.Game.route)
                    println("User removed from waiting room successfully")
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e("Failed to remove user from waiting room: ${error.message}")
                }
            })
        } else {
            println("Cannot leave the waiting room: game has started")
        }
    }

    fun resetGame() {
        _gameStarted.value = false
        isUserAddedToWaitingRoom = false
        _gameState.value = "waiting"


    }



}

class InGameViewModel() : ViewModel() {
    private val db = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")

    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    private val _questions = MutableLiveData<List<QCM>>()
    val questions: LiveData<List<QCM>> = _questions

    var nbQuestion = 0

    private val _questionState = MutableStateFlow(true)  // Initial state
    val questionState: StateFlow<Boolean> = _questionState.asStateFlow()

    private val _round = MutableLiveData<Int>()
    val round: LiveData<Int> = _round

    val displayFunction = MutableStateFlow(false)

    private var alreadyAnswered = false


    fun updateQuestionState(){
        _questionState.value = !questionState.value
    }

    fun fetchPlayers(gameId: String) {
        val gameRef = db.getReference("games/$gameId")
        gameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(Game::class.java)
                game?.players?.let {
                    _players.value = it
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("InGameViewModel", "Failed to fetch game data: ${error.message}")
            }
        })
    }

    fun fetchRound(gameId: String){
        val gameRef = db.getReference("games").child(gameId).child("round")
        gameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentRound = snapshot.getValue(Int::class.java)
                if (currentRound != null) {
                    _round.postValue(currentRound!!)
                } else {
                    _round.postValue(1) // Définir une valeur par défaut si rien n'est trouvé
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error fetching game round: ${error.message}")
            }
        })
    }



    fun fetchQuestions(gameId: String){
        val questionsRef = db.getReference("games/$gameId/questions")
        questionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fetchedQuestions = mutableListOf<QCM>()
                for (questionSnapshot in dataSnapshot.children) {

                        val id = questionSnapshot.child("id").getValue(String::class.java)
                        val type = questionSnapshot.child("type").getValue(String::class.java)
                        val category =
                            questionSnapshot.child("category").getValue(String::class.java)
                        val level = questionSnapshot.child("level").getValue(Int::class.java)
                        val question2 =
                            questionSnapshot.child("question").getValue(String::class.java)
                        val image = questionSnapshot.child("image").getValue(String::class.java)
                        val gap = questionSnapshot.child("gap").getValue(Float::class.java)
                        val explanation =
                            questionSnapshot.child("explanation").getValue(String::class.java)

                        // Récupération de la liste des réponses
                        val answersList = ArrayList<Answer>()
                        val answersSnapshot = questionSnapshot.child("answers")
                        for (answerSnapshot in answersSnapshot.children) {
                            val answer = answerSnapshot.child("answer").getValue(String::class.java)
                            val isRight =
                                answerSnapshot.child("right").getValue(Boolean::class.java)
                            if (answer != null && isRight != null) {
                                val reponse = Answer(isRight, answer)
                                answersList.add(reponse)

                            }
                        }

                        // Création de l'objet QCM avec les données récupérées
                        val question = QCM(
                            answersList,
                            id ?: "",
                            type ?: "",
                            category ?: "",
                            level ?: 0,
                            question2 ?: "",
                            image ?: "",
                            gap ?: 0F,
                            explanation ?: ""
                        )
                    fetchedQuestions.add(question)
                }
                _questions.value = fetchedQuestions
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun UpdateWaitingAnswerPlayer(gameId: String, number : Int){
        db.getReference("games").child(gameId).child("WaitingAnswerPlayer").setValue(number)
    }

    fun decrementWaitingAnswerPlayer(gameId: String) {
        val waitingPlayerRef = db.getReference("games").child(gameId).child("WaitingAnswerPlayer")

        // Démarre une transaction pour lire et écrire la valeur de façon atomique.
        waitingPlayerRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Int::class.java)
                if (currentValue == null) {
                    mutableData.value = 0 // Initialise à 0 si la valeur n'est pas définie.
                } else if (currentValue > 0) {
                    mutableData.value = currentValue - 1
                    if (mutableData.value == 0){
                        //setFutureGameStartTime(gameId,5000)
                    }
                }
                // Si la valeur actuelle est déjà à 0 ou moins, elle n'est pas changée.
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?) {
                if (committed) {
                    println("Transaction réussie : WaitingAnswerPlayer décrémenté")
                } else {
                    println("Transaction annulée, raison : ${databaseError?.message}")
                }
            }
        })
    }



    fun monitorGameStartTimeAndStart(gameId: String) {
        val startTimeRef = db.getReference("games").child(gameId).child("gameStartTime")

        startTimeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val startTime = snapshot.getValue(Long::class.java)
                if (startTime != null) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime >= startTime) {
                        startGame()
                        startTimeRef.removeEventListener(this) // Clean up listener
                    } else {
                        // Wait until the startTime to start the game
                        Handler(Looper.getMainLooper()).postDelayed({
                            startGame()
                        }, startTime - currentTime)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to listen for game start time: ${error.message}")
            }
        })
    }



    fun monitorWaitingAnswerPlayer(gameId: String) {
        val waitingPlayerRef = db.getReference("games").child(gameId).child("WaitingAnswerPlayer")

        // Créez un ValueEventListener
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(Int::class.java) ?: 0 // Utilisez 0 comme valeur par défaut si non définie

                if (value > 0) {
                    displayFunction.value = false
                    println("La valeur de WaitingAnswerPlayer est positive: $value")
                    // Ajoutez ici tout comportement supplémentaire nécessaire lorsque la valeur est positive
                } else {
                    println("La valeur de WaitingAnswerPlayer est nulle ou moins.")
                    // Comportement lorsque la valeur est nulle ou moins
                    // Supprime l'écouteur si la valeur est nulle ou moins

                    waitingPlayerRef.removeEventListener(this)
                    displayFunction.value = true
                    //monitorGameStartTimeAndStart(gameId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Écoute annulée pour WaitingAnswerPlayer, raison : ${databaseError.message}")
            }
        }

        // Attribuez le listener à la référence
        waitingPlayerRef.addValueEventListener(listener)
    }


    fun submitAnswer(gameId: String, username: String, correctAnswer: Boolean) {
        if(!alreadyAnswered) {
            alreadyAnswered=true
            val gameRef = db.getReference("games/$gameId")

            val playersRef = gameRef.child("players")

//      Supposons que vous cherchez l'utilisateur avec le pseudo "Arno2"
            val usernameQuery = playersRef.orderByChild("user/username").equalTo(username)
            println("submit :$correctAnswer")
            if (correctAnswer) {
                usernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (playerSnapshot in snapshot.children) {
                                val playerId =
                                    playerSnapshot.key // Récupère l'ID du joueur, e.g., "0"
                                val currentscore =
                                    playerSnapshot.child("score").getValue(Int::class.java) ?: 0
                                if (playerId != null) {
                                    playersRef.child(playerId).child("score")
                                        .setValue(currentscore + (questions.value!![nbQuestion].level * 4))
                                        .addOnSuccessListener {
                                            println("Score mis à jour avec succès pour le joueur $playerId")
                                        }
                                        .addOnFailureListener {
                                            println("Échec de la mise à jour du score pour le joueur $playerId: ${it.message}")
                                        }
                                }


                            }
                        } else {
                            println("Aucun joueur trouvé avec ce pseudo.")
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Timber.tag("InGameViewModel").e("Failed to update score: %s", error.message)
                    }
                })
            }

            if(nbQuestion == 4) {
                monitorWaitingAnswerPlayer(gameId)
                decrementWaitingAnswerPlayer(gameId)
            }
            else{
                nbQuestion+=1
                _questionState.value=false
                alreadyAnswered=false
            }

        }
    }

    fun startGame() {
        viewModelScope.launch {
            delay(5000) // Délai de 5 secondes
            displayFunction.value = true
        }
    }


}

@Composable
fun MultiScreen(
    state: MultiState,
    modifier: Modifier = Modifier,
    navController: NavHostController) {
    println("ok")
    val viewModel : MultiViewModel = viewModel()
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
        "waiting" -> DisplayWaitingRoom(viewModel,user,navController)
        "startGame" -> (viewModel.gameId.collectAsState().value?.let { StartGame(navController, it,user) })
    }
}

@Composable
fun DisplayWaitingRoom(viewModel: MultiViewModel, user: User?, navController: NavHostController) {
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


        if (score>0) {
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
fun DisplayQuestion(gameViewModel: InGameViewModel, gameId: String, qcm: QCM, user: User?) {
    val liste = qcm.answers.shuffled()
    val Questiontxt = qcm.question

    gameViewModel.UpdateWaitingAnswerPlayer(gameId,2)

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
fun ShowQuestion(gameViewModel: InGameViewModel, gameId: String, user: User?){
    val questionState by gameViewModel.questionState.collectAsState()
    Background()
    when(questionState) {
        true -> DisplayQuestion(
            gameViewModel,
            gameId,
            qcm = gameViewModel.questions.value!![gameViewModel.nbQuestion],
            user
        )

        false -> {gameViewModel.updateQuestionState();ShowQuestion(gameViewModel, gameId, user)}
    }
}

@Composable
fun StartGame(navController: NavHostController, gameId: String, user: User?) {

    val gameModel : InGameViewModel = viewModel()

    val players = gameModel.players.observeAsState(initial = emptyList())
    val currentRound = gameModel.round.observeAsState()

    val shouldDisplayFunction = gameModel.displayFunction.collectAsState().value

    if (shouldDisplayFunction) {
        ShowQuestion(gameModel,gameId,user)  // Appelle la fonction DisplayFunction si l'état est vrai
    } else {
        Background()

        LaunchedEffect(gameId) {
            gameModel.fetchPlayers(gameId)
            gameModel.fetchQuestions(gameId)
            gameModel.fetchRound(gameId)
            gameModel.startGame()
        }

        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Round n° : ${currentRound.value}", fontWeight = FontWeight.Normal,
                fontSize = 24.sp, color = Color.White, fontFamily = fontPrincipale)
            players.value.forEach { player ->
                DisplayScore(player.user,player.score)
            }
        } // Sinon, continue d'afficher l'interface utilisateur actuelle du jeu
    }
}