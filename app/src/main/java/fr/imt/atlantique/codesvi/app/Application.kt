package fr.imt.atlantique.codesvi.app


import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.google.firebase.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import fr.imt.atlantique.codesvi.app.data.model.MusicManager
import timber.log.Timber

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        MusicManager.initialize(this)
        // Plant Timber pour les logs
        Timber.plant(Timber.DebugTree())



    }
}

