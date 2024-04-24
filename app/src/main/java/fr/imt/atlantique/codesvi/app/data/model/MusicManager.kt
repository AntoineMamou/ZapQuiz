package fr.imt.atlantique.codesvi.app.data.model

import android.content.Context
import android.health.connect.datatypes.units.Volume
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import fr.imt.atlantique.codesvi.app.Application
import fr.imt.atlantique.codesvi.app.R

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null

    fun initialize(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.the_quest_begins).apply {
                isLooping = true
                setVolume(0.1f) // Set a low volume or as needed
                start()  // Start immediately and keep playing
            }
        }
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    fun playMusic() {
        if (!mediaPlayer?.isPlaying!!) {
            mediaPlayer?.start()
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> MusicManager.playMusic()
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

