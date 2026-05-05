package com.fhnav.app.data.remote.supabase

import com.fhnav.app.data.model.FriendshipStatus
import com.fhnav.app.data.model.UserLocation
import com.fhnav.app.data.repository.LocationRepository
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LocationRepositoryImpl : LocationRepository {

    private val supabase = SupabaseClient.client
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _friendsLocations = MutableSharedFlow<List<UserLocation>>(replay = 1)
    private var locationSharingJob: Job? = null
    private var realtimeChannel: io.github.jan.supabase.realtime.RealtimeChannel? = null

    override fun getCurrentLocation(): Flow<UserLocation> = flow {
        // This relies on the Android FusedLocationProviderClient being wired up
        // at the platform level. For now we emit a placeholder that the UI layer
        // replaces with actual GPS data via a callback-based provider.
        //
        // In a full implementation, this would wrap FusedLocationProviderClient:
        //   LocationServices.getFusedLocationProviderClient(context)
        //       .requestLocationUpdates(locationRequest, callback, looper)
        //
        // The flow never completes; the caller should use .collect or combine.
        kotlinx.coroutines.flow.flowOf<UserLocation>().collect { emit(it) }
    }

    override suspend fun updateLocation(location: UserLocation): Result<Unit> {
        return try {
            val userId = supabase.gotrue.currentUserOrNull()?.id
                ?: return Result.failure(IllegalStateException("Not logged in"))

            supabase.postgrest
                .from("user_locations")
                .upsert(
                    mapOf(
                        "user_id" to userId,
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "bearing" to location.bearing,
                        "speed" to location.speed,
                        "updated_at" to location.updatedAt
                    )
                ) {
                    // Upsert on user_id conflict
                    onConflict = "user_id"
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFriendsLocations(): Flow<List<UserLocation>> = flow {
        val currentUserId = supabase.gotrue.currentUserOrNull()?.id ?: return@flow

        try {
            // Get accepted friend IDs
            val friendships = supabase.postgrest
                .from("friendships")
                .select(columns = Columns.ALL) {
                    filter {
                        or {
                            eq("user_id", currentUserId)
                            eq("friend_id", currentUserId)
                        }
                        eq("status", FriendshipStatus.ACCEPTED.name)
                    }
                }
                .decodeList<com.fhnav.app.data.model.Friendship>()

            val friendIds = friendships.map { f ->
                if (f.userId == currentUserId) f.friendId else f.userId
            }.toSet()

            if (friendIds.isEmpty()) {
                emit(emptyList())
                return@flow
            }

            // Fetch all friends' locations
            val locations = supabase.postgrest
                .from("user_locations")
                .select(columns = Columns.ALL) {
                    filter {
                        isIn("user_id", friendIds.toList())
                    }
                }
                .decodeList<UserLocation>()

            emit(locations)
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun startLocationSharing() {
        locationSharingJob?.cancel()
        locationSharingJob = scope.launch {
            try {
                // Subscribe to friends' location changes
                val channel = supabase.realtime.channel("user-locations-changes")
                val changesFlow = channel.postgresChangeFlow(io.github.jan.supabase.realtime.realtime.PostgresAction::class) {
                    schema = "public"
                    table = "user_locations"
                }
                channel.subscribe()
                realtimeChannel = channel

                // Collect real-time changes and refresh the friends locations flow
                changesFlow.collect {
                    refreshFriendsLocations()
                }
            } catch (_: Exception) {
                // Realtime connection failed
            }
        }
    }

    override suspend fun stopLocationSharing() {
        locationSharingJob?.cancel()
        locationSharingJob = null
        realtimeChannel?.unsubscribe()
        realtimeChannel = null
    }

    /**
     * Internal: called periodically to write current GPS position to Supabase.
     * The caller (platform layer) should invoke this with actual location data.
     */
    suspend fun reportLocation(latitude: Double, longitude: Double, bearing: Float = 0f, speed: Float = 0f) {
        val userId = supabase.gotrue.currentUserOrNull()?.id ?: return
        try {
            supabase.postgrest
                .from("user_locations")
                .upsert(
                    mapOf(
                        "user_id" to userId,
                        "latitude" to latitude,
                        "longitude" to longitude,
                        "bearing" to bearing,
                        "speed" to speed,
                        "updated_at" to System.currentTimeMillis()
                    )
                ) {
                    onConflict = "user_id"
                }
        } catch (_: Exception) { }
    }

    private suspend fun refreshFriendsLocations() {
        val currentUserId = supabase.gotrue.currentUserOrNull()?.id ?: return
        try {
            val friendships = supabase.postgrest
                .from("friendships")
                .select(columns = Columns.ALL) {
                    filter {
                        or {
                            eq("user_id", currentUserId)
                            eq("friend_id", currentUserId)
                        }
                        eq("status", FriendshipStatus.ACCEPTED.name)
                    }
                }
                .decodeList<com.fhnav.app.data.model.Friendship>()

            val friendIds = friendships.map { f ->
                if (f.userId == currentUserId) f.friendId else f.userId
            }.toSet()

            if (friendIds.isEmpty()) {
                _friendsLocations.emit(emptyList())
                return
            }

            val locations = supabase.postgrest
                .from("user_locations")
                .select(columns = Columns.ALL) {
                    filter {
                        isIn("user_id", friendIds.toList())
                    }
                }
                .decodeList<UserLocation>()

            _friendsLocations.emit(locations)
        } catch (_: Exception) { }
    }
}
