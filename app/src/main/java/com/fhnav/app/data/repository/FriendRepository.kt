package com.fhnav.app.data.repository

import com.fhnav.app.data.model.Friendship
import com.fhnav.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getFriends(): Flow<List<User>>
    fun getPendingRequests(): Flow<List<Friendship>>
    fun getSentRequests(): Flow<List<Friendship>>

    suspend fun sendFriendRequest(friendId: String): Result<Unit>
    suspend fun acceptFriendRequest(friendshipId: String): Result<Unit>
    suspend fun rejectFriendRequest(friendshipId: String): Result<Unit>
    suspend fun removeFriend(friendId: String): Result<Unit>
}
