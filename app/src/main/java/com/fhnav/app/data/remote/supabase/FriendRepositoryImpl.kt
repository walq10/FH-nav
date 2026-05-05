package com.fhnav.app.data.remote.supabase

import com.fhnav.app.data.model.Friendship
import com.fhnav.app.data.model.User
import com.fhnav.app.data.repository.FriendRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepositoryImpl @Inject constructor(
    private val supabaseProvider: SupabaseClientProvider
) : FriendRepository {

    private val postgrest get() = supabaseProvider.client.postgrest
    private val auth get() = supabaseProvider.client.auth

    private fun requireUserId(): String =
        auth.currentUserOrNull()?.id ?: throw IllegalStateException("Not logged in")

    override fun getFriends(): Flow<List<User>> = flow {
        val userId = requireUserId()
        val friendships = postgrest.from("friendships")
            .select(Columns.raw("*, friend:users!friend_id(*)")) {
                filter {
                    eq("user_id", userId)
                    eq("status", "accepted")
                }
            }
            .decodeList<Friendship>()

        val friends = friendships.mapNotNull { it.friend }
        emit(friends)
    }

    override fun getPendingRequests(): Flow<List<Friendship>> = flow {
        val userId = requireUserId()
        val requests = postgrest.from("friendships")
            .select(Columns.raw("*, friend:users!user_id(*)")) {
                filter {
                    eq("friend_id", userId)
                    eq("status", "pending")
                }
            }
            .decodeList<Friendship>()
        emit(requests)
    }

    override fun getSentRequests(): Flow<List<Friendship>> = flow {
        val userId = requireUserId()
        val requests = postgrest.from("friendships")
            .select(Columns.raw("*, friend:users!friend_id(*)")) {
                filter {
                    eq("user_id", userId)
                    eq("status", "pending")
                }
            }
            .decodeList<Friendship>()
        emit(requests)
    }

    override suspend fun sendFriendRequest(friendId: String): Result<Unit> = runCatching {
        val userId = requireUserId()
        postgrest.from("friendships").insert(
            mapOf(
                "user_id" to userId,
                "friend_id" to friendId,
                "status" to "pending"
            )
        )
    }

    override suspend fun acceptFriendRequest(friendshipId: String): Result<Unit> = runCatching {
        postgrest.from("friendships").update(
            mapOf("status" to "accepted")
        ) {
            filter { eq("id", friendshipId) }
        }
    }

    override suspend fun rejectFriendRequest(friendshipId: String): Result<Unit> = runCatching {
        postgrest.from("friendships").update(
            mapOf("status" to "rejected")
        ) {
            filter { eq("id", friendshipId) }
        }
    }

    override suspend fun removeFriend(friendId: String): Result<Unit> = runCatching {
        val userId = requireUserId()
        postgrest.from("friendships").delete {
            filter {
                or {
                    and {
                        eq("user_id", userId)
                        eq("friend_id", friendId)
                    }
                    and {
                        eq("user_id", friendId)
                        eq("friend_id", userId)
                    }
                }
            }
        }
    }
}
