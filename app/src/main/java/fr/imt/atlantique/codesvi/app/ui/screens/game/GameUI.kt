package fr.imt.atlantique.codesvi.app.ui.screens.game

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import dagger.hilt.android.internal.Contexts.getApplication
import fr.imt.atlantique.codesvi.app.data.model.Answer
import fr.imt.atlantique.codesvi.app.data.model.MusicControl
import fr.imt.atlantique.codesvi.app.data.model.QCM
import fr.imt.atlantique.codesvi.app.data.model.SoundEffect

import fr.imt.atlantique.codesvi.app.R

import fr.imt.atlantique.codesvi.app.data.model.User
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.navigation.RootScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


val  fontPrincipale = FontFamily(Font(R.font.bubble_bobble))
val fontChiffre  = FontFamily(Font(R.font.bubble_bobble))

var user : User? = null

fun getAnyImageId(image: String, context: Context): Int {
    return context.resources.getIdentifier(image, "drawable", context.packageName)
}

fun getUser(user_eff : User){
    user = user_eff
}
suspend fun getUserInfoDatabase(
    username: String
): User? {
    return suspendCoroutine { continuation ->
        val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
        val usersRef = database.getReference("utilisateurs")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (userSnapshot in snapshot.children) {
                    val userSearched = userSnapshot.child("username").getValue(String::class.java)

                    if (userSearched == username) {
                        Timber.d("debut pour $userSearched")
                        val id = userSnapshot.child("username").getValue(String::class.java) ?: ""
                        val password = userSnapshot.child("password").getValue(String::class.java) ?: ""
                        val trophies = userSnapshot.child("trophies").getValue(Int::class.java) ?: 0
                        val playerIcon = userSnapshot.child("playerIcon").getValue(String::class.java) ?: ""
                        val title = userSnapshot.child("title").getValue(String::class.java) ?: ""
                        val connectionState = userSnapshot.child("connectionState").getValue(Boolean::class.java) ?: false
                        val victory = userSnapshot.child("victory").getValue(Int::class.java) ?: 0
                        val gamePlayed = userSnapshot.child("game_played").getValue(Int::class.java) ?: 0
                        val peakTrophy = userSnapshot.child("peak_trophy").getValue(Int::class.java) ?: 0
                        val favoriteCategory = userSnapshot.child("favorite_category").getValue(String::class.java) ?: ""
                        val money = userSnapshot.child("money").getValue(Int::class.java) ?: 0

                        val friendsList = ArrayList<String>()
                        val friendsSnapshot = userSnapshot.child("friends")
                        for (friendSnapshot in friendsSnapshot.children) {
                            friendsList.add(friendSnapshot.value.toString())
                        }

                        val friendsRequest = ArrayList<String>()
                        val friendsRequestSnapshot = userSnapshot.child("friendsRequest")
                        for (friendRequestSnapshot in friendsRequestSnapshot.children) {
                            friendsRequest.add(friendRequestSnapshot.value.toString())
                        }
                        Timber.d("fini pour $userSearched")
                        val user = User(id, password, trophies, playerIcon, title, connectionState, friendsList, victory, gamePlayed, peakTrophy, favoriteCategory, money, friendsRequest)
                        continuation.resume(user)
                        return
                    }
                }
                println("Username not found")
                continuation.resume(null)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
                continuation.resumeWithException(error.toException())
            }
        }

        usersRef.addListenerForSingleValueEvent(valueEventListener)
    }
}





