package fr.imt.atlantique.codesvi.app.ui.screens.multi

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.Game
import fr.imt.atlantique.codesvi.app.data.model.Player
import fr.imt.atlantique.codesvi.app.data.model.QCM
import fr.imt.atlantique.codesvi.app.data.model.User
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale
import fr.imt.atlantique.codesvi.app.ui.screens.question.Background
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

class MultiViewModel2 : ViewModel() {
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
                if (users.size == 2 && !_gameStarted.value) { // Vérifier si le joueur actuel est le deuxième à rejoindre
                    if (isUserAddedToWaitingRoom && !snapshot.hasChild("gameStarted")) {
                        // Vérifier si le jeu n'a pas déjà été démarré par un autre joueur
                        startNewGame(users)
                        waitingRoomRef.child("gameStarted").setValue(true)
                        waitingRoomRef.child("gameId").setValue(_gameId.value)
                        _gameState.value = "startGame"
                        _gameStarted.value = true
                        stopListeningToWaitingRoom()
                    }

                    // Supprimer le listener après l'utilisation
                    waitingRoomListener?.let { listener ->
                        waitingRoomRef.removeEventListener(listener)
                        waitingRoomListener = null
                    }
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

    fun addUserToWaitingRoom(user: User) {
        println(isUserAddedToWaitingRoom)
        if (!isUserAddedToWaitingRoom && !_gameStarted.value) {
            val key = waitingRoomRef.child("user").push().key
            if (key != null) {
                waitingRoomRef.child("user").child(key).setValue(user)
                    .addOnSuccessListener {
                        println("User added to waiting room successfully")
                        isUserAddedToWaitingRoom = true
                        checkWaitingRoom()
                        listenForGameStart()

                    }
                    .addOnFailureListener {
                        println("Failed to add user to waiting room")
                    }
            }
        } else {
            println("User already added to the waiting room or game has started")
        }
    }


    fun resetGame() {
        _gameStarted.value = false
        isUserAddedToWaitingRoom = false
        _gameState.value = "waiting"

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
            val key = waitingRoomRef.child("user").push().key
            if (key != null) {
                waitingRoomRef.child("user").child(key).setValue(user)
                    .addOnSuccessListener {
                        println("User added to waiting room successfully")
                        isUserAddedToWaitingRoom = true
                        checkWaitingRoom()
                        //listenForGameStart()

                    }
                    .addOnFailureListener {
                        println("Failed to add user to waiting room")
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

@Composable
fun MultiScreen2(
    state: MultiState,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    val context = LocalContext.current

    Background()

    val user1 = User("Antoine", "password", 1000, "lightning", "Zappeur professionnel", true, listOf(), 0, 0, 1000, "Histoire", 100, listOf())
    val user2 = User("Loic", "password", 420, "lightning", "Zappeur débutant", true, listOf(), 0, 0, 1000, "Histoire", 100, listOf())
    val user3 = User("Titouan", "password", 4000, "lightning", "Zappeur intermediaire", true, listOf(), 0, 0, 1000, "Histoire", 100, listOf())
    val user4 = User("Alexia", "password", 3000, "lightning", "Zappeur intermediaire", true, listOf(), 0, 0, 1000, "Histoire", 100, listOf())
    val user5 = User("Julien", "password", 600, "lightning", "Zappeur au top", true, listOf(), 0, 0, 1000, "Histoire", 100, listOf())
    
    //Home(listOf(user1, user2, user3,user4,user5))

    val pair1: Pair<User, Int> = Pair(user1, 10)
    val pair2: Pair<User, Int> = Pair(user2, 20)
    val pair3: Pair<User, Int> = Pair(user3, 190)
    val pair4: Pair<User, Int> = Pair(user4, 3)
    val pair5: Pair<User, Int> = Pair(user5, 0)

    ScreenChanger(usersScores= listOf(pair1,pair2,pair3,pair4,pair5),"nextRound","Home",navController)
}


class InGameViewModel() : ViewModel() {
    private val db = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")

    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    private val _questions = MutableLiveData<List<QCM>>()
    val questions: LiveData<List<QCM>> = _questions

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

    fun fetchQuestions(gameId: String) {
        val questionsRef = db.getReference("games/$gameId/questions")
        questionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fetchedQuestions = mutableListOf<QCM>()
                for (child in snapshot.children) {
                    val question = child.getValue(QCM::class.java)
                    question?.let { fetchedQuestions.add(it) }
                }
                _questions.value = fetchedQuestions
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("InGameViewModel", "Failed to fetch questions: ${error.message}")
            }
        })
    }

    fun submitAnswer(gameId: String, userId: String, answer: String) {
        val gameRef = db.getReference("games/$gameId")
        val userRef = gameRef.child("players").child(userId)
        val questionsRef = gameRef.child("questions")

        _questions.value?.let { questions ->
            // Fetch the current question based on your app logic (assuming the first question for simplicity)
            val currentQuestion = questions.firstOrNull()
            currentQuestion?.let { question ->
                // Check if the provided answer is correct
                val correctAnswer = question.answers.firstOrNull { it.answer == answer && it.isRight }

                if (correctAnswer != null) {
                    // If the answer is correct, update the score
                    userRef.child("score").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val currentScore = snapshot.getValue(Int::class.java) ?: 0
                            userRef.child("score").setValue(currentScore + 1)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("InGameViewModel", "Failed to update score: ${error.message}")
                        }
                    })
                } else {
                    // Optionally handle incorrect answer or provide feedback
                }

                // Optionally, navigate to the next question or update game state here
            }
        }
    }


}

@Composable
fun MultiScreen(
    state: MultiState,
    modifier: Modifier = Modifier,
    navController: NavHostController) {

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
        "startGame" -> (viewModel.gameId.collectAsState().value?.let { StartGame(navController, it) })
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
                            viewModel.leaveWaitingRoom(user,navController);


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
fun StartGame(navController: NavHostController, gameId : String) {
    Background()
    Text(gameId)
    val gameModel : InGameViewModel = viewModel()
    //Text(text = gameId)

    LaunchedEffect(gameId) {
        gameModel.fetchPlayers(gameId)
    }

    val players = gameModel.players.observeAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Players in Game", fontWeight = FontWeight.Normal,
            fontSize = 24.sp, color = Color.White, fontFamily = fontPrincipale)
        players.value.forEach { player ->
            DisplayScore(player.user,player.score)
        }
    }
}