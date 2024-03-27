import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.solo.SoloState




val customFontFamily = FontFamily(
    Font(R.font.bubble_bobble) // Remplacez "your_custom_font" par le nom de votre fichier de police
)

@Composable
fun Background()
{
    Image(
        painter = painterResource(id = R.drawable.fond),
        contentDescription = null, // Indiquez une description si nécessaire
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun BoxWithTextAndImage(
    title: String,
    imageResource: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(8.dp)
            )
            .width(30.dp)
            .height(90.dp)
            .border(
                5.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable (onClick= {
                navController.navigate(HomeRootScreen.Question.route);
                fr.imt.atlantique.codesvi.app.ui.screens.question.StartQuestion(title, "Solo",imageResource)
            })
    ) {
        Row(
        horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 15.dp)
                .padding(end = 15.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontFamily = customFontFamily,
                fontSize = 20.sp
            )

            Spacer(Modifier.width(10.dp))
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp) // Reduced padding
                    .size(40.dp) // Reduced size for the image

            )
        }
    }
}

@Composable
fun BoxesGrid(
    navController: NavHostController
) {
    val titles = listOf(
        "Histoire", "Géographie", "Musique", "Films","Littérature", "Autres arts",
        "Sport", "Jeux vidéo", "Sciences", "Tout thème"
    )

    val imageResources = listOf(
        R.drawable.histoire, R.drawable.geographie, R.drawable.musique,
        R.drawable.film, R.drawable.litterature, R.drawable.autres_arts,
        R.drawable.sport, R.drawable.jeux_video, R.drawable.science,
        R.drawable.cerveau
    )

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mode Solo",
            color = Color.White,
            fontSize = 50.sp,

            fontFamily = customFontFamily,
            modifier = Modifier.padding(bottom = 5.dp),
        )
        Text(
            text = "Choisissez un thème !",
            color = Color.White,
            fontSize = 30.sp,

            fontFamily = customFontFamily,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        Spacer(modifier = Modifier.height(10.dp))
        var index = 0
        while (index < titles.size) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BoxWithTextAndImage(
                    title = titles[index],
                    imageResource = imageResources[index],
                    modifier = Modifier
                        .weight(1f),
                    navController

                )
                index++

                if (index < titles.size) {
                    BoxWithTextAndImage(
                        title = titles[index],
                        imageResource = imageResources[index],
                        modifier = Modifier
                            .weight(1f),
                        navController

                    )
                    index++
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Space between rows
        }
    }
}

// exit button ----------------------------------------------------------
@Composable
fun PopUpBox(
    navController: NavHostController,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(), // Fill the entire screen
        color = Color.Black.copy(alpha = 0.5f), // Semi-transparent black color for overlay effect
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ) // Background color of the card
                    .border(
                        8.dp, color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) // Border color and width
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Voulez-vous vraiment quitter le mode solo ?",
                        textAlign = TextAlign.Center,
                        fontFamily = customFontFamily,
                        fontSize = 20.sp,
                        color=Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(HomeRootScreen.Game.route)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )

                        ) {
                            Text(
                                text = "Quitter",
                                fontFamily = customFontFamily,
                                color=Color.Black,
                            )
                        }

                        Spacer(modifier=Modifier.width(10.dp))

                        Button(
                            onClick = { onCancel() },
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(
                                text ="Annuler",
                                fontFamily = customFontFamily,
                                color=Color.Black,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExitButton(
    onPopUp: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(end = 16.dp, top = 16.dp) // Add padding to position button in top right corner
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End, // Align items to the end (right) of the row
        verticalAlignment = Alignment.Top // Align items to the top of the row
    ) {
        IconButton(
            onClick = onPopUp,
            modifier = Modifier.size(30.dp) // Set size of the button
        ) {
            Image(
                painter = painterResource(id = R.drawable.croix),
                contentDescription = "Exit",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}







@Composable
fun SoloScreen(
    state : SoloState,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Background()
    BoxesGrid(navController)

    // pop up box
    var isPopUpVisible by remember { mutableStateOf(false) }

    // Function to trigger appearance of the pop-up box
    val triggerPopUp = { isPopUpVisible = true }

    // Call the PopUpButton composable and pass the triggerPopUp function
    ExitButton(
        onPopUp = triggerPopUp
    )

    // Display the pop-up box if isPopUpVisible is true
    if (isPopUpVisible) {
        PopUpBox(
            navController,
            onCancel = { isPopUpVisible = false }
        )
    }
}
