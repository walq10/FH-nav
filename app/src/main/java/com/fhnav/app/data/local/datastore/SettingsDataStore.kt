package com.fhnav.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fhnav_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val MAP_STYLE = intPreferencesKey("map_style") // 0=dark, 1=standard, 2=satellite
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val VOICE_VOLUME = intPreferencesKey("voice_volume") // 0-100
        val SPEED_ALERT_ENABLED = booleanPreferencesKey("speed_alert_enabled")
        val SPEED_ALERT_THRESHOLD = intPreferencesKey("speed_alert_threshold") // km/h
        val LOCATION_SHARING_ENABLED = booleanPreferencesKey("location_sharing_enabled")
        val NAVIGATION_LANGUAGE = stringPreferencesKey("navigation_language") // zh, en
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    val darkMode: Flow<Boolean> = dataStore.data.map { it[DARK_MODE] ?: true }
    val mapStyle: Flow<Int> = dataStore.data.map { it[MAP_STYLE] ?: 0 }
    val voiceEnabled: Flow<Boolean> = dataStore.data.map { it[VOICE_ENABLED] ?: true }
    val voiceVolume: Flow<Int> = dataStore.data.map { it[VOICE_VOLUME] ?: 80 }
    val speedAlertEnabled: Flow<Boolean> = dataStore.data.map { it[SPEED_ALERT_ENABLED] ?: true }
    val speedAlertThreshold: Flow<Int> = dataStore.data.map { it[SPEED_ALERT_THRESHOLD] ?: 120 }
    val locationSharingEnabled: Flow<Boolean> = dataStore.data.map { it[LOCATION_SHARING_ENABLED] ?: true }
    val navigationLanguage: Flow<String> = dataStore.data.map { it[NAVIGATION_LANGUAGE] ?: "zh" }
    val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { it[HAS_COMPLETED_ONBOARDING] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun setMapStyle(style: Int) {
        dataStore.edit { it[MAP_STYLE] = style }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        dataStore.edit { it[VOICE_ENABLED] = enabled }
    }

    suspend fun setVoiceVolume(volume: Int) {
        dataStore.edit { it[VOICE_VOLUME] = volume.coerceIn(0, 100) }
    }

    suspend fun setSpeedAlertEnabled(enabled: Boolean) {
        dataStore.edit { it[SPEED_ALERT_ENABLED] = enabled }
    }

    suspend fun setSpeedAlertThreshold(threshold: Int) {
        dataStore.edit { it[SPEED_ALERT_THRESHOLD] = threshold }
    }

    suspend fun setLocationSharingEnabled(enabled: Boolean) {
        dataStore.edit { it[LOCATION_SHARING_ENABLED] = enabled }
    }

    suspend fun setNavigationLanguage(language: String) {
        dataStore.edit { it[NAVIGATION_LANGUAGE] = language }
    }

    suspend fun setHasCompletedOnboarding(completed: Boolean) {
        dataStore.edit { it[HAS_COMPLETED_ONBOARDING] = completed }
    }
}
