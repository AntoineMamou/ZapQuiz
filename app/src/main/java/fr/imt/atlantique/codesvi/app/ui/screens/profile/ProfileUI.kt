package fr.imt.atlantique.codesvi.app.ui.screens.profile

import android.content.Context
import android.content.SharedPreferences
import android.widget.NumberPicker.OnValueChangeListener
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.User
import fr.imt.atlantique.codesvi.app.ui.screens.game.ProfilWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.SettingsWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.fontChiffre
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.game.user
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

val fontPrincipale = FontFamily(Font(R.font.bubble_bobble))


fun filterFriends(friends: List<String>, searchText: String): List<String> {
    // Filter friends whose names start with the search text (case-insensitive)
    return friends.filter { it.startsWith(searchText, ignoreCase = true) }
}

fun filterUsers(users: List<User>, filteredUsernames: List<String>): SnapshotStateList<User> {
    // Filter users whose usernames are present in the filteredUsernames list
    val filteredUsers = users.filter { user -> user.username in filteredUsernames }

    // Convert the filtered list back to a SnapshotStateList
    return mutableStateListOf(*filteredUsers.toTypedArray())
}




@Composable
fun AfficheListe(amis: List<String>, usersList: List<User>, onClose: () -> Unit, onModifyUser: (User) -> Unit ) {
    val scrollState = rememberLazyListState()

    if (usersList.toList().size != 0) {


        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scrollState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 210.dp)
        ) {
            items(min(amis.size,usersList.size)) { index ->
                if (index >= 0 ) { // Limite le nombre d'éléments affichés à partir de l'index 3

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(15.dp)
                            )
                            .width(300.dp)
                            .height(70.dp)
                            .border(
                                5.dp,
                                color = MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(15.dp)
                            )
                            .clickable(onClick = {onModifyUser(usersList[index])})



                    ) {

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .width(100.dp)
                                .fillMaxWidth()

                        ) {

                            Image(
                                painter = painterResource(id = usersList[index].playerIcon),
                                contentDescription = null, // Description de l'image si nécessaire
                                modifier = Modifier.size(30.dp)
                            )


                            Text(
                                text = amis[index],
                                color = Color.White,
                                fontSize = 20.sp,
                                fontFamily = fontPrincipale
                            )

                        }

                        Spacer(modifier = Modifier.width(80.dp))

                        Text(
                            text = usersList[index].trophies.toString(),
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = fontPrincipale,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            painter = painterResource(id = R.drawable.couronne_laurier),
                            contentDescription = null, // Description de l'image si nécessaire
                            modifier = Modifier.size(50.dp)
                        )


                    }
                    Spacer(modifier = Modifier.height(8.dp))


                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(
    friends: List<String>,
    users: List<String>,
    onSearchResult: (List<String>) -> Unit,
    onSearchResultUsers: (List<String>) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var filteredNouns by remember { mutableStateOf(emptyList<String>()) }
    var displayList by remember { mutableStateOf<List<String>>(users) } // Default to users
    var isAllSelected by remember { mutableStateOf(false) } // Initialize with false

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 60.dp)
    ) {
        Text(
            text = "Social",
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = fontPrincipale
        )
        Spacer(Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ajouter_ami),
                contentDescription = null,
                modifier = Modifier.width(60.dp)
            )

            Spacer(Modifier.width(12.dp))

            Box(

            ) {
                TextField(
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                        filteredNouns = filterFriends(displayList, searchText)
                        onSearchResult(filteredNouns)
                        onSearchResultUsers(filteredNouns)
                    },
                    placeholder = { Text(text = "Rechercher ...") },
                    modifier = Modifier
                        .padding(end = 8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        filteredNouns = filterFriends(displayList, searchText)
                        onSearchResult(filteredNouns)
                        onSearchResultUsers(filteredNouns)
                    })
                )
            }
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = "Amis",
                    fontFamily = fontPrincipale,
                    color = if (!isAllSelected) MaterialTheme.colorScheme.primary else Color.Black,
                    modifier = Modifier.clickable {
                        isAllSelected = false
                        displayList = friends
                        filteredNouns = filterFriends(displayList, searchText)
                        onSearchResult(filteredNouns)
                        onSearchResultUsers(filteredNouns)
                    }
                )


                Spacer(Modifier.width(16.dp))

                Switch(
                    checked = isAllSelected,
                    onCheckedChange = { isChecked ->
                        isAllSelected = isChecked
                        displayList = if (isChecked) users else friends
                        filteredNouns = filterFriends(displayList, searchText)
                        onSearchResult(filteredNouns)
                        onSearchResultUsers(filteredNouns)
                    }
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Tous",
                    fontFamily = fontPrincipale,
                    color = if (isAllSelected) MaterialTheme.colorScheme.primary else Color.Black,
                    modifier = Modifier.clickable {
                        isAllSelected = true
                        displayList = users
                        filteredNouns = filterFriends(displayList, searchText)
                        onSearchResult(filteredNouns)
                        onSearchResultUsers(filteredNouns)
                    }
                )
            }
        }
    }
}

