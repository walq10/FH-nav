package com.fhnav.app.data.repository

import com.fhnav.app.data.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<UserLocation?>
    fun getFriendsLocations(friendIds: List<String>): Flow<List<UserLocation>>
    fun getLocationUpdates(): Flow<UserLocation>

    suspend fun updateMyLocation(location: UserLocation): Result<Unit>
    suspend fun subscribeToLocationUpdates(friendIds: List<String>)
    suspend fun unsubscribeFromLocationUpdates()
}
