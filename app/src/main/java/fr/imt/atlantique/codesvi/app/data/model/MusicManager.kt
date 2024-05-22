package fr.imt.atlantique.codesvi.app.data.model

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import fr.imt.atlantique.codesvi.app.R

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null

    fun initialize(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.the_quest_begins).apply {
                isLooping = true
                setVolume(0.1f) // Set a low volume or as needed
                //start()  // Start immediately and keep playing
            }
        }
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    fun playMusic(context: Context) {
        val sharedPreferences: SharedPreferences by lazy {
            context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        }

        val play = sharedPreferences.getBoolean("Effets sonores", true)

        if (play) {
            if (mediaPlayer == null) {
                initialize(context)
            }
            if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                try {
                    mediaPlayer?.start()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    initialize(context) // Reinitialize in case of error
                    mediaPlayer?.start()
                }
            }
        }
    }

    fun pauseMusic() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer?.pause()
        }
    }


    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

@Composable
fun MusicControl() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> MusicManager.playMusic(context)
                Lifecycle.Event.ON_STOP -> MusicManager.pauseMusic()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Your UI components here
}

@Composable
fun SoundEffect(soundEffectResource: Int) {
    val context = LocalContext.current
    val soundEffect = MediaPlayer.create(context, soundEffectResource)
    soundEffect.start()
    soundEffect.setOnCompletionListener { mp ->
        mp.release()
    }

}

