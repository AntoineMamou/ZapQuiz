package fr.imt.atlantique.codesvi.app.ui.screens.login

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.ui.navigation.RootScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.MessageDigest
import java.util.regex.Pattern



fun hacherMotDePasse(motDePasse: String): String {
    val bytes = motDePasse.toByteArray()
    val digest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = digest.digest(bytes)
    return hashedBytes.joinToString("") { "%02x".format(it) }
}


fun ajouterUtilisateur(pseudo: String, motDePasse: String) {
    val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("utilisateurs")
    val userId = usersRef.push().key // Générer une clé unique pour l'utilisateur
    if (userId != null) {
        val motDePasseHache = hacherMotDePasse(motDePasse)
        usersRef.child(userId).setValue(mapOf("username" to pseudo, "password" to motDePasseHache, "trophies" to 100, "playerIcon" to "lightning", "title" to "Zappeur débutant", "connectionState" to false, "friends" to listOf<String>(),"victory" to 0, "game_played" to 0, "peak_trophy" to 100, "favorite_category" to "", "money" to 100, "friendsRequest" to listOf<String>() ))
    }
}






// Fonction pour vérifier si le pseudo est conforme
fun isPseudoConforme(pseudo: String): Boolean {
    // Vérifier si le pseudo est vide
    if (pseudo.isEmpty()) {
        return false
    }
    // Vérifier si le pseudo contient uniquement des caractères alphanumériques
    return Pattern.matches("[a-zA-Z0-9]+", pseudo)
}

// Fonction pour vérifier si le mot de passe est conforme
fun isMotDePasseConforme(motDePasse: String): Pair<Boolean, String> {
    // Vérifier la longueur du mot de passe
    if (motDePasse.length < 8) {
        return Pair(false, "Le mot de passe doit contenir au moins 8 caractères.")
    }

    // Vérifier la présence de caractères spéciaux
    val specialCharacterPattern = Pattern.compile("[^a-zA-Z0-9 ]")
    val hasSpecialCharacter = specialCharacterPattern.matcher(motDePasse).find()
    if (!hasSpecialCharacter) {
        return Pair(false, "Le mot de passe doit contenir au moins un caractère spécial.")
    }

    // Vérifier la présence de majuscules
    val hasUpperCase = motDePasse.any { it.isUpperCase() }
    if (!hasUpperCase) {
        return Pair(false, "Le mot de passe doit contenir au moins une majuscule.")
    }

    // Vérifier la présence de minuscules
    val hasLowerCase = motDePasse.any { it.isLowerCase() }
    if (!hasLowerCase) {
        return Pair(false, "Le mot de passe doit contenir au moins une minuscule.")
    }

    return Pair(true, "")
}


// Fonction pour créer un compte utilisateur
fun creerCompte(pseudo: String, motDePasse: String, showError: (String) -> Unit): Boolean {
    val pseudoConforme = isPseudoConforme(pseudo)
    if (!pseudoConforme) {
        showError("Le pseudo n'est pas conforme.")
        return false
    }

    val (motDePasseConforme, errorMessage) = isMotDePasseConforme(motDePasse)
    if (!motDePasseConforme) {
        showError(errorMessage)
        return false
    }

    ajouterUtilisateur(pseudo, motDePasse)
    return true
}



fun connecterUtilisateur(
    pseudo: String,
    motDePasse: String,
    onNavigateToHome: () -> Unit,
    sharedPreferences: SharedPreferences,
    navController: NavHostController
){
    val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("utilisateurs")

    var found = false

    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override  fun onDataChange(snapshot: DataSnapshot) {
            for (userSnapshot in snapshot.children) {
                val userPseudo = userSnapshot.child("username").getValue(String::class.java)
                val userMotDePasse = userSnapshot.child("password").getValue(String::class.java)

                if (userPseudo == pseudo && userMotDePasse == hacherMotDePasse(motDePasse)) {
                    Timber.e("Connexion réussie pour l'utilisateur : $pseudo")
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("connexion", true)
                    editor.putString("username",pseudo)
                    editor.putBoolean("first_login", false) // Mettre à jour le premier login
                    editor.apply()
                    navController.navigate(RootScreen.Home.route)
                    found = true
                    break
                }
            }

            if (!found) {
                Timber.e("Échec de la connexion pour l'utilisateur : $pseudo")
                // Affichage d'un Snackbar en cas de pseudo ou mot de passe incorrect
            }


        }

        override fun onCancelled(error: DatabaseError) {
            Timber.e("Erreur lors de la connexion : ${error.message}")

        }
    })
}


class LoginViewModel : ViewModel() {
    private val _snackbarFlow = MutableSharedFlow<String>()
    val snackbarFlow = _snackbarFlow.asSharedFlow()

