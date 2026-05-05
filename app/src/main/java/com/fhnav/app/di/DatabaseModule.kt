package com.fhnav.app.di

import com.fhnav.app.data.local.FHNavDatabase
import com.fhnav.app.data.local.dao.PhraseDao
import com.fhnav.app.data.local.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Room database and DAO instances.
 *
 * The database instance is provided by [AppModule].
 * This module extracts individual DAOs for injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: FHNavDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun providePhraseDao(database: FHNavDatabase): PhraseDao {
        return database.phraseDao()
    }
}
