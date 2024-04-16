package fr.imt.atlantique.codesvi.app.ui.screens.multi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.imt.atlantique.codesvi.app.R
import fr.imt.atlantique.codesvi.app.data.model.User
import fr.imt.atlantique.codesvi.app.ui.navigation.HomeRootScreen
import fr.imt.atlantique.codesvi.app.ui.screens.profile.fontPrincipale
import fr.imt.atlantique.codesvi.app.ui.screens.question.Background
import fr.imt.atlantique.codesvi.app.ui.screens.question.*
import kotlinx.coroutines.delay


@Composable
fun ScreenChanger(
    usersScores: List<Pair<User, Int>>,
    nextScreen: String,
    current: String,
    navController: NavHostController
) {
    var currentScreen by remember { mutableStateOf(current) }
    val users = usersScores.map { it.first }
    // Utilisation de LaunchedEffect pour changer l'écran après 5 secondes
    LaunchedEffect(true) {
        delay(5000) // Délai de 5000 ms (5 secondes)
        currentScreen = nextScreen // Changer l'écran
    }

    // Afficher le contenu en fonction de l'écran actuel
    when (currentScreen) {
        "Home" -> Home(users)
        "nextRound" -> WaitingRound(1,usersScores,navController)

    }
}



@Composable
fun DisplayScore(user: User,score : Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(15.dp)
            )
            .width(300.dp)
            .height(100.dp)
            .border(
                5.dp,
                color = MaterialTheme.colorScheme.primary,
                RoundedCornerShape(15.dp)
            )
    ) {





            Image(
                painter = painterResource(id = user.playerIcon),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.username,
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = fontPrincipale
            )


        if (score>0) {
            Text(
                text = "$score point",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = fontPrincipale
            )
        }
        else{
            Text(
                text = "$score points",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = fontPrincipale
            )
        }
    }
}


@Composable
fun WaitingRound(
    roundNumber: Int,
    usersScores: List<Pair<User, Int>>,
    navController: NavHostController
) {
    var currentScreen by remember { mutableStateOf("nextRound") }
    // Utilisation de LaunchedEffect pour changer l'écran après 5 secondes
    LaunchedEffect(true) {
        delay(5000) // Délai de 5000 ms (5 secondes)
        currentScreen = "Question" // Changer l'écran
    }

    // Afficher le contenu en fonction de l'écran actuel
    when (currentScreen) {
        "nextRound" -> Round(roundNumber, usersScores)
        "Question" -> PlayQuestion(usersScores,navController)

    }
}

@Composable
fun PlayQuestion(usersScores: List<Pair<User, Int>>, navController: NavHostController) {
    navController.navigate(HomeRootScreen.Question.route);
    StartQuestion("Histoire", "Multi", R.drawable.multi)
}

@Composable
fun Round(roundNumber: Int, usersScores: List<Pair<User, Int>>) {

    val sortedScores = usersScores.sortedByDescending { it.second }
    Column(verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Manche $roundNumber",
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = fontPrincipale
        )
        sortedScores.forEach { x ->
            DisplayScore(x.first,x.second)
        }
    }
}

@Composable
fun Test() {
    Text(text = "bonjour")
}


@Composable
fun HomeComponent(user:User){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(15.dp)
                )
                .fillMaxWidth()
                .height(100.dp)
                .border(
                    5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(15.dp)
                )
        ) {


            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .width(100.dp)
            ) {


                Image(
                    painter = painterResource(id = user.playerIcon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = user.username,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = fontPrincipale
                )




            }

            Text(
                text = user.title,
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = fontPrincipale
            )

            /*Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,) {
                Text(
                    text = user.trophies.toString(),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontFamily = fontPrincipale,
                    modifier = Modifier.padding(end = 10.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.couronne_laurier),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }*/


        }

        //Spacer(modifier = Modifier.height(32.dp)) // Espacement vertical uniforme
}


@Composable
fun Home(users: List<User>) {
    Column(verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {
        HomeComponent(user = users[0])
        HomeComponent(user = users[1])
        HomeComponent(user = users[2])
        HomeComponent(user = users[3])
        HomeComponent(user = users[4])
    }
}



@Composable
fun MultiScreen(
    state: MultiState,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    Background()

    val user1 = User("Antoine", "password", 1000, 2131099725, "Zappeur professionnel", true, listOf(), 0, 0, 1000, "Histoire", 100)
    val user2 = User("Loic", "password", 420, 2131099725, "Zappeur débutant", true, listOf(), 0, 0, 1000, "Histoire", 100)
    val user3 = User("Titouan", "password", 4000, 2131099725, "Zappeur intermediaire", true, listOf(), 0, 0, 1000, "Histoire", 100)
    val user4 = User("Alexia", "password", 3000, 2131099725, "Zappeur intermediaire", true, listOf(), 0, 0, 1000, "Histoire", 100)
    val user5 = User("Julien", "password", 600, 2131099725, "Zappeur au top", true, listOf(), 0, 0, 1000, "Histoire", 100)
    
    //Home(listOf(user1, user2, user3,user4,user5))

    val pair1: Pair<User, Int> = Pair(user1, 10)
    val pair2: Pair<User, Int> = Pair(user2, 20)
    val pair3: Pair<User, Int> = Pair(user3, 190)
    val pair4: Pair<User, Int> = Pair(user4, 3)
    val pair5: Pair<User, Int> = Pair(user5, 0)

    ScreenChanger(usersScores= listOf(pair1,pair2,pair3,pair4,pair5),"nextRound","Home",navController)
}