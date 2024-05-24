package fr.imt.atlantique.codesvi.app.data.model

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.question.QuestionIndexList
import fr.imt.atlantique.codesvi.app.ui.screens.question.RandomCategorie
import fr.imt.atlantique.codesvi.app.ui.screens.question.categorie
import fr.imt.atlantique.codesvi.app.ui.screens.question.databaseGlobal
import fr.imt.atlantique.codesvi.app.ui.screens.question.questionFromDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.question.questionGeneration
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

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

    private var questionToAdd : QCM? = null

    private val _questionList = MutableStateFlow<List<QCM>>(listOf())
    val questionList :StateFlow<List<QCM>> = _questionList.asStateFlow()

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


    suspend fun getQuestionFromDatabase2(theme: String, randomIndex: Int) {
        val questionsRef = database.getReference("questions/$theme")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (questionSnapshot in dataSnapshot.children) {
                    val questionId = questionSnapshot.key?.toIntOrNull()

                    if (questionId == randomIndex) {
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
                                val reponse = Answer(isRight, answer)
                                answersList.add(reponse)
                            }
                        }

                        // Création de l'objet QCM avec les données récupérées
                        questionToAdd = QCM(
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
                        println(questionToAdd)

                        return
                    }
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                println("Database error: ${databaseError.message}")

            }
        }

        questionsRef.addListenerForSingleValueEvent(valueEventListener)
    }

    suspend fun getQuestionFromDatabase(theme: String, randomIndex: Int): QCM? {
        return suspendCoroutine { cont ->
            val questionsRef = database.getReference("questions/$theme")

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (questionSnapshot in dataSnapshot.children) {
                        val questionId = questionSnapshot.key?.toIntOrNull()
                        Timber.d("ok")
                        if (questionId == randomIndex) {
                            val id = questionSnapshot.child("id").getValue(String::class.java)
                            val type = questionSnapshot.child("type").getValue(String::class.java)
                            val category = questionSnapshot.child("category").getValue(String::class.java)
                            val level = questionSnapshot.child("level").getValue(Int::class.java)
                            val question2 = questionSnapshot.child("question").getValue(String::class.java)
                            val image = questionSnapshot.child("image").getValue(String::class.java)
                            val gap = questionSnapshot.child("gap").getValue(Float::class.java)
                            val explanation = questionSnapshot.child("explanation").getValue(String::class.java)

                            val answersList = ArrayList<Answer>()
                            val answersSnapshot = questionSnapshot.child("answers")
                            for (answerSnapshot in answersSnapshot.children) {
                                val answer = answerSnapshot.child("answer").getValue(String::class.java)
                                val isRight = answerSnapshot.child("isRight").getValue(Boolean::class.java)
                                if (answer != null && isRight != null) {
                                    val reponse = Answer(isRight, answer)
                                    answersList.add(reponse)
                                }
                            }

                            val questionToAdd = QCM(
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
                            cont.resume(questionToAdd)
                            return
                        }
                    }
                    cont.resume(null)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    cont.resumeWithException(databaseError.toException())
                }
            }

            questionsRef.addListenerForSingleValueEvent(valueEventListener)
        }
    }

    // Function to generate questions
    fun generateQuestions(): List<QCM> {
        val questionsList = mutableListOf<QCM>()
        runBlocking {
            coroutineScope {
                val jobs = (1..10).map {
                    async {
                        val randomCategory = RandomCategorie()
                        val questionIndex = Random.nextInt(0, 50)
                        getQuestionFromDatabase(randomCategory, questionIndex)
                    }
                }
                val results = jobs.awaitAll()
                questionsList.addAll(results.filterNotNull())
            }
        }

        if (questionsList.isEmpty()) {
            println("No questions found for the given indices.")
        } else {
            println("Questions retrieved: ${questionsList.size}")
        }

        println("Final list of questions: $questionsList")
        return questionsList
    }


    private  fun generateQuestions2(): List<QCM> {
        viewModelScope.launch {
            val randomcategory = RandomCategorie()
            val questionIndex = Random.nextInt(0, 50)
            getQuestionFromDatabase2(randomcategory, questionIndex)
            if (questionToAdd != null) {
                    // Do something with the question
                    println("ok question")
                    _questionList.value = _questionList.value + questionToAdd!!
            } else {
                    // Handle the case where no question was found
                    println("No question found for the given index.")
            }

            println("fin des questions : $questionList")
            while (_questionList.value.size < 15){
                println(_questionList.value)
            }
        }

        return _questionList.value




       /* return listOf(
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

        )*/
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