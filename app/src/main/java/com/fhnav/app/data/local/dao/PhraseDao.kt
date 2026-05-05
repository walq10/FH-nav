package com.fhnav.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fhnav.app.data.local.entity.PhraseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {

    @Query("SELECT * FROM phrases ORDER BY category, textZh")
    fun getAllPhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE category = :category ORDER BY textZh")
    fun getPhrasesByCategory(category: String): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE isPreset = 1 ORDER BY lastUsedAt DESC LIMIT :limit")
    fun getRecentPresetPhrases(limit: Int = 10): Flow<List<PhraseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phrases: List<PhraseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(phrase: PhraseEntity)

    @Query("UPDATE phrases SET lastUsedAt = :timestamp WHERE id = :id")
    suspend fun updateLastUsed(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM phrases WHERE id = :id AND isPreset = 0")
    suspend fun deleteCustomPhrase(id: String)

    @Query("DELETE FROM phrases")
    suspend fun clearAll()
}
