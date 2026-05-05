package com.fhnav.app.voice

import com.fhnav.app.domain.model.VoiceEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * High-level voice manager that coordinates speaking and listening.
 * Used by VoiceUseCase.
 */
@Singleton
class VoiceManager @Inject constructor(
    private val voiceEngine: VoiceEngine
) {
    private val _voiceEvents = MutableSharedFlow<VoiceEvent>(extraBufferCapacity = 16)
    val voiceEvents: SharedFlow<VoiceEvent> = _voiceEvents.asSharedFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    suspend fun speak(text: String, priority: Boolean = false) {
        _isSpeaking.value = true
        _voiceEvents.emit(VoiceEvent.SpeechStarted(text))
        try {
            voiceEngine.speakInstruction(text)
            _voiceEvents.emit(VoiceEvent.SpeechCompleted(text))
        } catch (e: Exception) {
            _voiceEvents.emit(VoiceEvent.SpeechError(e.message ?: "语音合成失败"))
        } finally {
            _isSpeaking.value = false
        }
    }

    suspend fun speakWithAsset(text: String, assetKey: String) {
        _isSpeaking.value = true
        _voiceEvents.emit(VoiceEvent.SpeechStarted(text))
        try {
            voiceEngine.speakInstruction(text, assetKey)
            _voiceEvents.emit(VoiceEvent.SpeechCompleted(text))
        } catch (e: Exception) {
            _voiceEvents.emit(VoiceEvent.SpeechError(e.message ?: "语音播放失败"))
        } finally {
            _isSpeaking.value = false
        }
    }

    suspend fun startListening() {
        _isListening.value = true
        _voiceEvents.emit(VoiceEvent.RecognitionStarted)
        // Note: Actual speech recognition requires SpeechRecognizer API
        // This is a placeholder for the recognition pipeline
    }

    suspend fun stopListening() {
        _isListening.value = false
    }

    fun stop() {
        voiceEngine.stop()
        _isSpeaking.value = false
    }
}
