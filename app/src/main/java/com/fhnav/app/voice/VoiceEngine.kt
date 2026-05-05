package com.fhnav.app.voice

/**
 * Core voice engine that coordinates between asset playback and TTS.
 * Decides whether to use a pre-recorded asset or fall back to TTS.
 */
import android.content.Context
import com.fhnav.app.data.local.datastore.SettingsDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val voiceAssetMapper: VoiceAssetMapper,
    private val ttsEngine: TTSEngine,
    private val audioFocusManager: AudioFocusManager,
    private val settingsDataStore: SettingsDataStore
) {
    /**
     * Speak a navigation instruction.
     * Tries pre-recorded asset first, falls back to TTS.
     */
    suspend fun speakInstruction(text: String, assetKey: String? = null) {
        val voiceEnabled = settingsDataStore.voiceEnabled.first()
        if (!voiceEnabled) return

        audioFocusManager.requestFocus()

        val usedAsset = if (assetKey != null) {
            voiceAssetMapper.playAsset(assetKey)
        } else {
            false
        }

        if (!usedAsset) {
            // Fall back to TTS for dynamic content
            val language = settingsDataStore.navigationLanguage.first()
            val locale = if (language == "zh") Locale.CHINESE else Locale.ENGLISH
            ttsEngine.speak(text, locale)
        }

        audioFocusManager.abandonFocus()
    }

    /**
     * Speak a phrase message (pre-recorded or TTS).
     */
    suspend fun speakPhrase(text: String, audioUrl: String? = null) {
        val voiceEnabled = settingsDataStore.voiceEnabled.first()
        if (!voiceEnabled) return

        audioFocusManager.requestFocus()

        val usedAsset = if (!audioUrl.isNullOrBlank()) {
            voiceAssetMapper.playFromUrl(audioUrl)
        } else {
            false
        }

        if (!usedAsset) {
            ttsEngine.speak(text)
        }

        audioFocusManager.abandonFocus()
    }

    fun stop() {
        ttsEngine.stop()
        audioFocusManager.abandonFocus()
    }

    fun shutdown() {
        ttsEngine.shutdown()
        audioFocusManager.abandonFocus()
    }
}
