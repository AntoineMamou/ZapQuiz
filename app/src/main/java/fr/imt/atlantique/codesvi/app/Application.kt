package fr.imt.atlantique.codesvi.app


import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        // Plant Timber pour les logs
        Timber.plant(Timber.DebugTree())
    }
}

