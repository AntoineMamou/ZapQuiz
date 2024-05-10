
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.User
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserId
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.game.user
import fr.imt.atlantique.codesvi.app.ui.screens.solo.SoloState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


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

// Define the list of titles
val titles = listOf(
    "Histoire", "Géographie", "Musique", "Cinéma","Littérature", "Autres arts",
    "Sport", "Jeux vidéo", "Sciences", "Tout thème"
)



fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("ClickCounts", Context.MODE_PRIVATE)
}

// Function to load click counts from SharedPreferences
fun loadClickCounts(context: Context): MutableMap<String, Int> {
    val sharedPreferences = getSharedPreferences(context)
    val titleClickCount = mutableMapOf<String, Int>()

    titles.forEach { title ->
        titleClickCount[title] = sharedPreferences.getInt(title, 0)
    }

    return titleClickCount
}

// Function to save click counts to SharedPreferences
fun saveClickCounts(context: Context, titleClickCount: Map<String, Int>) {
    val editor = getSharedPreferences(context).edit()

    titleClickCount.forEach { (title, count) ->
        editor.putInt(title, count)
    }

    editor.apply()
}

fun getTitleWithMostClick(titleClickCount: Map<String, Int>): String? {
    var maxTitle: String? = null
    var maxClicks = Int.MIN_VALUE

    for ((title, clicks) in titleClickCount) {
        if (clicks > maxClicks) {
            maxTitle = title
            maxClicks = clicks
        }
    }

    return maxTitle
}

@Composable
fun BoxWithTextAndImage(
    title: String,
    imageResource: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    titleClickCount: MutableMap<String, Int>,
    context: Context
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
            .clickable(onClick = {
                titleClickCount[title] = titleClickCount[title]!! + 1;
                saveClickCounts(context, titleClickCount)
                navController.navigate(HomeRootScreen.Question.route);
                fr.imt.atlantique.codesvi.app.ui.screens.question.StartQuestion(
                    title,
                    "Solo",
                    imageResource
                )
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
    navController: NavHostController,
    titleClickCount: MutableMap<String, Int>,
    context: Context
) {
    val titles = listOf(
        "Histoire", "Géographie", "Musique", "Cinéma","Littérature", "Autres arts",
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
                    navController,
                    titleClickCount,
                    context

                )
                index++

                if (index < titles.size) {
                    BoxWithTextAndImage(
                        title = titles[index],
                        imageResource = imageResources[index],
                        modifier = Modifier
                            .weight(1f),
                        navController,
                        titleClickCount,
                        context

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
                        colorResource(id = R.color.blue_2),
                        shape = RoundedCornerShape(8.dp)
                    ) // Background color of the card
                    .border(
                        4.dp, color = Color.Black,
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

//IMPOSSIBLE DE L'IMPORTER C RELOU
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
 fun SoloScreen(
    state: SoloState,
    modifier: Modifier = Modifier,
    navController: NavHostController
    ) {
        val context = LocalContext.current

        var titleClickCount = loadClickCounts(context)
        val preferedCategory = getTitleWithMostClick(titleClickCount)

        val viewModel = GameViewModel()
        val userState by viewModel.userState.collectAsState()

        if (preferedCategory != user!!.favorite_category) {
            viewModel.changeAnyAtomicV2(favorite_category = preferedCategory)
        }

        Background()
        BoxesGrid(navController, titleClickCount, context)


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
