package com.fhnav.app.data.remote.supabase

import com.fhnav.app.data.model.User
import com.fhnav.app.data.repository.UserRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val supabaseProvider: SupabaseClientProvider
) : UserRepository {

    private val postgrest get() = supabaseProvider.client.postgrest

    override fun getUser(userId: String): Flow<User?> = flow {
        val user = postgrest.from("users")
            .select(Columns.ALL) {
                filter { eq("id", userId) }
            }
            .decodeSingleOrNull<User>()
        emit(user)
    }

    override fun getUserByPhone(phone: String): Flow<User?> = flow {
        val user = postgrest.from("users")
            .select(Columns.ALL) {
                filter { eq("phone", phone) }
            }
            .decodeSingleOrNull<User>()
        emit(user)
    }

    override suspend fun updateUser(user: User): Result<User> = runCatching {
        postgrest.from("users").update(
            mapOf(
                "nickname" to user.nickname,
                "avatar_url" to user.avatarUrl
            )
        ) {
            filter { eq("id", user.id) }
        }

        postgrest.from("users")
            .select(Columns.ALL) {
                filter { eq("id", user.id) }
            }
            .decodeSingle<User>()
    }

    override suspend fun updateAvatar(avatarUrl: String): Result<Unit> = runCatching {
        val userId = supabaseProvider.client.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Not logged in")

        postgrest.from("users").update(
            mapOf("avatar_url" to avatarUrl)
        ) {
            filter { eq("id", userId) }
        }
    }

    override suspend fun searchUsers(query: String): Result<List<User>> = runCatching {
        postgrest.from("users")
            .select(Columns.ALL) {
                filter {
                    or {
                        ilike("nickname", "%$query%")
                        ilike("phone", "%$query%")
                    }
                }
            }
            .decodeList<User>()
    }
}
