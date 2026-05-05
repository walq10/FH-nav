package com.fhnav.app.ui.navigation_ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhnav.app.data.model.RoutePoint
import com.fhnav.app.domain.model.NavigationState
import com.fhnav.app.domain.model.TurnType
import com.fhnav.app.domain.usecase.NavigationUseCase
import com.fhnav.app.domain.usecase.VoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationUseCase: NavigationUseCase,
    private val voiceUseCase: VoiceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val navState: StateFlow<NavigationState> =
        navigationUseCase.navigationState as StateFlow<NavigationState>

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _showOverview = MutableStateFlow(false)
    val showOverview: StateFlow<Boolean> = _showOverview.asStateFlow()

    init {
        val destName = savedStateHandle.get<String>("destinationName") ?: ""
        val destLat = savedStateHandle.get<Float>("destLat")?.toDouble() ?: 0.0
        val destLng = savedStateHandle.get<Float>("destLng")?.toDouble() ?: 0.0
        val originLat = savedStateHandle.get<Float>("originLat")?.toDouble() ?: 0.0
        val originLng = savedStateHandle.get<Float>("originLng")?.toDouble() ?: 0.0

        if (destLat != 0.0 && destLng != 0.0) {
            startNavigation(destName, destLat, destLng, originLat, originLng)
        }
    }

    private fun startNavigation(
        destName: String,
        destLat: Double,
        destLng: Double,
        originLat: Double,
        originLng: Double
    ) {
        viewModelScope.launch {
            val origin = RoutePoint(originLat, originLng)
            val dest = RoutePoint(destLat, destLng)

            navigationUseCase.calculateRoute(origin, dest).collect { route ->
                if (route != null) {
                    navigationUseCase.startNavigation(route, destName)
                    if (!_isMuted.value) {
                        voiceUseCase.speakNavigationInstruction("开始导航，目的地$destName")
                    }
                }
            }
        }
    }

    fun stopNavigation() {
        viewModelScope.launch {
            voiceUseCase.stopSpeaking()
            navigationUseCase.stopNavigation()
        }
    }

    fun toggleMute() {
        viewModelScope.launch {
            val muted = !_isMuted.value
            _isMuted.value = muted
            if (muted) {
                voiceUseCase.stopSpeaking()
            }
        }
    }

    fun toggleOverview() {
        _showOverview.value = !_showOverview.value
    }

    fun updateSpeed(speedKmh: Float) {
        navigationUseCase.updateSpeed(speedKmh)
    }

    fun updateBearing(bearing: Float) {
        navigationUseCase.updateBearing(bearing)
    }

    fun speakInstruction(text: String) {
        if (!_isMuted.value) {
            viewModelScope.launch {
                voiceUseCase.speakNavigationInstruction(text)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            navigationUseCase.stopNavigation()
        }
    }
}
