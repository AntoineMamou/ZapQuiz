package fr.imt.atlantique.codesvi.app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.ui.screens.game.ProfilWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.SettingsWindow
import fr.imt.atlantique.codesvi.app.ui.screens.game.fontChiffre
import timber.log.Timber

val fontPrincipale = FontFamily(Font(R.font.bubble_bobble))



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(){

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top=60.dp)
    ) {
        Text(
            text = "Amis",
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = fontPrincipale
        )
        Spacer(Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()

        ){
            Image(
                painter = painterResource(id = R.drawable.ajouter_ami),
                contentDescription = null, // Description de l'image si nécessaire
                modifier = Modifier.width(60.dp)
            )

            Spacer(Modifier.width(12.dp))

            var searchText by remember { mutableStateOf(TextFieldValue()) }

            Box(
                modifier=Modifier.background(
                    color = MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(15.dp)
                )
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text(text = "Rechercher ...") },
                    modifier = Modifier
                        .width(215.dp)
                        .border(
                            5.dp,
                            color = MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(15.dp)
                        ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = Color.Transparent, // Couleur transparente pour le curseur
                        unfocusedBorderColor =  Color.Transparent, // Couleur transparente pour l'indicateur de focus
                        focusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.White,
                        unfocusedPlaceholderColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done // Définir l'action IME sur "Done"
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        // Exécuter votre fonction ici
                        searchText= TextFieldValue("")
                    }),
                )
            }
        }
    }
}


@Composable
fun AfficheListe(amis: List<String>) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 190.dp)
    ) {
        items(amis.size) { index ->
            if (index >= 3) { // Limite le nombre d'éléments affichés à partir de l'index 3

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(15.dp))
                        .width(300.dp)
                        .height(70.dp)
                        .border(5.dp, color=MaterialTheme.colorScheme.primary, RoundedCornerShape(15.dp))


                ){

                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .width(100.dp)

                    ){

                        Image(
                            painter = painterResource(id = R.drawable.magasin_png),
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
                        text = "2900",
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








@Composable
fun ProfileScreen(
    state : ProfileState,
    modifier: Modifier = Modifier
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


    fr.imt.atlantique.codesvi.app.ui.screens.game.BackgroundImage()
    fr.imt.atlantique.codesvi.app.ui.screens.game.Header({ settingsModalVisible = true },
        { profilVisible = true })
    Main()


    val listeAmis = remember {
        listOf(
            "Alice", "Bob", "Charlie", "David", "Eve",
            "Frank", "Grace", "Hannah", "Isaac", "Jack",
            "Kate", "Liam", "Mia", "Noah", "Olivia",
            "Paul", "Quinn", "Rachel", "Sam", "Taylor",
            "Uma", "Victor", "Wendy", "Xavier", "Yara",
            "Zach", "Abby", "Ben", "Chloe", "Dylan",
            "Emma", "Finn", "Gina", "Hector", "Ivy",
            "Jake", "Kylie", "Leo", "Megan", "Nathan",
            "Oscar", "Piper", "Quentin", "Ruby", "Seth",
            "Tina", "Vincent", "Willow", "Xander", "Yasmine"
        )
    }

    AfficheListe(amis = listeAmis)
}