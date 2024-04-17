package fr.imt.atlantique.codesvi.app

import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import fr.imt.atlantique.codesvi.app.ui.MainUI

import timber.log.Timber



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashscreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            MusicPlayer()
            MainUI()
            Timber.d("setContent called")

        }
    }
}

@Composable
fun MusicPlayer() {
    val mediaPlayer = MediaPlayer.create(LocalContext.current, R.raw.the_quest_begins)

    // Lancer la lecture de la musique dès que le composable est attaché
    LaunchedEffect(Unit) {
        mediaPlayer.start()


    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MusicPlayer()
}



