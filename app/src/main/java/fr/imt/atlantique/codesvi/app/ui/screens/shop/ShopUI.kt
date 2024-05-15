package fr.imt.atlantique.codesvi.app.ui.screens.shop

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R

import fr.imt.atlantique.codesvi.app.data.model.MusicControl
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen

import fr.imt.atlantique.codesvi.app.data.model.User

import fr.imt.atlantique.codesvi.app.ui.screens.game.HorizontalBar
import fr.imt.atlantique.codesvi.app.ui.screens.game.ProfilWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.SettingsWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.addIconName
import fr.imt.atlantique.codesvi.app.ui.screens.game.getAnyImageId
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserId
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.game.loadIconNames
import fr.imt.atlantique.codesvi.app.ui.screens.game.user
import fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale

import timber.log.Timber

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class IconModel(val iconSourceName: String, val iconName: String, val iconPrice: Int,val iconFunction: (IconModel)->Unit)
@Composable
fun IconComponent(
    icon: IconModel,
    context: Context
){

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        IconButton(
            onClick = { icon.iconFunction(icon) },
            modifier = Modifier
                .size(80.dp),
            content = {
                Image(
                    painter = painterResource(id = getAnyImageId(icon.iconSourceName, context)),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                )
            }
        )

        val customFontFamily = FontFamily(
            Font(R.font.bubble_bobble) // Remplacez "your_custom_font" par le nom de votre fichier de police
        )

        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(90.dp)){
            Text(
                text = icon.iconPrice.toString(),
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = customFontFamily
            )

            Image(
                painter = painterResource(id = R.drawable.piece),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
            )
        }

    }

}

@Composable
fun Main(iconList: List<IconModel>, context: Context) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 60.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "MAGASIN",
            color = Color.White,
            fontSize = 50.sp,
            fontFamily = fontPrincipale
        )
        Spacer(Modifier.height(50.dp))

        /*
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth())
        {

            IconComponent(iconResourceId = R.drawable.croix_suppr, iconName = "", iconPrice = "110", iconFunction = infoBuyOnClick)

            IconComponent(iconResourceId = R.drawable.croix_suppr, iconName = "", iconPrice = "320", iconFunction = infoBuyOnClick)

            IconComponent(iconResourceId = R.drawable.croix_suppr, iconName = "", iconPrice = "900", iconFunction = infoBuyOnClick)
        }*/

        IconList(icons = iconList, context)

        /*Spacer(Modifier.height(30.dp))


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier
                .width(320.dp)
                .height(100.dp)
                .background(color = Color.Green, RoundedCornerShape(15.dp))
                .border(width = 2.dp, color = Color.Black, RoundedCornerShape(15.dp))
            ){

            }

            Spacer(Modifier.height(5.dp))

            val customFontFamily = FontFamily(
                Font(R.font.bubble_bobble) // Remplacez "your_custom_font" par le nom de votre fichier de police
            )

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.width(90.dp)){
                Text(
                    text = "1000",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = customFontFamily
                )

                Image(
                    painter = painterResource(id = R.drawable.piece),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )
            }


        }*/
    }
}



@SuppressLint("UnrememberedMutableState")
@Composable
fun IconList(icons: List<IconModel>,
             context: Context) {

    val scrollState = rememberLazyListState()


    var sortedIcons1 = icons.sortedBy { it.iconPrice }
    /*if (icons.size % 2 == 1) {
        val sortedIcons2 = listOf(sortedIcons1.value.last())
        sortedIcons1.toList().removeAt()

    }*/
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            //.padding(top = 210.dp),
    ){
        val max = if (sortedIcons1.size%2 == 0) sortedIcons1.size/2 else sortedIcons1.size/2 +1
        items(max) { index ->
            val icone1 = sortedIcons1[index * 2]
            val icone2: IconModel? = sortedIcons1.getOrNull(index * 2 + 1)

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth())
            {

                IconComponent(icone1, context)


                icone2?.let { IconComponent(it, context) }


            }

            Spacer(Modifier.height(30.dp))
        }
    }

}
fun isBuyable(icon: IconModel, iconNameList: List<String>): Boolean {
    return !(iconNameList.contains(icon.iconSourceName)) && user!!.money > icon.iconPrice
}

