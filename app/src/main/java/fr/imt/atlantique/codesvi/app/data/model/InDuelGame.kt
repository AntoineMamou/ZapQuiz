package fr.imt.atlantique.codesvi.app.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.random.Random


data class GameResult(
    val result: String,
    val trophies: Int,
    val coins: Int,
    val username: String
)


class InDuelGame() : ViewModel() {
    private val db =
        FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")

    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    private val _questions = MutableLiveData<List<QCM>>()
    val questions: LiveData<List<QCM>> = _questions

    var nbQuestion = 0

    private val _questionState = MutableStateFlow(true)  // Initial state
    val questionState: StateFlow<Boolean> = _questionState.asStateFlow()

    private val _round = MutableStateFlow<Int>(1)
    val round: StateFlow<Int> = _round.asStateFlow()

    val displayFunction = MutableStateFlow(false)
    var endGame = MutableStateFlow(false)

    private var alreadyAnswered = false

    private var isWaitingPlayer = false

    var score = 0

    var gameStat: GameResult? = null

    fun updateQuestionState() {
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

    fun fetchQuestions(gameId: String) {
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

    fun initWaitingPlayer(gameId: String) {
        if (!isWaitingPlayer) {
            db.getReference("games").child(gameId).child("WaitingAnswerPlayer").setValue(2)
        }
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
                    if (mutableData.value == 0) {
                        //setFutureGameStartTime(gameId,5000)
                    }
                }
                // Si la valeur actuelle est déjà à 0 ou moins, elle n'est pas changée.
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                if (committed) {
                    println("Transaction réussie : WaitingAnswerPlayer décrémenté")
                } else {
                    println("Transaction annulée, raison : ${databaseError?.message}")
                }
            }
        })
    }

    fun monitorWaitingAnswerPlayer(gameId: String,username: String) {
        val waitingPlayerRef = db.getReference("games").child(gameId).child("WaitingAnswerPlayer")

        val coroutineScope = CoroutineScope(Dispatchers.Main)

        coroutineScope.launch {
            var isActive = true // Variable pour suivre l'état de la coroutine
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.getValue(Int::class.java) ?: 0
                    if (isActive) { // Vérifiez si la coroutine est toujours active
                        if (value > 0) {
                            displayFunction.value = false
                            println("La valeur de WaitingAnswerPlayer est positive: $value")
                            // Ajoutez ici tout comportement supplémentaire nécessaire lorsque la valeur est positive
                        } else {
                            println("La valeur de WaitingAnswerPlayer est nulle ou moins.")
                            // Comportement lorsque la valeur est nulle ou moins
                            // Supprime l'écouteur si la valeur est nulle ou moins
                            waitingPlayerRef.removeEventListener(this)
                            isActive = false // Marquer la coroutine comme terminée
                            EndGame(gameId,username)

                            //monitorGameStartTimeAndStart(gameId)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Écoute annulée pour WaitingAnswerPlayer, raison : ${databaseError.message}")
                    if (isActive) { // Vérifiez si la coroutine est toujours active
                        waitingPlayerRef.removeEventListener(this)
                        isActive = false // Marquer la coroutine comme terminée
                        //EndGame(gameId,username)
                        displayFunction.value = false
                        //monitorGameStartTimeAndStart(gameId)
                    }
                }
            }

            // Attribuez le listener à la référence
            waitingPlayerRef.addValueEventListener(listener)
            // Attendez que la valeur devienne 0
            while (isActive) {
                displayFunction.value = false
                delay(1000) // Attendez une seconde avant de vérifier à nouveau
            }
        }
    }






    fun submitAnswer(gameId: String, username: String, correctAnswer: Boolean) {
        println(alreadyAnswered)
        if (!alreadyAnswered) {
            alreadyAnswered = true
            val gameRef = db.getReference("games/$gameId")

            val playersRef = gameRef.child("players")

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
                                            score+= questions.value!![nbQuestion].level * 4
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

            if (nbQuestion == 9) {
                monitorWaitingAnswerPlayer(gameId,username)
                decrementWaitingAnswerPlayer(gameId)
                isWaitingPlayer = true


            } else {
                nbQuestion += 1
                _questionState.value = false
                alreadyAnswered = false
            }

        }
        println("tentative de réponse échoué")
    }

    fun startGame() {
        if(nbQuestion==0){
            viewModelScope.launch {
                delay(5000) // Délai de 5 secondes
                displayFunction.value = true
                println("new game has started")
            }
        }
    }

    fun deleteGame(gameId: String) {
        // Obtenez la référence de l'élément en utilisant le chemin approprié
        val itemRef = db.getReference("games").child(gameId)

        // Vérifiez si l'élément existe
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // L'élément existe, donc on peut le supprimer
                    itemRef.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("L'élément avec l'ID $gameId a été supprimé avec succès.")
                        } else {
                            println("Échec de la suppression de l'élément avec l'ID $gameId : ${task.exception?.message}")
                        }
                    }
                } else {
                    // L'élément n'existe pas
                    println("L'élément avec l'ID $gameId n'existe pas dans la base de données.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Erreur lors de la vérification de l'existence de l'élément : ${databaseError.message}")
            }
        })
    }


    fun getOpponentUsername(username: String) : String? {
        val u1 = players.value?.get(0)
        val u2 = players.value?.get(1)

        if (u1 != null && u2 != null) {
            if(u1.user.username==username){
                return u2.user.username
            }
            else{
                if(u2.user.username==username){
                    return u1.user.username
                }
            }
        }

        return null

    }


    fun EndStats(gameId: String, username: String, onStatsCompleted: () -> Unit) {
        val opponent = getOpponentUsername(username)
        val gameRef = db.getReference("games/$gameId")
        val playersRef = gameRef.child("players")

        var result = ""
        var trophies = 0
        var coins = 0

        val usernameQuery = playersRef.orderByChild("user/username").equalTo(opponent)
        usernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (playerSnapshot in snapshot.children) {
                        val playerId = playerSnapshot.key
                        val opponentScore = playerSnapshot.child("score").getValue(Int::class.java) ?: 0
                        if (opponentScore > score) {
                            result = "Défaite"
                        } else if (opponentScore < score) {
                            result = "Victoire"
                        } else {
                            result = "Égalité"
                        }

                        if (result == "Victoire"){
                            trophies = Random.nextInt(27, 32)
                            coins = Random.nextInt(30, 41)
                        }

                        if (result == "Défaite"){
                            trophies = -1 * Random.nextInt(15, 22)
                            coins = Random.nextInt(10, 30)
                        }

                        if(result == "Égalité"){
                            trophies = Random.nextInt(1, 4)
                            coins = Random.nextInt(20, 31)
                        }

                        gameStat = GameResult(result = result, trophies = trophies, coins = coins, username = username)
                        println("gameStat actualise : $gameStat")
                    }
                } else {
                    println("Aucun joueur trouvé avec ce pseudo.")
                }
                onStatsCompleted() // Appel du callback une fois que les statistiques sont terminées
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag("InGameViewModel").e("Failed to update score: %s", error.message)
                onStatsCompleted() // Appel du callback en cas d'erreur
            }
        })
    }

    fun EndGame(gameId: String, username: String) {
        EndStats(gameId, username) {
            // Cette fonction sera exécutée une fois que EndStats sera terminé
            while (gameStat == null) {
                // Attendre jusqu'à ce que gameStat soit mis à jour
                // Cela peut entraîner un blocage si gameStat ne change pas
                println(gameStat)
            }
            println("fini")
            endGame.value = true
            //deleteGame(gameId)
        }
    }
}
