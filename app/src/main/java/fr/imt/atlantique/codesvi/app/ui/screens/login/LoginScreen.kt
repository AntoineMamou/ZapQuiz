package fr.imt.atlantique.codesvi.app.ui.screens.login

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.imt.atlantique.codesvi.app.R
import kotlinx.coroutines.launch
import androidx.compose.material3.SearchBar as SearchBar1
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
        usersRef.child(userId).setValue(mapOf("pseudo" to pseudo, "motDePasse" to motDePasseHache))
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
fun isMotDePasseConforme(motDePasse: String): Boolean {
    // Vérifier si le mot de passe fait au moins 8 caractères
    if (motDePasse.length < 8) {
        return false
    }
    // Vérifier si le mot de passe contient au moins un caractère spécial ou une majuscule
    val specialCharacterPattern = Pattern.compile("[^a-zA-Z0-9 ]")
    val hasSpecialCharacter = specialCharacterPattern.matcher(motDePasse).find()
    val hasUpperCase = motDePasse.any { it.isUpperCase() }
    val hasLowerCase = motDePasse.any {it.isLowerCase()}
    return (hasSpecialCharacter && hasUpperCase && hasLowerCase)
}


// Fonction pour créer un compte utilisateur
fun creerCompte(pseudo: String, motDePasse: String): Boolean {
    // Vérifier si le pseudo et le mot de passe sont conformes
    return if (isPseudoConforme(pseudo) && isMotDePasseConforme(motDePasse)) {
        // Si oui, ajouter l'utilisateur à la base de données
        ajouterUtilisateur(pseudo, motDePasse)
        true
    } else {
        // Sinon, afficher un message d'erreur ou effectuer une autre action
        // Par exemple :
        false
    }
}


fun connecterUtilisateur(
    pseudo: String,
    motDePasse: String,
    onNavigateToHome: () -> Unit,
): Boolean {
    val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("utilisateurs")

    var x=false

    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (userSnapshot in snapshot.children) {
                val userPseudo = userSnapshot.child("pseudo").getValue(String::class.java)
                val userMotDePasse = userSnapshot.child("motDePasse").getValue(String::class.java)

                if (userPseudo == pseudo && userMotDePasse == hacherMotDePasse(motDePasse)) {
                    Timber.e("Connexion réussie pour l'utilisateur : $pseudo")
                    onNavigateToHome()
                }
                else{
                    x=true
                    //afficheSnackBar.value ="pseudo ou mot de passe incorrect"
                }
            }

            Timber.e("Échec de la connexion pour l'utilisateur : $pseudo")


        }

        override fun onCancelled(error: DatabaseError) {
            Timber.e("Erreur lors de la connexion : ${error.message}")

        }
    })
    return x
}


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    state : LoginState,
    onNavigateToHome: () -> Unit
) {


    var pseudo by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }


    val snackbarHostState = remember { SnackbarHostState() } // Créez l'état de la SnackbarHost





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
                            val x= connecterUtilisateur(pseudo, motDePasse, onNavigateToHome)

                        }) {
                            Text(text = "Se Connecter ")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val x = creerCompte(pseudo, motDePasse);
                            if (x) {
                                onNavigateToHome()
                            }
                        }) {
                            Text(text = "Créer un compte ")
                        }
                    }
                }
            }
        }
    }



}