@Composable
fun BackgroundImage() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Afficher l'image de fond
        Image(
            painter = painterResource(id = R.drawable.fond),
            contentDescription = null, // Indiquez une description si nécessaire
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun Header(settingsonClick: () -> Unit , profilOnClick: ()->Unit, user : User) {

    val context= LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
        // Image à gauche

        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 3.dp),
        ){
            IconButton(
                onClick = { profilOnClick() },
                modifier = Modifier.size(70.dp),
                content = {
                    Image(
                        painter = painterResource(id = user.getImageResourceId(context)),
                        contentDescription = null, // Description de l'image si nécessaire
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )

            Text(
                text = user.username,
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = fontPrincipale
            )

        }







        // Contenu à droite (texte et image)
        Box(
            modifier = Modifier
                .padding(end = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Texte à droite
                Text(
                    text = user.money.toString(),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = fontChiffre
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Image à droite
                Image(
                    painter = painterResource(id = R.drawable.piece),
                    contentDescription = null, // Description de l'image si nécessaire
                    modifier = Modifier.size(30.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(30.dp))


                IconButton(
                    onClick = {  settingsonClick()},
                    modifier = Modifier.size(30.dp),
                    content = {
                        Image(
                            painter = painterResource(id = R.drawable.roue_parametre),
                            contentDescription = "Paramètres",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                )
            }
        }

    }
}

@Composable
fun Logo(){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,) {
        Image(
            painter = painterResource(id = R.drawable.logo_zap_quiz),
            contentDescription = null, // Description de l'image si nécessaire
            modifier = Modifier.width(220.dp)
        )

    }

}




//Permet de définir le mode de jeu pour le bouton start et l'image affiché parmi les 3 modes de jeu
var currentIndex = 1

@Composable
fun ModeDeJeu() {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var job: Job? = null
    val itemWidth = 390.dp // Largeur de chaque élément
    //REGLER LE PROBLEME POUR ACCEDER A LA TAILLE DE L'ECRAN DU JOUEUR
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    //val screenWidth = 400.dp
    val spacing = (screenWidth - itemWidth) / 2 + 5.dp


    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        state = scrollState,
        userScrollEnabled = false,
        modifier = Modifier
            .fillMaxWidth()
            //.padding(top = 190.dp)
            .padding(start = spacing, end = spacing)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, _) = dragAmount

                    // Annuler la tâche précédente
                    job?.cancel()
                    job = coroutineScope.launch {
                        // Attendre les prochains changements de gestes
                        delay(200)


                        when {
                            x > 0 -> {
                                try {
                                    scrollState.animateScrollToItem(--currentIndex)
                                } catch (e: Exception) {

                                }
                            }

                            x < 0 -> {
                                try {
                                    scrollState.animateScrollToItem(++currentIndex)
                                } catch (e: Exception) {

                                }
                            }
                        }
                    }
                }
            }
    ) {
        val boxWidth = itemWidth
        val boxHeight = 230.dp



        items(3) { index ->

            Box(
                modifier = Modifier
                    .width(boxWidth)
                    .height(boxHeight)
                    .padding(end = 10.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(15.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black,

                        ),
                content = {
                    when (index) {
                        0 -> {
                            // Load image for index 1
                            Image(
                                painter = painterResource(R.drawable.multi_main),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 10.dp, end = 10.dp)
                                    .background(
                                        getColorForIndex(index = index),
                                        RoundedCornerShape(15.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                            )
                        }
                        1 -> {
                            // Load image for index 2
                            Image(
                                painter = painterResource(R.drawable.solo_main),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 10.dp, end = 10.dp)
                                    .background(
                                        getColorForIndex(index = index),
                                        RoundedCornerShape(15.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                            )
                        }
                        2 -> {
                            // Load image for index 3
                            Image(
                                painter = painterResource(R.drawable.duel_main),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 10.dp, end = 10.dp)
                                    .background(
                                        getColorForIndex(index = index),
                                        RoundedCornerShape(15.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                            )
                        }
                    }
                }
            )


        }
    }

    // Faire défiler vers l'élément initial
    LaunchedEffect(Unit) {
        scrollState.scrollToItem(currentIndex)
    }
}



@Composable
fun getColorForIndex(index: Int): Color {
    return when (index) {
        0 -> Color.Blue
        1 -> Color.Red
        else -> Color.Blue
    }
}


@Composable
fun MainGame() {
    Box(
        modifier = Modifier
            .padding(top = 40.dp)
            .padding(start = 0.dp)
            .padding(end = 0.dp)
            .fillMaxSize()
            .zIndex(0.5f)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .height(150.dp)
                    .width(100.dp)
                    .size(600.dp),

                content = {
                    Image(
                        painter = painterResource(id = R.drawable.fleche_gauche),
                        contentDescription = "fleche_gauche",
                    )
                }
            )
        }



        IconButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(150.dp)
                .width(100.dp)
                .size(600.dp),

            content = {
                Image(
                    painter = painterResource(id = R.drawable.fleche_droite),
                    contentDescription = "fleche_droite",
                )
            }
        )
    }
}