suspend fun getAllUsernames(): List<String> {
    return suspendCoroutine { continuation ->
        try {
            val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.getReference("utilisateurs")

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usernames = mutableListOf<String>()

                    for (userSnapshot in snapshot.children) {
                        val username = userSnapshot.child("username").getValue(String::class.java)
                        if (username != null) {
                            usernames.add(username)
                        }
                    }

                    //println("Fetched usernames: $usernames")
                    continuation.resume(usernames)
                }

                override fun onCancelled(error: DatabaseError) {
                    //println("Database error: ${error.message}")
                    continuation.resumeWithException(error.toException())
                }
            }

            usersRef.addListenerForSingleValueEvent(valueEventListener)
        } catch (e: Exception) {
            println("Exception occurred: ${e.message}")
            continuation.resumeWithException(e)
        }
    }
}

@Composable
fun changeUsersList(filteredNouns: List<String>): SnapshotStateList<User> {
    var usersList by remember { mutableStateOf<SnapshotStateList<User>>(mutableStateListOf()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(filteredNouns.joinToString()) {
        val newUsersList = mutableListOf<User>()

        // Launch new jobs for each username in filteredNouns
        for (username in filteredNouns) {
            coroutineScope.launch {
                val user = getUserInfoDatabase(username)

                // Add the new user to the temporary list
                user?.let {
                    newUsersList.add(it)
                }

                // Update usersList with the new list
                usersList = mutableStateListOf(*newUsersList.toTypedArray())
            }
        }
    }

    return usersList
}





@Composable
fun ProfileScreen(
    state : ProfileState,
    modifier: Modifier = Modifier,
    navController : NavHostController
) {

    val context = LocalContext.current
    val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }



    //Permet de gérer l'affichage ou non de la fenêtre des paramètres
    var settingsModalVisible by remember { mutableStateOf(false) }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (settingsModalVisible) {

        SettingsWindow(onClose = { settingsModalVisible = false }, sharedPreferences, navController)
    }


    //Permet de gérer l'affichage ou non de la fenêtre de profil
    var profilVisible by remember { mutableStateOf(false) }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (profilVisible) {
        user?.let { ProfilWindow(onClose = { profilVisible = false }, it) }
    }


    fr.imt.atlantique.codesvi.app.ui.screens.game.BackgroundImage()
    user?.let {
        fr.imt.atlantique.codesvi.app.ui.screens.game.Header({ settingsModalVisible = true },
        { profilVisible = true }, it
        )
    }



    val listeAmis = remember {
        user!!.friends
    }

    val usernames = remember {
        mutableStateOf<List<String>>(listOf<String>())
    }
    LaunchedEffect(usernames){
        val searched_usernames = getAllUsernames()
        usernames.value=searched_usernames
    }


    var filteredNouns by remember { mutableStateOf(listeAmis) }
    var usersList = remember { mutableStateListOf<User>() }
    usersList = changeUsersList(filteredNouns = filteredNouns)
    val allUsersList = changeUsersList(filteredNouns = usernames.value)
    println(allUsersList.toList())


    Main(friends = listeAmis, users = usernames.value,
        { filteredNounsList -> filteredNouns = filteredNounsList },
        { filteredNouns -> usersList = filterUsers(allUsersList, filteredNouns);println(usersList.toList()) }
    )

    var userProfilVisible by remember { mutableStateOf(false) }
    var user_display = remember { mutableStateOf<User?>(null) }
    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (userProfilVisible) {
        println(user_display.value)
        user_display.value?.let { ProfilWindow(onClose = { userProfilVisible = false }, user = it) }
    }

    AfficheListe(amis = filteredNouns, usersList = usersList, onClose = { userProfilVisible = false }, {user -> user_display.value = user; userProfilVisible = true})
}