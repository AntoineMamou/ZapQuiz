package fr.imt.atlantique.codesvi.app.ui.screens.game

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.imt.atlantique.codesvi.app.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.imt.atlantique.codesvi.app.ui.navigation.AppState
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.navigation.rememberAppState
import fr.imt.atlantique.codesvi.app.ui.screens.shop.ShopState
import fr.imt.atlantique.codesvi.app.ui.screens.solo.rememberSoloState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.format.TextStyle


val  fontPrincipale = FontFamily(Font(R.font.bubble_bobble))
val fontChiffre  = FontFamily(Font(R.font.bubble_bobble))

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
fun Header(settingsonClick: () -> Unit , profilOnClick: ()->Unit) {


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
                        painter = painterResource(id = R.drawable.magasin_png),
                        contentDescription = null, // Description de l'image si nécessaire
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )

            Text(
                text = "Username",
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
                    text = "100",
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
    val itemWidth = 380.dp // Largeur de chaque élément
    //REGLER LE PROBLEME POUR ACCEDER A LA TAILLE DE L'ECRAN DU JOUEUR
    // val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenWidth = 400.dp
    val spacing = (screenWidth - itemWidth) / 2


    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        state = scrollState,
        userScrollEnabled = false,
        modifier = Modifier
            .fillMaxWidth()
            //.padding(top = 190.dp)
            .padding(start = spacing)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, _) = dragAmount

                    // Annuler la tâche précédente
                    job?.cancel()
                    job = coroutineScope.launch {
                        // Attendre les prochains changements de gestes
                        delay(200)

                        // Bien que nous n'ayons pas besoin de balayages à droite et à gauche
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
        val boxWidth = 390.dp
        val boxHeight = 250.dp



        items(3) { index ->

            Box(
                modifier = Modifier
                    .width(boxWidth)
                    .height(boxHeight)
                    .padding(end = 10.dp)
                    .background(getColorForIndex(index)),
                content = {
                    Text(
                        text = "Box ${index + 1}",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
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
        else -> Color.Green
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
fun Centre(navController: NavHostController) {
    val parentHeight = remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top=200.dp)
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
fun SettingsWindow(onClose: () -> Unit) {
    // La taille de la fenêtre modale des paramètres
    val windowWidth = 250.dp
    val windowHeight = 350.dp

    // Contenu de la fenêtre modale des paramètres
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0f))
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
                        modifier = Modifier.size(40.dp) // Ajuster la taille de l'image selon vos besoins
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
fun GridProfilComponent(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ){

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfilComponent(textTitre = "Victoires", textResultat = "150" )
            Spacer(modifier = Modifier.height(16.dp))
            ProfilComponent(textTitre = "Meilleurs Trophées", textResultat ="1600")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfilComponent(textTitre = "Parties Jouées", textResultat = "200" )
            Spacer(modifier = Modifier.height(16.dp))
            ProfilComponent(textTitre = "Catégorie Préférée", textResultat ="Histoire")
        }


    }
}


@Composable
fun ProfilWindow(onClose: () -> Unit){
    // La taille de la fenêtre modale des paramètres
    val windowWidth = 350.dp
    val windowHeight = 430.dp

    // Contenu de la fenêtre modale des paramètres
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0f))
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
                        onClick = { },
                        modifier = Modifier.size(70.dp),
                        content = {
                            Image(
                                painter = painterResource(id = R.drawable.magasin_png),
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
                            text = "Username",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = fontPrincipale
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "ID : #2J3S0",
                            color = Color.LightGray,
                            fontSize = 16.sp,
                            fontFamily = fontPrincipale
                        )

                    }

                    Spacer(modifier=Modifier.width(24.dp))

                    Text(
                        text = "1560",
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

                GridProfilComponent()

                HorizontalBar()

                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Voir le classement")
                }

            }
        }
    }

}





@Composable
fun GameScreen(
    state : GameState,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {



    //Permet de gérer l'affichage ou non de la fenêtre des paramètres
    var settingsModalVisible by remember { mutableStateOf(false) }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (settingsModalVisible) {

        SettingsWindow(onClose = { settingsModalVisible = false })
    }


    //Permet de gérer l'affichage ou non de la fenêtre de profil
    var profilVisible by remember { mutableStateOf(false) }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (profilVisible) {
        ProfilWindow(onClose = { profilVisible = false })
    }


    BackgroundImage()
    Header ({ settingsModalVisible = true} , {profilVisible=true })
    Logo()
    //MainGame()
    //ModeDeJeu()
    //StartButton(navController)
    Centre(navController)






}