@Composable
fun StartButton(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {


        IconButton(
            onClick = {
                if(currentIndex <= 0) {
                    currentIndex=0
                    navController.navigate(HomeRootScreen.Duel.route)
                }

                if(currentIndex == 1) {
                    navController.navigate(HomeRootScreen.Solo.route)
                }

                if(currentIndex >= 2) {
                    currentIndex=2
                    navController.navigate(HomeRootScreen.Multi.route)
                }
            },
            modifier = Modifier
                .align(Alignment.Center)
                //.offset(y = (420).dp)
                .height(170.dp)
                .size(200.dp),

            content = {
                Image(
                    painter = painterResource(id = R.drawable.bouton_start),
                    contentDescription = "start",
                    modifier= Modifier.size(180.dp),
                )
            }
        )
    }
}

@Composable
fun StartButton2(navController: NavHostController){
    val context = LocalContext.current
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
                if (currentIndex <= 0) {
                    currentIndex = 0
                    val soundEffect = MediaPlayer.create(context, R.raw.game_start)
                    soundEffect.start()
                    soundEffect.setOnCompletionListener { mp ->
                        mp.release()
                    }
                    navController.navigate(HomeRootScreen.Duel.route)
                }

                if (currentIndex == 1) {

                    val soundEffect = MediaPlayer.create(context, R.raw.game_start)
                    soundEffect.start()
                    soundEffect.setOnCompletionListener { mp ->
                        mp.release()
                    }
                    navController.navigate(HomeRootScreen.Solo.route)
                }

                if (currentIndex >= 2) {
                    currentIndex = 2
                    val soundEffect = MediaPlayer.create(context, R.raw.game_start)
                    soundEffect.start()
                    soundEffect.setOnCompletionListener { mp ->
                        mp.release()
                    }
                    navController.navigate(HomeRootScreen.Multi.route)
                }
            })

    ){
        Text(text = "Start",
            color = Color.White,
            fontSize = 25.sp,
            fontFamily = fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale
        )
    }
}



@Composable
fun Centre(navController: NavHostController) {
    val parentHeight = remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 200.dp)
            .onGloballyPositioned { coordinates ->
                parentHeight.value = coordinates.size.height
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(parentHeight.value.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center, // Ajout de l'espacement vertical
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val y=(parentHeight.value - 250 - 200)/10
                Spacer(modifier = Modifier.height(y.dp))
                ModeDeJeu()

                StartButton(navController = navController)
                Spacer(modifier = Modifier.height(y.dp))
            }
        }
    }
}


@Composable
fun Centre2(navController: NavHostController){
    val parentHeight = remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 200.dp)
            .onGloballyPositioned { coordinates ->
                parentHeight.value = coordinates.size.height
            }
    ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly, // Ajout de l'espacement vertical
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.height(parentHeight.value.dp)
            ) {

                ModeDeJeu()

                StartButton2(navController = navController)

            }
    }
}




