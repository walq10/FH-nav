package com.fhnav.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.fhnav.app.data.local.datastore.SettingsDataStore
import com.fhnav.app.service.LocationService
import com.fhnav.app.ui.navigation.FHNavGraph
import com.fhnav.app.ui.theme.FHNavTheme
import com.fhnav.app.util.PermissionHelper
import com.fhnav.app.voice.VoiceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    @Inject lateinit var voiceManager: VoiceManager
    @Inject lateinit var settingsDataStore: SettingsDataStore

    private var locationPermissionGranted by mutableStateOf(false)
    private var notificationPermissionGranted by mutableStateOf(true) // Default true for < Android 13

    // Permission launchers
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        locationPermissionGranted = fineGranted || coarseGranted

        if (locationPermissionGranted) {
            Log.d(TAG, "Location permission granted")
            // Request background location separately on Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestBackgroundLocationPermission()
            }
        } else {
            Log.w(TAG, "Location permission denied")
        }
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d(TAG, "Background location permission granted")
        } else {
            Log.w(TAG, "Background location permission denied")
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationPermissionGranted = granted
        Log.d(TAG, "Notification permission: $granted")
    }

    private val phoneStatePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d(TAG, "Phone state permission: $granted")
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkAndRequestPermissions()

        setContent {
            FHNavTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FHNavGraph()

                    // Play welcome voice on first launch after login
                    LaunchedEffect(Unit) {
                        playWelcomeIfNeeded()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        voiceManager.release()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Let Compose navigation handle back press
        super.onBackPressed()
    }

    // ─── Permissions ──────────────────────────────────────────────────────

    private fun checkAndRequestPermissions() {
        // Location permission
        locationPermissionGranted = PermissionHelper.isLocationPermissionGranted(this)
        if (!locationPermissionGranted) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestBackgroundLocationPermission()
        }

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!notificationPermissionGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Phone state permission (for SIM-based auth)
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            phoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    // ─── Welcome voice ────────────────────────────────────────────────────

    private suspend fun playWelcomeIfNeeded() {
        try {
            val nickname = settingsDataStore.selectedNickname.first()
            if (nickname.isNotBlank()) {
                voiceManager.playWelcome(nickname)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play welcome voice", e)
        }
    }

    // ─── Location service control ─────────────────────────────────────────

    fun startLocationSharing() {
        if (locationPermissionGranted) {
            LocationService.startService(this)
        }
    }

    fun stopLocationSharing() {
        LocationService.stopService(this)
    }
}