@Composable
fun InfoBuyWindow(
    onClose: () -> Unit,
    icon: IconModel,
    context: Context,
    viewModel: GameViewModel
){
    // La taille de la fenêtre modale des paramètres
    val windowWidth = 350.dp
    val windowHeight = 430.dp

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
                        text = "Achât d'icone de joueur",
                        fontSize = 20.sp,
                        fontFamily = fr.imt.atlantique.codesvi.app.ui.screens.game.fontPrincipale,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.weight(1f) // Aligner le texte à gauche
                    )

                    Spacer(modifier = Modifier.width(16.dp)) // Espacement entre le texte et l'image

                    Image(
                        painter = painterResource(id = R.drawable.croix_suppr),
                        contentDescription = "fermer", // Indiquez une description si nécessaire
                        modifier = Modifier
                            .size(40.dp) // Ajuster la taille de l'image selon vos besoins
                            .clickable(onClick = onClose)
                    )
                }

                HorizontalBar()

                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {


                    Image(
                        painter = painterResource(id = getAnyImageId(icon.iconSourceName, context)),
                        contentDescription = "", // Indiquez une description si nécessaire
                        modifier = Modifier
                            .size(100.dp) // Ajuster la taille de l'image selon vos besoins
                    )

                    Text(
                        text = icon.iconName,
                        fontSize = 30.sp,
                        fontFamily = fr.imt.atlantique.codesvi.app.ui.screens.game.fontPrincipale,
                        color = colorResource(id = R.color.white),
                    )


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.width(90.dp)
                    ) {
                        Text(
                            text = icon.iconPrice.toString(),
                            color = Color.White,
                            fontSize = 30.sp,
                            fontFamily = fontPrincipale
                        )

                        Image(
                            painter = painterResource(id = R.drawable.piece),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                    val buyable = isBuyable(icon, loadIconNames(context))

                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .width(300.dp)
                            .height(70.dp)
                            //.padding(bottom = 16.dp)
                            .background(
                                color = if (buyable) MaterialTheme.colorScheme.secondary else Color.Gray,
                                RoundedCornerShape(15.dp)
                            )
                            .border(
                                5.dp,
                                color = MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(15.dp)
                            )

                            .clickable(enabled = buyable, onClick = {
                                viewModel.changeAnyAtomicV2(
                                    money = user!!.money - icon.iconPrice
                                )
                                addIconName(context, icon.iconSourceName)
                                onClose()
                            })

                    ){
                        Text(text = "Acheter",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontFamily = fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale,

                        )

                    }


                }
            }
        }
    }

}


// impossible de l'importer

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







@Composable
fun ShopScreen(
    state : ShopState,
    modifier: Modifier = Modifier,
    navController : NavHostController
) {

    MusicControl()

    val viewModel = GameViewModel()

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
        user?.let { ProfilWindow(onClose = { profilVisible = false }, it, false, {}) }
    }


    //Permet de gérer l'affichage ou non de la fenêtre de profil
    var infoBuyVisible by remember { mutableStateOf(false) }
    var iconModelBuyVisible by remember {
        mutableStateOf(
            IconModel(
                iconSourceName = "lightning",
                iconName = "Default Icon",
                iconPrice = Int.MAX_VALUE,
                iconFunction = {}
            )
        )
    }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (infoBuyVisible) {
        InfoBuyWindow(onClose = { infoBuyVisible = false }, iconModelBuyVisible, context, viewModel )
    }

    fr.imt.atlantique.codesvi.app.ui.screens.game.BackgroundImage()
    user?.let {
        fr.imt.atlantique.codesvi.app.ui.screens.game.Header({ settingsModalVisible = true },
            { profilVisible = true }, it
        )
    }



    // Déclenchez le chargement de l'utilisateur
   LaunchedEffect(user!!.money) {
        viewModel.loadUser(user!!.username)
    }


    // Shop database en dur
    val icon1 = IconModel(
        iconSourceName = "frog",
        iconName = "Grenouille sympa",
        iconPrice = 200,
        iconFunction = {iconModel ->  infoBuyVisible = true; iconModelBuyVisible = iconModel}
    )
    val icon2 = IconModel(
        iconSourceName = "pig",
        iconName = "Le père du cochonnet",
        iconPrice = 200,
        iconFunction = {iconModel ->  infoBuyVisible = true; iconModelBuyVisible = iconModel}
    )
    val icon3 = IconModel(
        iconSourceName = "duck",
        iconName = "Donald",
        iconPrice = 400,
        iconFunction = {iconModel ->  infoBuyVisible = true; iconModelBuyVisible = iconModel}
    )
    val icon4 = IconModel(
        iconSourceName = "bull",
        iconName = "Chicagoen",
        iconPrice = 400,
        iconFunction = {iconModel ->  infoBuyVisible = true; iconModelBuyVisible = iconModel}
    )
    val icon5 = IconModel(
        iconSourceName = "panda",
        iconName = "\"On s'fait un bambou ?\"",
        iconPrice = 1000,
        iconFunction = {iconModel ->  infoBuyVisible = true; iconModelBuyVisible = iconModel}
    )
    val icon6 = IconModel(
        iconSourceName = "dragon",
        iconName = "\"J'suis en feu !\"",
        iconPrice = 1000,
        iconFunction = {iconModel ->  infoBuyVisible = true; iconModelBuyVisible = iconModel}
    )
    val icon7 = IconModel(
        iconSourceName = "logo",
        iconName = "Zzzzzap Quiz",
        iconPrice = 3000,
        iconFunction = {iconModel ->  infoBuyVisible = true; iconModelBuyVisible = iconModel}
    )


    val iconList: List<IconModel> = listOf(
        icon1, icon2, icon3, icon4, icon5, icon6, icon7
        )



    Main(iconList, context)

}