@Composable
fun SettingsRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontFamily = fontPrincipale,
            color = colorResource(id = R.color.white),
            modifier = Modifier.weight(1f) // Aligner le texte à gauche
        )

        Spacer(modifier = Modifier.width(16.dp))

        var checked by remember { mutableStateOf(true) }

        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
            },
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun HorizontalBar(){
    Spacer(modifier = Modifier.height(10.dp))

    HorizontalDivider(
        modifier = Modifier
            .height(10.dp)
            .border(1.dp, colorResource(id = R.color.blue_1), RoundedCornerShape(2.dp)),
        thickness = 5.dp,
        color = colorResource(id = R.color.blue_1)

    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun SettingsWindow(onClose: () -> Unit, sharedPreferences: SharedPreferences, navController: NavHostController) {
    // La taille de la fenêtre modale des paramètres
    val windowWidth = 250.dp
    val windowHeight = 350.dp

    // Contenu de la fenêtre modale des paramètres
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .zIndex(1f)
            .clickable(onClick = onClose)

    ) {

        Box(
            modifier = Modifier
                .size(windowWidth, windowHeight)
                .align(Alignment.TopEnd)
                .padding(3.dp) // Ajouter une marge pour l'espacement
                .background(colorResource(id = R.color.blue_2), RoundedCornerShape(15.dp))
                .clickable(enabled = false, onClickLabel = null, role = null, onClick = {})
                .border(5.dp, colorResource(id = R.color.black), RoundedCornerShape(15.dp))


        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Paramètres",
                        fontSize = 20.sp,
                        fontFamily = fontPrincipale,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.weight(1f) // Aligner le texte à gauche
                    )

                    Spacer(modifier = Modifier.width(16.dp)) // Espacement entre le texte et l'image

                    Image(
                        painter = painterResource(id = R.drawable.roue_parametre),
                        contentDescription = "parametres", // Indiquez une description si nécessaire
                        modifier = Modifier
                            .size(30.dp) // Ajuster la taille de l'image selon vos besoins
                            .clickable(onClick = onClose)
                    )
                }

                HorizontalBar()

                SettingsRow(text = "Effets sonores")

                HorizontalBar()

                SettingsRow(text = "Notifications")

                HorizontalBar()

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Crédits",
                    fontSize = 20.sp,
                    fontFamily = fontPrincipale,
                    color = colorResource(id = R.color.white)
                )

                HorizontalBar()
                Spacer(modifier = Modifier.height(7.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Se déconnecter",
                        fontSize = 20.sp,
                        fontFamily = fontPrincipale,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.weight(1f) // Aligner le texte à gauche
                    )

                    Spacer(modifier = Modifier.width(16.dp)) // Espacement entre le texte et l'image

                    Image(
                        painter = painterResource(id = R.drawable.deconnexion),
                        contentDescription = "deconnexion", // Indiquez une description si nécessaire
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(onClick = {
                                val editor = sharedPreferences.edit();
                                editor.putBoolean("first_login", true);
                                editor.apply();
                                navController.navigate(RootScreen.Login.route)

                            })
                    )
                }

            }
        }
    }
}



@Composable
fun ProfilComponent(textTitre : String, textResultat : String){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 3.dp),
    ) {


        Text(
            text = textTitre,
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = fontPrincipale
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(100.dp)
                .height(25.dp)
                .background(colorResource(id = R.color.black), RoundedCornerShape(15.dp))
        ) {
            Text(
                text = textResultat,
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = fontPrincipale
            )
        }

    }
}

@Composable
fun GridProfilComponent(user : User){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ){

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfilComponent(textTitre = "Victoires Royales", textResultat = user.victory.toString() )
            Spacer(modifier = Modifier.height(16.dp))
            ProfilComponent(textTitre = "Meilleurs Trophées", textResultat =user.peak_trophy.toString())
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfilComponent(textTitre = "Parties Jouées", textResultat = user.game_played.toString() )
            Spacer(modifier = Modifier.height(16.dp))
            ProfilComponent(textTitre = "Catégorie Préférée", textResultat =user.favorite_category)
        }


    }
}

fun getUserId (usernameSearched : String, onGetUserId: (String) -> Unit) {
    val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("utilisateurs")

// Username to search for
    val usernameToSearch = usernameSearched

// Query the database to find the user with the matching username
    usersRef.orderByChild("username").equalTo(usernameToSearch).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                // Loop through the dataSnapshot to get the userId
                var userId: String? = null
                for (snapshot in dataSnapshot.children) {
                    userId = snapshot.key
                    break // Break after finding the first match
                }

                if (userId != null) {
                    println("Found userId for username '$usernameToSearch': $userId")
                    onGetUserId(userId)
                } else {
                    println("UserId not found for username '$usernameToSearch'")
                }
            } else {
                println("No user found with username '$usernameToSearch'")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            println("Error searching for user: $databaseError")
        }
    })

}


fun addFriendfromId(userId : String, username_friend : String, deleteRequest : Boolean){
    val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("utilisateurs/$userId")



        // Friend to add
        val friendUsername = username_friend

        // Fetch the existing user's data
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user_searched = dataSnapshot.getValue(User::class.java)

                if (user_searched != null) {
                    // Update the friends list
                    val updatedFriends = user_searched.friends.toMutableList()
                    updatedFriends.add(friendUsername)

                    // Update the user object with the new friends list
                    user_searched.friends = updatedFriends

                    if (deleteRequest){
                        val updatedRequest = user_searched.friendsRequest.toMutableList()
                        updatedRequest.remove(username_friend)

                        user_searched.friendsRequest = updatedRequest
                    }

                    // Push the updated user object back to the database
                    usersRef.setValue(user_searched)
                        .addOnSuccessListener {
                            // Successfully added friend
                            println("Friend added successfully!")
                        }
                        .addOnFailureListener { e ->
                            // Failed to add friend
                            println("Error adding friend: $e")
                        }
                } else {
                    println("User not found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error fetching user: $databaseError")
            }
        })


}



