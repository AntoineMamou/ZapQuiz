package fr.imt.atlantique.codesvi.app.ui.screens.game
/*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.imt.atlantique.codesvi.app.data.model.QCM
import fr.imt.atlantique.codesvi.app.repository.QcmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface GameScreenUiState {

    data class GameScreen(
        val qcmList: List<QCM>,
        val selectedQCM: QCM? = null
    ) : GameScreenUiState

    data object Empty : GameScreenUiState
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val qcmRepository: QcmRepository
) : ViewModel() {

    private val allQCMs = MutableStateFlow<List<QCM>>(listOf())
    private val selectedQCM = MutableStateFlow<QCM?>(null)

    val uiState: StateFlow<GameScreenUiState> = combine(allQCMs, selectedQCM) {
            allQCMs, selectedQCM ->
        GameScreenUiState.GameScreen(qcmList = allQCMs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GameScreenUiState.Empty
    )

}

 */