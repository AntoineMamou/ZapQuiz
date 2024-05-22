package fr.imt.atlantique.codesvi.app.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class DuelViewModel : ViewModel() {
    private val _gameState = MutableStateFlow("waiting")  // Initial state
    val gameState: StateFlow<String> = _gameState.asStateFlow()


    private val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    private val waitingRoomRef = database.getReference("waiting_room_duel")


    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private var isUserAddedToWaitingRoom = false

    private val _gameId = MutableStateFlow<String?>(null)
    val gameId: StateFlow<String?> = _gameId.asStateFlow()

    private val _gameStarted = MutableStateFlow(false) // État initial de la partie



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
            waitingRoomRef.child("user").orderByChild("username").equalTo(user.username).addListenerForSingleValueEvent(object :
                ValueEventListener {
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