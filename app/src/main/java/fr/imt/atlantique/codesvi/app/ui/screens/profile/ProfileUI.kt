package fr.imt.atlantique.codesvi.app.ui.screens.profile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.GameViewModel
import fr.imt.atlantique.codesvi.app.data.model.MusicControl
import fr.imt.atlantique.codesvi.app.data.model.User
import fr.imt.atlantique.codesvi.app.ui.screens.game.DisplayTitles
import fr.imt.atlantique.codesvi.app.ui.screens.game.ProfilWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.ScrollableColumnWithImages
import fr.imt.atlantique.codesvi.app.ui.screens.game.SettingsWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.addFriendfromId
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUser
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserId
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.game.removeFriendRequest
import fr.imt.atlantique.codesvi.app.ui.screens.game.user
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    val context = LocalContext.current
    val scrollState = rememberLazyListState()

    println("usersList a affiche : ${usersList.toList()}")
    println("amis a affiche : ${amis.toList()}")

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
                        horizontalArrangement = Arrangement.SpaceAround,
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
                            .clickable(onClick = { onModifyUser(usersList[index]) })



                    ) {

                            Image(
                                painter = painterResource(id = usersList[index].getImageResourceId(context)),
                                contentDescription = null, // Description de l'image si nécessaire
                                modifier = Modifier.size(30.dp)
                            )


                            Text(
                                text = amis[index],
                                color = Color.White,
                                fontSize = 20.sp,
                                fontFamily = fontPrincipale,
                            )


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                        ) {


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
    onSearchResultUsers: (List<String>) -> Unit,
    onFriendsRequestVisible: (Boolean) -> Unit,
    isFriendsRequest: Boolean
) {
    var searchText by remember { mutableStateOf("") }
    var filteredNouns by remember { mutableStateOf(emptyList<String>()) }
    var displayList by remember { mutableStateOf<List<String>>(users) } // Default to users
    var isAllSelected by remember { mutableStateOf(false) } // Initialize with false

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 60.dp)
            .fillMaxHeight()
    ) {
        Text(
            text = "Social",
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = fontPrincipale
        )
        //Spacer(Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = if (isFriendsRequest) R.drawable.add_friend_pop else R.drawable.add_friend),
                contentDescription = null,
                modifier = Modifier
                    .width(60.dp)
                    .clickable(onClick = { onFriendsRequestVisible(true) })
            )

            //Spacer(Modifier.width(12.dp))

            Box(

            ) {
                TextField(
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                        filteredNouns = filterFriends(displayList, searchText)
                        onSearchResult(filteredNouns)
                        onSearchResultUsers(filteredNouns)
                    },
                    placeholder = { Text(text = "Rechercher ...") },
                    /*modifier = Modifier
                        .padding(end = 8.dp),*/
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
                    },
                    modifier = Modifier.rotate(90F)
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


