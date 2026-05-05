package com.fhnav.app.data.repository

import com.fhnav.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    val isLoggedIn: Flow<Boolean>

    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, code: String): Result<User>
    suspend fun updateNickname(nickname: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun refreshToken(): Result<Unit>
    fun getCurrentUserId(): String?
}
