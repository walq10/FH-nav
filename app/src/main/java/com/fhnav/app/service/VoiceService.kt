package com.fhnav.app.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.fhnav.app.domain.model.VoiceEvent
import com.fhnav.app.voice.VoiceEngine
import com.fhnav.app.voice.VoiceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@AndroidEntryPoint
class VoiceService : Service() {

    @Inject lateinit var voiceEngine: VoiceEngine
    @Inject lateinit var voiceManager: VoiceManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val binder = VoiceBinder()

    inner class VoiceBinder : Binder() {
        fun getService(): VoiceService = this@VoiceService
    }

    val voiceEvents: SharedFlow<VoiceEvent> = voiceManager.voiceEvents

    override fun onBind(intent: Intent?): IBinder = binder

    suspend fun speak(text: String, assetKey: String? = null) {
        if (assetKey != null) {
            voiceManager.speakWithAsset(text, assetKey)
        } else {
            voiceManager.speak(text)
        }
    }

    suspend fun speakNavigation(text: String) {
        voiceManager.speak(text, priority = true)
    }

    fun stopSpeaking() {
        voiceManager.stop()
    }

    override fun onDestroy() {
        voiceEngine.shutdown()
        serviceScope.cancel()
        super.onDestroy()
    }
}
