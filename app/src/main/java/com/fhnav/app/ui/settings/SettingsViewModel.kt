package com.fhnav.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhnav.app.data.local.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val darkMode: StateFlow<Boolean> = settingsDataStore.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val mapStyle: StateFlow<Int> = settingsDataStore.mapStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val voiceEnabled: StateFlow<Boolean> = settingsDataStore.voiceEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val voiceVolume: StateFlow<Int> = settingsDataStore.voiceVolume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 80)

    val speedAlertEnabled: StateFlow<Boolean> = settingsDataStore.speedAlertEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val speedAlertThreshold: StateFlow<Int> = settingsDataStore.speedAlertThreshold
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 120)

    val locationSharingEnabled: StateFlow<Boolean> = settingsDataStore.locationSharingEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val navigationLanguage: StateFlow<String> = settingsDataStore.navigationLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "zh")

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setDarkMode(enabled) }
    }

    fun setMapStyle(style: Int) {
        viewModelScope.launch { settingsDataStore.setMapStyle(style) }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setVoiceEnabled(enabled) }
    }

    fun setVoiceVolume(volume: Int) {
        viewModelScope.launch { settingsDataStore.setVoiceVolume(volume) }
    }

    fun setSpeedAlertEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setSpeedAlertEnabled(enabled) }
    }

    fun setSpeedAlertThreshold(threshold: Int) {
        viewModelScope.launch { settingsDataStore.setSpeedAlertThreshold(threshold) }
    }

    fun setLocationSharingEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setLocationSharingEnabled(enabled) }
    }

    fun setNavigationLanguage(language: String) {
        viewModelScope.launch { settingsDataStore.setNavigationLanguage(language) }
    }
}
