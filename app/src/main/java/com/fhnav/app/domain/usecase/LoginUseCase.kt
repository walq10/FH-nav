package com.fhnav.app.domain.usecase

import com.fhnav.app.data.model.User
import com.fhnav.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Orchestrates the login flow: send OTP → verify → check if first login → return user state.
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) {

    /**
     * Current authenticated user, or null if not logged in.
     */
    val currentUser: Flow<User?> = authRepository.currentUser

    /**
     * Whether the user is currently logged in.
     */
    val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn

    /**
     * Step 1: Send OTP to the given phone number.
     */
    suspend fun sendOtp(phone: String): Result<Unit> {
        // Validate phone format (basic check)
        val cleaned = phone.trim()
        if (cleaned.length < 8) {
            return Result.failure(IllegalArgumentException("Invalid phone number"))
        }
        return authRepository.sendOtp(cleaned)
    }

    /**
     * Step 2: Verify the OTP code and log in.
     * Returns [LoginResult] indicating if it's a new or returning user.
     */
    suspend fun verifyOtp(phone: String, code: String): Result<LoginResult> {
        val verifyResult = authRepository.verifyOtp(phone.trim(), code.trim())
        return verifyResult.fold(
            onSuccess = { user ->
                val isFirstLogin = user.nickname.isBlank()
                Result.success(LoginResult(user = user, isFirstLogin = isFirstLogin))
            },
            onFailure = { e ->
                Result.failure(e)
            }
        )
    }

    /**
     * Step 3 (optional): Set nickname for first-time users.
     */
    suspend fun completeOnboarding(nickname: String): Result<Unit> {
        if (nickname.isBlank()) {
            return Result.failure(IllegalArgumentException("Nickname cannot be empty"))
        }
        return authRepository.updateNickname(nickname.trim())
    }

    /**
     * Update nickname for existing users.
     */
    suspend fun updateNickname(nickname: String): Result<Unit> {
        if (nickname.isBlank()) {
            return Result.failure(IllegalArgumentException("Nickname cannot be empty"))
        }
        return authRepository.updateNickname(nickname.trim())
    }

    /**
     * Sign out the current user.
     */
    suspend fun signOut() {
        authRepository.signOut()
    }

    /**
     * Check if the current user needs onboarding (has no nickname).
     */
    fun needsOnboarding(): Flow<Boolean> {
        return authRepository.currentUser.map { user ->
            user != null && user.nickname.isBlank()
        }
    }
}

/**
 * Result of a login verification.
 */
data class LoginResult(
    val user: User,
    val isFirstLogin: Boolean
)