fun addFriendRequest(userId: String, newUserName: String) {
    val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("utilisateurs/$userId")
    println("userRef $usersRef")
    println("userID $userId")
    // Fetch the existing user's data
    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var existingUser = dataSnapshot.getValue(User::class.java)
            println(existingUser)
            if (existingUser != null) {
                // Check if newUser.username is already in the friendsRequest list
                if (!existingUser.friendsRequest.contains(newUserName)) {
                    // Update the friendsRequest list
                    val updatedFriendsRequest = existingUser.friendsRequest.toMutableList()
                    updatedFriendsRequest.add(newUserName)

                    // Update the existingUser object with the new friendsRequest list
                    existingUser.friendsRequest = updatedFriendsRequest

                    // Push the updatedUser object back to the database
                    usersRef.setValue(existingUser)
                        .addOnSuccessListener {
                            println("Friend request added successfully!")
                        }
                        .addOnFailureListener { e ->
                            println("Error adding friend request: $e")
                        }
                } else {
                    println("Friend request already exists for username '${newUserName}'")
                }
            } else {
                println("User not found")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            println("Error fetching user: $databaseError")
        }
    })
}

fun removeFriendRequest(userId: String, newUserName: String) {
    val database = FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
    val usersRef = database.getReference("utilisateurs/$userId")

    // Fetch the existing user's data
    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var existingUser = dataSnapshot.getValue(User::class.java)

            if (existingUser != null) {
                // Check if newUser.username is in the friendsRequest list
                if (existingUser.friendsRequest.contains(newUserName)) {
                    // Update the friendsRequest list
                    val updatedFriendsRequest = existingUser.friendsRequest.toMutableList()
                    updatedFriendsRequest.remove(newUserName)

                    // Update the existingUser object with the new friendsRequest list
                    existingUser.friendsRequest = updatedFriendsRequest

                    // Push the updatedUser object back to the database
                    usersRef.setValue(existingUser)
                        .addOnSuccessListener {
                            println("Friend request removed successfully!")
                        }
                        .addOnFailureListener { e ->
                            println("Error removing friend request: $e")
                        }
                } else {
                    println("Friend request not found for username '${newUserName}'")
                }
            } else {
                println("User not found")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            println("Error fetching user: $databaseError")
        }
    })
}



