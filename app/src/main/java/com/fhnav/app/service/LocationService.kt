package com.fhnav.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.fhnav.app.MainActivity
import com.fhnav.app.R
import com.fhnav.app.data.model.UserLocation
import com.fhnav.app.domain.usecase.LocationShareUseCase
import com.fhnav.app.domain.usecase.NavigationUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject lateinit var locationShareUseCase: LocationShareUseCase
    @Inject lateinit var navigationUseCase: NavigationUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationCallback: LocationCallback? = null

    companion object {
        const val CHANNEL_ID = "fhnav_location_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.fhnav.app.START_LOCATION"
        const val ACTION_STOP = "com.fhnav.app.STOP_LOCATION"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startLocationUpdates()
            ACTION_STOP -> stopLocationUpdates()
        }
        return START_STICKY
    }

    private fun startLocationUpdates() {
        startForeground(NOTIFICATION_ID, createNotification("正在获取位置..."))

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L // 2 seconds
        ).apply {
            setMinUpdateDistanceMeters(5f)
            setMinUpdateIntervalMillis(1000L)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val userLocation = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speed = location.speed * 3.6f, // m/s to km/h
                        bearing = location.bearing,
                        timestamp = System.currentTimeMillis()
                    )

                    // Update navigation state
                    navigationUseCase.updateSpeed(userLocation.speed)
                    navigationUseCase.updateBearing(userLocation.bearing)

                    // Share location with friends
                    serviceScope.launch {
                        locationShareUseCase.updateMyLocation(userLocation)
                    }
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Permission not granted
            stopSelf()
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "位置服务",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "FH导航后台位置服务"
            setShowBadge(false)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FH 导航")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        stopLocationUpdates()
        serviceScope.cancel()
        super.onDestroy()
    }
}
