package fr.imt.atlantique.codesvi.app

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import fr.imt.atlantique.codesvi.app.data.model.ConnectivityViewModel
import fr.imt.atlantique.codesvi.app.ui.MainUI
import fr.imt.atlantique.codesvi.app.ui.NoConnectionScreen

import timber.log.Timber



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashscreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            val connectivityViewModel: ConnectivityViewModel = viewModel()
            val isConnected = connectivityViewModel.isConnected.observeAsState(initial = false)

            if (isConnected.value) {
                MainUI()  // L'écran principal accessible lorsque connecté
            } else {
                NoConnectionScreen()  // Écran affiché lorsqu'il n'y a pas de connexion Internet
            }
            Timber.d("setContent called")

        }

    }
}





