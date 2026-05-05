package com.fhnav.app.di

import com.fhnav.app.BuildConfig
import com.fhnav.app.data.repository.AMapRepository
import com.fhnav.app.data.repository.AuthRepository
import com.fhnav.app.data.repository.FriendRepository
import com.fhnav.app.data.repository.LocationRepository
import com.fhnav.app.data.repository.PhraseRepository
import com.fhnav.app.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ─── Supabase Client ──────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseAuth(client: SupabaseClient): Auth {
        return client.auth
    }

    @Provides
    @Singleton
    fun provideSupabasePostgrest(client: SupabaseClient): Postgrest {
        return client.postgrest
    }

    @Provides
    @Singleton
    fun provideSupabaseRealtime(client: SupabaseClient): Realtime {
        return client.realtime
    }

    // ─── Repositories ─────────────────────────────────────────────────────
    // These @Provides methods allow Hilt to construct repository implementations.
    // The actual implementations should be annotated with @Inject constructor
    // and @Singleton. If they are interfaces with separate Impl classes,
    // bind them here. Otherwise, if the Repo classes have @Inject constructors,
    // Hilt handles them automatically and these can be removed.

    // AuthRepository — if using an interface + impl pattern:
    // @Provides @Singleton
    // fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    // For now, assuming repositories have @Inject constructors, Hilt resolves them.
    // These provides are kept as explicit bindings for clarity and future-proofing.
}