@Composable
fun PopupWindow(
    friends_request: List<String>,
    onClose: () -> Unit,
) {
    val context = LocalContext.current

    BackHandler(onBack = {
        // Handle back button press to close the window
        onClose()
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Handle click outside of the window to close it
                        onClose()
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(400.dp)
                .align(Alignment.Center)
                .background(colorResource(id = R.color.blue_2), RoundedCornerShape(15.dp))
                .border(
                    width = 8.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        colorResource(id = R.color.blue_2),
                        RoundedCornerShape(15.dp)
                    )
                    .padding(16.dp)
            ) {
                // Image in the top right corner
                Image(
                    painter = painterResource(id = R.drawable.croix_suppr), // replace with your image resource
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            // Close the window when clicked
                            onClose()
                        }
                        .align(Alignment.End)
                )

                Text(
                    text = "Demandes d'ami",
                    fontSize = 25.sp,
                    fontFamily = fontPrincipale,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .height(10.dp)
                        .border(
                            1.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(2.dp)
                        ),
                    thickness = 5.dp,
                    color = MaterialTheme.colorScheme.secondary

                )

                if(friends_request.isEmpty()){

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Personne ne veut être votre Zami :(",
                        fontFamily = fontPrincipale,
                        fontSize = 18.sp,
                        color = Color.White,
                        fontStyle = FontStyle.Italic
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    friends_request.forEach { username ->
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$username veut être votre ami",
                                    fontFamily = fontPrincipale,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 4.dp)
                                )
                                IconButton(
                                    onClick = {
                                        user?.let {
                                            getUserId(it.username, { myUserId ->
                                                addFriendfromId(myUserId, username, true);
                                                getUserId(username, { userId ->
                                                    addFriendfromId(userId, user!!.username, false)
                                                });
                                            })
                                        };
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.tick),
                                        contentDescription = "Accept",
                                        modifier = Modifier.size(30.dp),
                                        tint = Color.Green
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        var myUserIdCatch = ""
                                        user?.let { getUserId(it.username, { userId -> myUserIdCatch = userId;
                                            removeFriendRequest(myUserIdCatch,username)
                                        }) }
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.cross),
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Red
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .height(5.dp)
                                    .border(
                                        1.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(2.dp)
                                    ),
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.primary

                            )
                        }
                    }
                }
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
fun LoadingSpinner() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(50.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingSpinner() {
    LoadingSpinner()
}



class UserViewModel : ViewModel() {
    private val _usersList = MutableStateFlow<SnapshotStateList<User>>(mutableStateListOf())
    val usersList: StateFlow<SnapshotStateList<User>> = _usersList

    private val _allUsersList = MutableStateFlow<SnapshotStateList<User>>(mutableStateListOf())
    val allUsersList: StateFlow<SnapshotStateList<User>> = _allUsersList


    suspend fun fetchUsersForNouns(filteredNouns: List<String>) {
        viewModelScope.launch {
            val newUsersList = mutableListOf<User>()
            for (username in filteredNouns) {
                val user = getUserInfoDatabase(username) // Assume this is a suspend function
                user?.let {
                    newUsersList.add(it)
                }
            }
            _usersList.value = mutableStateListOf(*newUsersList.toTypedArray())
        }
    }

    fun fetchAllUsersForNouns(filteredNouns: List<String>) {
        viewModelScope.launch {
            val newUsersList = mutableListOf<User>()
            for (username in filteredNouns) {
                val user = getUserInfoDatabase(username) // Assume this is a suspend function
                user?.let {
                    newUsersList.add(it)
                }
            }
            _allUsersList.value = mutableStateListOf(*newUsersList.toTypedArray())
        }
    }

    fun updateUsersList(newUsers: List<User>) {
        viewModelScope.launch {
            // Créez une nouvelle SnapshotStateList avec les nouveaux utilisateurs
            val snapshotList = mutableStateListOf<User>().apply {
                addAll(newUsers)
            }
            _usersList.value = snapshotList
        }
    }

}



@Composable
fun ProfileScreen(
    state : ProfileState,
    modifier: Modifier = Modifier,
    navController : NavHostController
) {
    MusicControl()

    val tag: String? = "MyAppTag"
    Log.d(tag ?: "DefaultTag", "Bonjour")

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

    val gameModel = GameViewModel()


    val userState by gameModel.userState.collectAsState()
    val username = sharedPreferences.getString("username", "")

    // Déclenchez le chargement de l'utilisateur


    userState?.let { getUser(it) }

    //Permet de gérer l'affichage ou non de la fenêtre des paramètres
    var settingsModalVisible by remember { mutableStateOf(false) }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (settingsModalVisible) {

        SettingsWindow(onClose = { settingsModalVisible = false }, sharedPreferences, navController)
    }

    var changeIconVisible by remember { mutableStateOf(false) }
    var changeTitleVisible by remember { mutableStateOf(false) }
    //Permet de gérer l'affichage ou non de la fenêtre de profil
    var profilVisible by remember { mutableStateOf(false) }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (profilVisible) {
        user?.let { ProfilWindow(onClose = { profilVisible = false }, it, false, {changeIconVisible = true}, {changeTitleVisible = true}) }
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



    /*PROBLEME ICI */
    var filteredNouns by remember { mutableStateOf(listeAmis) }

    val viewModel: UserViewModel = viewModel()
    val usersList by viewModel.usersList.collectAsState()
    val allUsersList by viewModel.allUsersList.collectAsState()

   // usersList= changeUsersList(filteredNouns = filteredNouns)
    //allUsersList = changeUsersList(filteredNouns = usernames.value)
    //val allUsersList = listOf<User>()

    LaunchedEffect(filteredNouns) {
        println("avant modif ${usersList.toList()}")
        viewModel.fetchUsersForNouns(filteredNouns)
        println("apres modif ${usersList.toList()}")
    }

    LaunchedEffect(usernames) {
        viewModel.fetchAllUsersForNouns(usernames.value)
    }


    var friendsRequestVisible by remember { mutableStateOf(false) }
    /*#######################################################################*/


    var friendsRequestList by remember { mutableStateOf((user!!.friendsRequest)) }
    println(friendsRequestList)

    println("users : $usernames")

    Main(
        friends = listeAmis, users = usernames.value,
        { filteredNounsList -> filteredNouns = filteredNounsList },
        { filteredNouns -> viewModel.updateUsersList(filterUsers(allUsersList, filteredNouns))},
        {bool -> friendsRequestVisible = bool},
        !friendsRequestList.isEmpty()
    )
    println(friendsRequestVisible)


    var userProfilVisible by remember { mutableStateOf(false) }
    var user_display = remember { mutableStateOf<User?>(null) }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (userProfilVisible) {
        user_display.value?.let { user?.friends?.let { it1 -> ProfilWindow(onClose = { userProfilVisible = false }, user_display = it, !it1.contains(user_display.value!!.username), {})} }
    }

    if(usersList.isNotEmpty()) {
        AfficheListe(
            amis = filteredNouns,
            usersList = usersList,
            onClose = { userProfilVisible = false },
            { user -> user_display.value = user; userProfilVisible = true })
    }
    else{
        PreviewLoadingSpinner()
    }


    if (friendsRequestVisible){
        PopupWindow(friends_request = friendsRequestList, onClose = {friendsRequestVisible = false})
    }




    if (changeIconVisible) {
        ScrollableColumnWithImages(
            user!!.availableIcons, onClose = { changeIconVisible = false }, gameModel
        )
    }

    if (changeTitleVisible) {
        DisplayTitles(
            titleList = user!!.availableTitles,
            onClose = { changeTitleVisible = false }, gameModel
        )
    }

    // Problème d'update que je n'arrive pas a résoudre
    //cette solution risque de créer un chargement infini / crash ?
   LaunchedEffect(key1 = userState?.username + userState?.playerIcon + userState?.title + changeTitleVisible + changeIconVisible
            ) {
        gameModel.loadUser(username)
        println("ok")
    }

}