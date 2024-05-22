package fr.imt.atlantique.codesvi.app.data.model

import android.os.Handler
import android.os.Looper
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
import kotlin.coroutines.suspendCoroutine

class InGameViewModel() : ViewModel() {
    private val db = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")

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

    private var alreadyAnswered = false

    private var isWaitingPlayer = false


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

    /*fun fetchRound(gameId: String){
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
    }*/



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

    fun initWaitingPlayer(gameId: String){
        if(!isWaitingPlayer) {
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
                val value = try {
                    dataSnapshot.getValue(Int::class.java) ?: 0 // Utilisez 0 comme valeur par défaut si non définie
                } catch (e: IllegalStateException) {
                    // Capturer l'exception et afficher un message d'erreur
                    println("Erreur lors de la récupération de la valeur : ${e.message}")
                    0 // Retourner une valeur par défaut en cas d'erreur
                }

                if (value > 0) {
                    displayFunction.value = false
                    println("La valeur de WaitingAnswerPlayer est positive: $value")
                    // Ajoutez ici tout comportement supplémentaire nécessaire lorsque la valeur est positive
                } else {
                    println("La valeur de WaitingAnswerPlayer est nulle ou moins.")
                    // Comportement lorsque la valeur est nulle ou moins
                    // Supprime l'écouteur si la valeur est nulle ou moins

                    waitingPlayerRef.removeEventListener(this)
                    //displayFunction.value = true
                    startNextRound(gameId)
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
        println(alreadyAnswered)
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
                isWaitingPlayer=true


            }
            else{
                nbQuestion+=1
                _questionState.value=false
                alreadyAnswered=false
            }

        }
        println("tentative de réponse échoué")
    }

    fun startGame() {
        viewModelScope.launch {
            delay(5000) // Délai de 5 secondes
            displayFunction.value = true

        }
    }

    fun startNextRound(gameId: String) {
        viewModelScope.launch {
            alreadyAnswered = false // Réinitialiser l'état de réponse des joueurs
            delay(5000) // Délai de 5 secondes pour afficher le score
            resetForNextRound()
            //fetchQuestions(gameId) // Recharger les questions pour la nouvelle manche
            _questionState.value = true // Réinitialiser l'état des questions
            _round.value += 1
            println(alreadyAnswered)
            println(_round.value)
            initWaitingPlayer(gameId)
        }
    }

    private fun resetForNextRound() {
        nbQuestion = 5
        displayFunction.value = false
        alreadyAnswered = false
    }
}