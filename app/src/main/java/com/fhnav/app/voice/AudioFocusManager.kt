package com.fhnav.app.voice

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages audio focus for navigation voice prompts.
 * Handles ducking, pausing, and focus loss/gain callbacks.
 */
@Singleton
class AudioFocusManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var focusRequest: AudioFocusRequest? = null
    private var onFocusLost: (() -> Unit)? = null
    private var onFocusGained: (() -> Unit)? = null

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                onFocusLost?.invoke()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                onFocusLost?.invoke()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Duck volume for navigation prompts
                onFocusLost?.invoke()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                onFocusGained?.invoke()
            }
        }
    }

    fun requestFocus(
        onFocusGained: () -> Unit = {},
        onFocusLost: () -> Unit = {}
    ): Boolean {
        this.onFocusGained = onFocusGained
        this.onFocusLost = onFocusLost

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener(focusChangeListener)
            .setAcceptsDelayedFocusGain(true)
            .build()

        val result = audioManager.requestAudioFocus(focusRequest!!)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonFocus() {
        focusRequest?.let {
            audioManager.abandonAudioFocusRequest(it)
        }
        focusRequest = null
        onFocusGained = null
        onFocusLost = null
    }

    fun hasFocus(): Boolean = focusRequest != null
}
