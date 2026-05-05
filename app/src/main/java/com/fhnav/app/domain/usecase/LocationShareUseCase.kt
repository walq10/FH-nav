package com.fhnav.app.domain.usecase

import com.fhnav.app.data.local.datastore.SettingsDataStore
import com.fhnav.app.data.model.UserLocation
import com.fhnav.app.data.repository.LocationRepository
import com.fhnav.app.data.remote.supabase.LocationRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Orchestrates location sharing: start/stop sharing, get friends' locations.
 */
class LocationShareUseCase(
    private val locationRepository: LocationRepository,
    private val settingsDataStore: SettingsDataStore
) {

    /**
     * Whether location sharing is currently enabled by the user.
     */
    val locationSharingEnabled: Flow<Boolean> = settingsDataStore.locationSharingEnabled

    /**
     * Get the user's current location.
     */
    fun getCurrentLocation(): Flow<UserLocation> = locationRepository.getCurrentLocation()

    /**
     * Get friends' shared locations.
     */
    fun getFriendsLocations(): Flow<List<UserLocation>> = locationRepository.getFriendsLocations()

    /**
     * Start sharing location with friends.
     * Respects the user's location sharing preference.
     */
    suspend fun startLocationSharing() {
        val enabled = settingsDataStore.locationSharingEnabled.first()
        if (!enabled) {
            settingsDataStore.setLocationSharingEnabled(true)
        }
        locationRepository.startLocationSharing()
    }

    /**
     * Stop sharing location with friends.
     */
    suspend fun stopLocationSharing() {
        locationRepository.stopLocationSharing()
    }

    /**
     * Update the current location.
     * Typically called by the platform layer with GPS data every ~10 seconds.
     */
    suspend fun updateLocation(latitude: Double, longitude: Double, bearing: Float = 0f, speed: Float = 0f): Result<Unit> {
        val enabled = settingsDataStore.locationSharingEnabled.first()
        if (!enabled) return Result.success(Unit)

        val location = UserLocation(
            userId = "", // Will be filled by the repository using current auth
            latitude = latitude,
            longitude = longitude,
            bearing = bearing,
            speed = speed,
            updatedAt = System.currentTimeMillis()
        )
        return locationRepository.updateLocation(location)
    }

    /**
     * Toggle location sharing on/off.
     */
    suspend fun toggleLocationSharing(): Boolean {
        val current = settingsDataStore.locationSharingEnabled.first()
        val newValue = !current
        settingsDataStore.setLocationSharingEnabled(newValue)
        if (newValue) {
            locationRepository.startLocationSharing()
        } else {
            locationRepository.stopLocationSharing()
        }
        return newValue
    }

    /**
     * Report current GPS position to Supabase.
     * This is the method called by the platform layer's location callback.
     * Only works with [LocationRepositoryImpl] which has the reportLocation method.
     */
    suspend fun reportCurrentLocation(latitude: Double, longitude: Double, bearing: Float = 0f, speed: Float = 0f) {
        val enabled = settingsDataStore.locationSharingEnabled.first()
        if (!enabled) return

        if (locationRepository is LocationRepositoryImpl) {
            locationRepository.reportLocation(latitude, longitude, bearing, speed)
        } else {
            val location = UserLocation(
                userId = "",
                latitude = latitude,
                longitude = longitude,
                bearing = bearing,
                speed = speed,
                updatedAt = System.currentTimeMillis()
            )
            locationRepository.updateLocation(location)
        }
    }
}
