package com.fhnav.app.voice

import android.content.Context
import android.media.MediaPlayer
import com.fhnav.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Maps navigation voice events to pre-recorded audio assets.
 * Handles phrase lookup and provides MediaPlayer instances.
 */
@Singleton
class VoiceAssetMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Map of phrase keys to raw resource IDs
    private val assetMap = mapOf(
        "nav_start" to R.raw.nav_start,
        "nav_stop" to R.raw.nav_stop,
        "turn_left" to R.raw.turn_left,
        "turn_right" to R.raw.turn_right,
        "turn_straight" to R.raw.turn_straight,
        "turn_uturn" to R.raw.turn_uturn,
        "arrive_dest" to R.raw.arrive_dest,
        "speed_over" to R.raw.speed_over,
        "reroute" to R.raw.reroute,
        "camera_alert" to R.raw.camera_alert
    )

    /**
     * Check if we have a pre-recorded asset for this key.
     */
    fun hasAsset(key: String): Boolean = assetMap.containsKey(key)

    /**
     * Get the raw resource ID for a phrase key.
     */
    fun getRawResourceId(key: String): Int? = assetMap[key]

    /**
     * Play a pre-recorded asset and wait for completion.
     */
    suspend fun playAsset(key: String): Boolean {
        val resId = getRawResourceId(key) ?: return false
        return suspendCancellableCoroutine { continuation ->
            try {
                val mediaPlayer = MediaPlayer.create(context, resId)
                if (mediaPlayer == null) {
                    continuation.resume(false)
                    return@suspendCancellableCoroutine
                }
                mediaPlayer.setOnCompletionListener {
                    it.release()
                    if (continuation.isActive) continuation.resume(true)
                }
                mediaPlayer.setOnErrorListener { mp, _, _ ->
                    mp.release()
                    if (continuation.isActive) continuation.resume(false)
                    true
                }
                mediaPlayer.start()
                continuation.invokeOnCancellation {
                    mediaPlayer.release()
                }
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(false)
            }
        }
    }

    /**
     * Play a phrase message audio from URL.
     */
    suspend fun playFromUrl(url: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(url)
                mediaPlayer.setOnPreparedListener { it.start() }
                mediaPlayer.setOnCompletionListener {
                    it.release()
                    if (continuation.isActive) continuation.resume(true)
                }
                mediaPlayer.setOnErrorListener { mp, _, _ ->
                    mp.release()
                    if (continuation.isActive) continuation.resume(false)
                    true
                }
                mediaPlayer.prepareAsync()
                continuation.invokeOnCancellation {
                    mediaPlayer.release()
                }
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(false)
            }
        }
    }
}