@Composable
fun ScrollableColumnWithImages(
    imageList: List<String>,
    onClose: () -> Unit,
    viewModel: GameViewModel
) {
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8F)
                .height(200.dp)
                .align(Alignment.Center)
                .background(colorResource(id = R.color.blue_2), RoundedCornerShape(15.dp))
                .border(
                    width = 4.dp,
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text("Changer d'avatar",
                    fontFamily = fontPrincipale,
                    color = Color.White,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(15.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(imageList) { imageRes ->
                        Image(
                            painter = painterResource(id = getAnyImageId(imageRes, context)),
                            contentDescription = null,
                            modifier = Modifier
                                .height(50.dp)
                                .width((50.dp))
                                .clickable {
                                    // Handle image click here
                                    viewModel.changeAnyAtomicV2(playerIcon = imageRes);
                                    onClose()
                                },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                Button(onClick = onClose) {
                    Text("Fermer")
                }
            }
        }
    }
}

@Composable
fun ProfilWindow(
    onClose: () -> Unit,
    user_display : User,
    isNotFriend: Boolean,
    onDisplayIcons: () -> Unit

    ){
    // La taille de la fenêtre modale des paramètres
    val windowWidth = 350.dp
    val windowHeight = 430.dp

    val context = LocalContext.current

    // Contenu de la fenêtre modale des paramètres
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .zIndex(1f)
            .clickable(onClick = onClose)

    ) {

        Box(
            modifier = Modifier
                .size(windowWidth, windowHeight)
                .align(Alignment.Center)
                .padding(3.dp) // Ajouter une marge pour l'espacement
                .background(colorResource(id = R.color.blue_2), RoundedCornerShape(15.dp))
                .clickable(enabled = false, onClickLabel = null, role = null, onClick = {})
                .border(5.dp, colorResource(id = R.color.black), RoundedCornerShape(15.dp))


        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(
                        text = "Profil du Joueur",
                        fontSize = 20.sp,
                        fontFamily = fontPrincipale,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.weight(1f) // Aligner le texte à gauche
                    )

                    Spacer(modifier = Modifier.width(16.dp)) // Espacement entre le texte et l'image

                    Image(
                        painter = painterResource(id = R.drawable.croix_suppr),
                        contentDescription = "femer", // Indiquez une description si nécessaire
                        modifier = Modifier
                            .size(40.dp) // Ajuster la taille de l'image selon vos besoins
                            .clickable(onClick = onClose)
                    )
                }

                HorizontalBar()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {


                    IconButton(
                        onClick = {
                                  if(user_display.username.equals(user!!.username)){
                                      onClose();
                                      onDisplayIcons()


                                  }
                        },
                        modifier = Modifier.size(70.dp),
                        content = {
                            Image(
                                painter = painterResource(id = user_display.getImageResourceId(context)),
                                contentDescription = null, // Description de l'image si nécessaire
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    )

                    Spacer(modifier=Modifier.width(24.dp))

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 3.dp),
                    ) {


                        Text(
                            text = user_display.username,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = fontPrincipale
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = user_display.title,
                            color = Color.LightGray,
                            fontSize = 16.sp,
                            fontFamily = fontPrincipale
                        )

                    }

                    Spacer(modifier=Modifier.width(24.dp))

                    Text(
                        text = user_display.trophies.toString(),
                        color = Color.White,
                        fontSize = 25.sp,
                        fontFamily = fontPrincipale
                    )

                    Spacer(modifier=Modifier.width(5.dp))

                    Image(
                        painter = painterResource(id = R.drawable.couronne_laurier),
                        contentDescription = null, // Description de l'image si nécessaire
                        modifier = Modifier.size(100.dp)
                    )

                }


                HorizontalBar()

                GridProfilComponent(user_display)

                HorizontalBar()
                if(isNotFriend && user_display.username != (user!!.username)){
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { /*TODO  */ }) {
                            Text(text = "Classement")
                        }

                        Button(onClick = {
                            var userIdCatch:String?= null
                            getUserId(user_display.username,
                                {userId -> userIdCatch = userId;
                                    user?.username.let {
                                        if (it != null) {
                                            addFriendRequest(userIdCatch!!, it)
                                        }
                                    }
                                })
                        }) {
                            Text(text = "Demander en ami")
                        }
                    }
                }
                else{
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Voir le classement")
                    }
                }

            }
        }
    }

}


