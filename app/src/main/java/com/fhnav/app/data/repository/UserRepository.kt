package com.fhnav.app.data.repository

import com.fhnav.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(userId: String): Flow<User?>
    fun getUserByPhone(phone: String): Flow<User?>
    suspend fun updateUser(user: User): Result<User>
    suspend fun updateAvatar(avatarUrl: String): Result<Unit>
    suspend fun searchUsers(query: String): Result<List<User>>
}
