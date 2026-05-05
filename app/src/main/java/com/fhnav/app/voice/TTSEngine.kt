package com.fhnav.app.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * TTS engine for dynamic navigation content that doesn't have pre-recorded assets.
 * Supports Chinese and English.
 */
@Singleton
class TTSEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine ->
                    engine.language = Locale.CHINESE
                    engine.setSpeechRate(1.1f)
                    engine.setPitch(1.0f)
                    _isInitialized.value = true
                }
            }
        }
    }

    suspend fun speak(text: String, locale: Locale = Locale.CHINESE): Boolean {
        if (!_isInitialized.value || tts == null) return false

        tts?.language = locale

        return suspendCancellableCoroutine { continuation ->
            val utteranceId = "fhnav_${System.currentTimeMillis()}"

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    if (continuation.isActive) continuation.resume(true)
                }

                @Deprecated("Deprecated in API")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    if (continuation.isActive) continuation.resume(false)
                }
            })

            val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            if (result == TextToSpeech.ERROR) {
                if (continuation.isActive) continuation.resume(false)
            }

            continuation.invokeOnCancellation {
                tts?.stop()
                _isSpeaking.value = false
            }
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isInitialized.value = false
    }
}
