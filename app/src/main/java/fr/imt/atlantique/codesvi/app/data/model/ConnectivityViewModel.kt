package fr.imt.atlantique.codesvi.app.data.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ConnectivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnected.postValue(true)
        }

        override fun onLost(network: Network) {
            _isConnected.postValue(false)
        }
    }

    init {
        // Enregistrez le callback pour surveiller l'état de la connexion
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        // Vérifiez l'état initial de la connexion
        checkCurrentConnectivity()
    }

    private fun checkCurrentConnectivity() {
        val activeNetwork = connectivityManager.activeNetwork ?: return
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        _isConnected.value = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }

    override fun onCleared() {
        super.onCleared()
        // Retirez le callback lorsque le ViewModel est détruit
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
