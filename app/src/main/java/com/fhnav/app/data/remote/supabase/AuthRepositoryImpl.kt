package com.fhnav.app.data.remote.supabase

import com.fhnav.app.data.model.User
import com.fhnav.app.data.repository.AuthRepository
import io.github.jan.supabase.auth.providers.builtin.Phone
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseProvider: SupabaseClientProvider
) : AuthRepository {

    private val auth get() = supabaseProvider.client.auth
    private val postgrest get() = supabaseProvider.client.postgrest

    private val _currentUser = MutableStateFlow<User?>(null)

    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    override val isLoggedIn: Flow<Boolean> = _currentUser.map { it != null }

    override suspend fun sendOtp(phone: String): Result<Unit> = runCatching {
        auth.signInWith(Phone) {
            this.phone = phone
        }
    }

    override suspend fun verifyOtp(phone: String, code: String): Result<User> = runCatching {
        auth.verifyPhoneOtp(
            type = io.github.jan.supabase.auth.providers.builtin.Phone,
            phone = phone,
            token = code
        )

        val userId = auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Auth succeeded but no user found")

        // Upsert user profile
        val user = User(
            id = userId,
            phone = phone,
            createdAt = System.currentTimeMillis().toString()
        )

        postgrest.from("users").upsert(user)

        val fetched = postgrest.from("users")
            .select(Columns.ALL) {
                filter { eq("id", userId) }
            }
            .decodeSingle<User>()

        _currentUser.value = fetched
        fetched
    }

    override suspend fun updateNickname(nickname: String): Result<User> = runCatching {
        val userId = getCurrentUserId() ?: throw IllegalStateException("Not logged in")

        postgrest.from("users").update(
            mapOf("nickname" to nickname)
        ) {
            filter { eq("id", userId) }
        }

        val updated = postgrest.from("users")
            .select(Columns.ALL) {
                filter { eq("id", userId) }
            }
            .decodeSingle<User>()

        _currentUser.value = updated
        updated
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
        _currentUser.value = null
    }

    override suspend fun refreshToken(): Result<Unit> = runCatching {
        auth.refreshCurrentSession()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }
}