    fun connecterUtilisateur(pseudo: String, motDePasse: String, sharedPreferences: SharedPreferences, navController: NavHostController) {
        val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
        val usersRef = database.getReference("utilisateurs")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var found = false
                for (userSnapshot in snapshot.children) {
                    val userPseudo = userSnapshot.child("username").getValue(String::class.java)
                    val userMotDePasse = userSnapshot.child("password").getValue(String::class.java)

                    if (userPseudo == pseudo && userMotDePasse == hacherMotDePasse(motDePasse)) {
                        Timber.e("Connexion réussie pour l'utilisateur : $pseudo")
                        sharedPreferences.edit().apply {
                            putBoolean("connexion", true)
                            putString("username", pseudo)
                            putBoolean("first_login", false)
                            apply()
                        }
                        navController.navigate(RootScreen.Home.route)
                        found = true
                        break
                    }
                }

                if (!found) {
                    viewModelScope.launch {
                        _snackbarFlow.emit("Pseudo ou mot de passe incorrect")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Erreur lors de la connexion : ${error.message}")
            }
        })
    }

    fun creerCompte(pseudo: String, motDePasse: String, sharedPreferences: SharedPreferences,navController: NavHostController) {
        if (!isPseudoConforme(pseudo)) {
            viewModelScope.launch {
                _snackbarFlow.emit("Le pseudo n'est pas conforme.")
            }
            return
        }

        val (motDePasseConforme, errorMessage) = isMotDePasseConforme(motDePasse)
        if (!motDePasseConforme) {
            viewModelScope.launch {
                _snackbarFlow.emit(errorMessage)
            }
            return
        }

        ajouterUtilisateur(pseudo, motDePasse)
        val editor = sharedPreferences.edit();
        editor.putBoolean("connexion", true);
        editor.putString("username",pseudo);
        editor.putBoolean("first_login", false); // Mettre à jour le premier login
        editor.apply();
        navController.navigate(RootScreen.Home.route)
    }

    private fun isMotDePasseConforme(motDePasse: String): Pair<Boolean, String> {
        if (motDePasse.length < 8) {
            return Pair(false, "Le mot de passe doit contenir au moins 8 caractères.")
        }

        val specialCharacterPattern = Pattern.compile("[^a-zA-Z0-9 ]")
        val hasSpecialCharacter = specialCharacterPattern.matcher(motDePasse).find()
        if (!hasSpecialCharacter) {
            return Pair(false, "Le mot de passe doit contenir au moins un caractère spécial.")
        }

        val hasUpperCase = motDePasse.any { it.isUpperCase() }
        if (!hasUpperCase) {
            return Pair(false, "Le mot de passe doit contenir au moins une majuscule.")
        }

        val hasLowerCase = motDePasse.any { it.isLowerCase() }
        if (!hasLowerCase) {
            return Pair(false, "Le mot de passe doit contenir au moins une minuscule.")
        }

        return Pair(true, "")
    }

    private fun isPseudoConforme(pseudo: String): Boolean {
        // Vérifier si le pseudo est vide
        if (pseudo.isEmpty()) {
            return false
        }
        // Vérifier si le pseudo contient uniquement des caractères alphanumériques
        return Pattern.matches("[a-zA-Z0-9]+", pseudo)
    }

}



@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    state : LoginState,
    onNavigateToHome: () -> Unit,
    navController : NavHostController

) {


    val context = LocalContext.current
    val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }


    var pseudo by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }


    val snackbarHostState = remember { SnackbarHostState() } // Créez l'état de la SnackbarHost

    val viewModel : LoginViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.snackbarFlow.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }




    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        }

    ) { contentPadding ->

        AnimatedVisibility(visible = true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,

                ) {

                Image(
                    painter = painterResource(id = R.drawable.fond),
                    contentDescription = null, // Indiquez une description si nécessaire
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                Column {
                    Row {
                        Spacer(modifier = Modifier.width(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.logo_zap_quiz),
                            contentDescription = null, // Description de l'image si nécessaire
                            modifier = Modifier.width(250.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    TextField(value = pseudo,
                        onValueChange = { pseudo = it },
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text("Pseudo :") })

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = motDePasse,
                        onValueChange = { motDePasse = it },
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text("Mot de passe :") })


                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Button(onClick = {
                            viewModel.connecterUtilisateur(pseudo, motDePasse, sharedPreferences, navController)

                        }) {
                            Text(text = "Se Connecter ")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.creerCompte(pseudo, motDePasse,sharedPreferences,navController)
                        }) {
                            Text(text = "Créer un compte ")
                        }
                    }
                }
            }
        }
    }



}