class GameViewModel : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    fun loadUser(username: String?) {
        viewModelScope.launch {
            username?.let {
                val user = getUserInfoDatabase(it)
                _userState.value = user
            }
        }
    }


 fun changeAnyAtomicV2(
        trophies: Int? = _userState.value?.trophies,
        playerIcon: String? = _userState.value?.playerIcon,
        title: String? = _userState.value?.title,
        connectionState: Boolean? = _userState.value?.connectionState,
        victory: Int? = _userState.value?.victory,
        game_played: Int? = _userState.value?.game_played,
        peak_trophy: Int? = _userState.value?.peak_trophy,
        favorite_category: String? = _userState.value?.favorite_category,
        money: Int? = _userState.value?.money
    ) {
        getUserId(user!!.username) { userId ->
            val database =
                FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.getReference("utilisateurs/$userId")

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var existingUser = dataSnapshot.getValue(User::class.java)

                    if (existingUser != null) {
                        var isUpdated = false


                        if (existingUser.playerIcon != playerIcon) {
                            if (playerIcon != null) {
                                existingUser.playerIcon = playerIcon
                                isUpdated = true
                            }
                        }


                        if (existingUser.title != title) {
                            if (title != null) {
                                existingUser.title = title
                                isUpdated = true
                            }
                        }

                        if (existingUser.victory != victory) {
                            if (victory != null) {
                                existingUser.victory = victory
                                isUpdated = true
                            }
                        }

                        if (existingUser.game_played != game_played) {
                            if (game_played != null) {
                                existingUser.game_played = game_played
                                isUpdated = true
                            }
                        }

                        if (existingUser.peak_trophy != peak_trophy) {
                            if (peak_trophy != null) {
                                existingUser.peak_trophy = peak_trophy
                                isUpdated = true
                            }
                        }

                        if (existingUser.favorite_category != favorite_category) {
                            if (favorite_category != null) {
                                existingUser.favorite_category = favorite_category
                                isUpdated = true
                            }
                        }

                        if (existingUser.money != money) {
                            if (money != null) {
                                existingUser.money = money
                                isUpdated = true
                            }
                        }

                        if (existingUser.connectionState != connectionState) {
                            if (connectionState != null) {
                                existingUser.connectionState = connectionState
                            }
                        }

                        if (existingUser.trophies != trophies) {
                            if (trophies != null) {
                                existingUser.trophies = trophies
                            }
                        }

                        if (isUpdated) {
                            usersRef.setValue(existingUser)
                                .addOnSuccessListener {
                                    // Update the user state only if Firebase update was successful
                                    _userState.value = existingUser
                                }
                                .addOnFailureListener { e ->
                                    println("Error updating user: $e")
                                }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error fetching user: $databaseError")
                }
            })

        }
    }
}

fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("IconNames", Context.MODE_PRIVATE)
}

// Function to load icon names from SharedPreferences, initializes if nothing is saved
fun loadIconNames(context: Context): List<String> {
    val sharedPreferences = getSharedPreferences(context)
    return sharedPreferences.getStringSet("iconNames", null)?.toList()
        ?: listOf(
            "lightning", "lightning_blue", "lightning_black",
            "lightning_red", "lightning_green", "lightning_white",
            "lightning_purple", "lightning_pink"
        )
}

fun addIconName(context: Context, iconName: String) {
    val sharedPreferences = getSharedPreferences(context)
    val iconNames = sharedPreferences.getStringSet("iconNames", mutableSetOf())?.toMutableSet()
        ?: mutableSetOf()

    iconNames.add(iconName)

    val editor = getSharedPreferences(context).edit()
    editor.putStringSet("iconNames", iconNames)
    editor.apply()
}

fun removeIconName(context: Context, iconName: String) {
    val sharedPreferences = getSharedPreferences(context)
    val iconNames = sharedPreferences.getStringSet("iconNames", mutableSetOf())?.toMutableSet()
        ?: mutableSetOf()

    iconNames.remove(iconName)

    val editor = getSharedPreferences(context).edit()
    editor.putStringSet("iconNames", iconNames)
    editor.apply()
}



@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GameScreen(
    state: GameState,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {



    MusicControl()
    val viewModel: GameViewModel = viewModel()
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


    val userState by viewModel.userState.collectAsState()

    val username = sharedPreferences.getString("username", "")

    // Déclenchez le chargement de l'utilisateur
    LaunchedEffect(userState?.username, userState?.money) {
        viewModel.loadUser(username)
    }





    userState?.let { getUser(it) }
    println(user)


    // Gestion des modalités et autres états UI
    var settingsModalVisible by remember { mutableStateOf(false) }
    var profilVisible by remember { mutableStateOf(false) }
    var changeIconVisible by remember { mutableStateOf(false) }
    var icons = loadIconNames(context)
    //removeIconName(context, "pig")
    println(icons)

    // Affichage des fenêtres modales
    if (settingsModalVisible) {
        SettingsWindow(onClose = { settingsModalVisible = false }, sharedPreferences, navController)
    }
    if (profilVisible) {
        if (user != null) {
            ProfilWindow(
                onClose = { profilVisible = false },
                user!!,
                false,
                onDisplayIcons = { changeIconVisible = true }
            )
        }
    }

    BackgroundImage()
    user?.let { Header({ settingsModalVisible = true }, { profilVisible = true }, it) }
    Logo()
    // MainGame()
    // ModeDeJeu()
    // StartButton(navController)
    Centre2(navController)

    if (changeIconVisible) {
        ScrollableColumnWithImages(
            icons, onClose = { changeIconVisible = false },viewModel
        )
    }
}

