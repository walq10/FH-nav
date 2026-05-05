package com.fhnav.app.domain.usecase

import com.fhnav.app.data.model.NavigationEvent
import com.fhnav.app.data.repository.AMapRepository
import com.fhnav.app.data.local.datastore.SettingsDataStore
import com.fhnav.app.domain.model.VoiceCommand
import com.fhnav.app.domain.model.VoicePriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Maps navigation events to voice commands and triggers TTS playback.
 * Considers user preferences for voice language and volume.
 */
class VoiceUseCase(
    private val settingsDataStore: SettingsDataStore,
    private val amapRepository: AMapRepository
) {

    /**
     * Whether voice guidance is enabled.
     */
    val voiceEnabled: Flow<Boolean> = settingsDataStore.voiceEnabled

    /**
     * Current voice volume (0..1).
     */
    val voiceVolume: Flow<Float> = settingsDataStore.voiceVolume

    /**
     * Current voice language ("zh" or "en").
     */
    val voiceLanguage: Flow<String> = settingsDataStore.voiceLanguage

    /**
     * Transform navigation events into voice commands.
     * Returns null for events that should not trigger voice.
     */
    fun mapEventToVoiceCommand(event: NavigationEvent, language: String): VoiceCommand? {
        return when (event) {
            is NavigationEvent.Started -> VoiceCommand(
                ttsText = if (language == "zh") "导航已开始，准备出发" else "Navigation started, ready to go",
                priority = VoicePriority.HIGH
            )

            is NavigationEvent.TurnLeft -> VoiceCommand(
                ttsText = if (language == "zh") {
                    formatDistance(event.distance, "zh") + "后左转"
                } else {
                    "Turn left in " + formatDistance(event.distance, "en")
                },
                priority = VoicePriority.HIGH
            )

            is NavigationEvent.TurnRight -> VoiceCommand(
                ttsText = if (language == "zh") {
                    formatDistance(event.distance, "zh") + "后右转"
                } else {
                    "Turn right in " + formatDistance(event.distance, "en")
                },
                priority = VoicePriority.HIGH
            )

            is NavigationEvent.GoStraight -> VoiceCommand(
                ttsText = if (language == "zh") "请直行" else "Continue straight",
                priority = VoicePriority.NORMAL
            )

            is NavigationEvent.UTurn -> VoiceCommand(
                ttsText = if (language == "zh") "请掉头" else "Make a U-turn",
                priority = VoicePriority.HIGH
            )

            is NavigationEvent.KeepLeft -> VoiceCommand(
                ttsText = if (language == "zh") {
                    formatDistance(event.distance, "zh") + "后靠左行驶"
                } else {
                    "Keep left in " + formatDistance(event.distance, "en")
                },
                priority = VoicePriority.NORMAL
            )

            is NavigationEvent.KeepRight -> VoiceCommand(
                ttsText = if (language == "zh") {
                    formatDistance(event.distance, "zh") + "后靠右行驶"
                } else {
                    "Keep right in " + formatDistance(event.distance, "en")
                },
                priority = VoicePriority.NORMAL
            )

            is NavigationEvent.EnterRoundabout -> VoiceCommand(
                ttsText = if (language == "zh") "请进入环岛" else "Enter the roundabout",
                priority = VoicePriority.HIGH
            )

            is NavigationEvent.Arrived -> VoiceCommand(
                ttsText = if (language == "zh") "已到达目的地" else "You have arrived at your destination",
                priority = VoicePriority.URGENT
            )

            is NavigationEvent.OffRoute -> VoiceCommand(
                ttsText = if (language == "zh") "已偏离路线，正在重新规划" else "Off route, recalculating",
                priority = VoicePriority.URGENT
            )

            is NavigationEvent.SpeedCamera -> VoiceCommand(
                ttsText = if (language == "zh") "前方有测速" else "Speed camera ahead",
                priority = VoicePriority.HIGH
            )

            is NavigationEvent.FlavorLine -> VoiceCommand(
                ttsText = event.text,
                priority = VoicePriority.LOW
            )

            is NavigationEvent.WelcomeBack -> VoiceCommand(
                ttsText = if (language == "zh") "欢迎回来，继续导航" else "Welcome back, continuing navigation",
                priority = VoicePriority.NORMAL
            )
        }
    }

    /**
     * Get a stream of voice commands from navigation events.
     * Only emits when voice is enabled.
     */
    fun getVoiceCommands(): Flow<VoiceCommand> {
        return amapRepository.getNavigationEvents().map { event ->
            val lang = "zh" // Default; in practice, combine with settingsDataStore.voiceLanguage
            mapEventToVoiceCommand(event, lang)
        }.map { command ->
            command ?: VoiceCommand(ttsText = "", priority = VoicePriority.LOW)
        }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        settingsDataStore.setVoiceEnabled(enabled)
    }

    suspend fun setVoiceVolume(volume: Float) {
        settingsDataStore.setVoiceVolume(volume)
    }

    suspend fun setVoiceLanguage(language: String) {
        settingsDataStore.setVoiceLanguage(language)
    }

    private fun formatDistance(meters: Int, language: String): String {
        return if (language == "zh") {
            when {
                meters >= 1000 -> String.format("%.1f公里", meters / 1000.0)
                meters >= 100 -> "${meters / 100 * 100}米"
                else -> "${meters}米"
            }
        } else {
            when {
                meters >= 1000 -> String.format("%.1f kilometers", meters / 1000.0)
                else -> "$meters meters"
            }
        }
    }
}
