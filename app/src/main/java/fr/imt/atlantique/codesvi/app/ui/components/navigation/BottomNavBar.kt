package fr.imt.atlantique.codesvi.app.ui.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
/*
@Composable
fun BottomNavBar(
    currentSelectedScreen: HomeRootScreen,
    navigateToScreen: (HomeRootScreen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .background(color = colorResource(id = R.color.light_blue_200))
            .height(100.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            selected = currentSelectedScreen == HomeRootScreen.Game,
            onClick = { navigateToScreen(HomeRootScreen.Game) },
            alwaysShowLabel = true,
            label = {
                Text(text = "Accueil")
            },
            icon = {
                BottomNavIcon(
                    icon = Icons.Outlined.Games,
                    description = "Accueil"
                )
            }
        )

        NavigationBarItem(
            selected = currentSelectedScreen == HomeRootScreen.Profile,
            onClick = { navigateToScreen(HomeRootScreen.Profile) },
            alwaysShowLabel = true,
            label = { Text(text = "Profil") },
            icon = {
                BottomNavIcon(
                    icon = Icons.Outlined.AccountCircle,
                    description = "Profil"
                )
            }
        )

        NavigationBarItem(
            selected = currentSelectedScreen == HomeRootScreen.Profile, // Je suppose que Social est une autre écran, donc je l'ai changé à HomeRootScreen.Social
            onClick = { navigateToScreen(HomeRootScreen.Profile) },
            alwaysShowLabel = true,
            label = { Text(text = "Social") },
            icon = {
                BottomNavIcon(
                    icon = Icons.Outlined.AccountCircle,
                    description = "Social"
                )
            }
        )
    }
}
*/

@Composable
fun BottomNavBarComponent(
    iconResourceId: Int,
    nom : String,
    onClick: () -> Unit
) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start=3.dp),

    ){
        IconButton(
            onClick = onClick,
            modifier = Modifier
                    .size(80.dp),
            content = {
                Image(
                    painter = painterResource(id = iconResourceId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                )
            }
        )

        val customFontFamily = FontFamily(
            Font(R.font.bubble_bobble) // Remplacez "your_custom_font" par le nom de votre fichier de police
        )

        Text(
            text = nom,
            color = Color.White,
            fontSize = 30.sp,
            fontFamily = customFontFamily

        )

    }
}


@Composable
fun BottomNavBar(
    currentSelectedScreen: HomeRootScreen,
    navigateToScreen: (HomeRootScreen) -> Unit
){
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .height(120.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){



        BottomNavBarComponent(
            iconResourceId = R.drawable.magasin_png,
            nom = "Magasin",
            onClick = { navigateToScreen(HomeRootScreen.Shop) }
        )

        BottomNavBarComponent(
            iconResourceId = R.drawable.cerveau,
            nom = "Jouer",
            onClick = { navigateToScreen(HomeRootScreen.Game) }
        )

        BottomNavBarComponent(
            iconResourceId = R.drawable.social,
            nom = "Social",
            onClick = {  navigateToScreen(HomeRootScreen.Profile)  }
        )



    }
}
