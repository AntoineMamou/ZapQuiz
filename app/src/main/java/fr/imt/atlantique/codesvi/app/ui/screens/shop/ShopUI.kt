package fr.imt.atlantique.codesvi.app.ui.screens.shop

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import androidx.navigation.NavHostController
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.game.HorizontalBar
import fr.imt.atlantique.codesvi.app.ui.screens.game.ProfilWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.SettingsWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.currentIndex
import fr.imt.atlantique.codesvi.app.ui.screens.game.user
import fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale


@Composable
fun IconComponent(
    iconResourceId: Int,
    iconName : String,
    iconPrice : String,
    iconFunction : (Int) -> Unit
){

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        IconButton(
            onClick = { iconFunction(iconResourceId) },
            modifier = Modifier
                .size(80.dp),
            content = {
                Image(
                    painter = painterResource(id = iconResourceId),
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
                text = iconPrice,
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
fun Main(iconList: List<IconModel>) {
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

        IconList(icons = iconList)

        Spacer(Modifier.height(30.dp))


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


        }
    }
}

data class IconModel(val iconResourceId: Int, val iconName: String, val iconPrice: Int,val iconFunction: (Int)->Unit)

@SuppressLint("UnrememberedMutableState")
@Composable
fun IconList(icons: List<IconModel>) {

    val scrollState = rememberLazyListState()


    var sortedIcons1 = icons.sortedBy { it.iconPrice }
    /*if (icons.size % 2 == 1) {
        val sortedIcons2 = listOf(sortedIcons1.value.last())
        sortedIcons1.toList().removeAt()

    }*/
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            //.padding(top = 210.dp),
    ){
        items(sortedIcons1.size/2) { index ->
            val icone1 = sortedIcons1[index * 2]
            val icone2: IconModel? = sortedIcons1.getOrNull(index * 2 + 1)

            val iconResourceId1 = icone1.iconResourceId
            val iconName1 = icone1.iconName
            val iconPrice1 = icone1.iconPrice
            val iconFunction1 = icone1.iconFunction




            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth())
            {

                IconComponent(iconResourceId = iconResourceId1, iconName = iconName1, iconPrice = iconPrice1.toString(), iconFunction = iconFunction1)

                if(icone2 != null) {
                    val iconResourceId2 = icone2.iconResourceId
                    val iconName2 = icone2.iconName
                    val iconPrice2 = icone2.iconPrice
                    val iconFunction2 = icone2.iconFunction
                    IconComponent(iconResourceId = iconResourceId2, iconName = iconName2, iconPrice = iconPrice2.toString(), iconFunction = iconFunction2)
                }

            }

            Spacer(Modifier.height(30.dp))
        }
    }

}


@Composable
fun InfoBuyWindow(onClose: () -> Unit){
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
                        painter = painterResource(id = R.drawable.croix_suppr),
                        contentDescription = "", // Indiquez une description si nécessaire
                        modifier = Modifier
                            .size(100.dp) // Ajuster la taille de l'image selon vos besoins
                    )

                    Text(
                        text = "Icone numéro 1",
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
                            text = "1000",
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

                    ){
                        Text(text = "Acheter",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontFamily = fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale
                        )
                    }


                }
            }
        }
    }

}

@Composable
fun ShopScreen(
    state : ShopState,
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
        user?.let { ProfilWindow(onClose = { profilVisible = false }, it, false, {}) }
    }


    //Permet de gérer l'affichage ou non de la fenêtre de profil
    var infoBuyVisible by remember { mutableStateOf(false) }

    // Afficher la fenêtre modale des paramètres si settingsModalVisible est vrai
    if (infoBuyVisible) {
        InfoBuyWindow(onClose = { infoBuyVisible = false })
    }

    fr.imt.atlantique.codesvi.app.ui.screens.game.BackgroundImage()
    user?.let {
        fr.imt.atlantique.codesvi.app.ui.screens.game.Header({ settingsModalVisible = true },
            { profilVisible = true }, it
        )
    }


    val iconList: List<IconModel> = listOf(
        IconModel(
            iconResourceId = R.drawable.croix_suppr,
            iconName = "Icon Name 1",
            iconPrice = 110,
            iconFunction = { infoBuyVisible = true }
        ),
        IconModel(
            iconResourceId = R.drawable.croix_suppr,
            iconName = "Icon Name 2",
            iconPrice = 320,
            iconFunction =  { infoBuyVisible = true }
        ),
        IconModel(
            iconResourceId = R.drawable.croix_suppr,
            iconName = "Icon Name 3",
            iconPrice = 900,
            iconFunction = { infoBuyVisible = true }
        ),
        IconModel(
            iconResourceId = R.drawable.croix_suppr,
            iconName = "Icon Name 4",
            iconPrice = 110,
            iconFunction = { infoBuyVisible = true }
        ),
        IconModel(
            iconResourceId = R.drawable.croix_suppr,
            iconName = "Icon Name 5",
            iconPrice = 320,
            iconFunction =  { infoBuyVisible = true }
        ),
        IconModel(
            iconResourceId = R.drawable.croix_suppr,
            iconName = "Icon Name 6",
            iconPrice = 900,
            iconFunction = { infoBuyVisible = true }
        ),

        )



    Main(iconList)

}
