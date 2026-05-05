package com.fhnav.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fhnav.app.data.local.dao.PhraseDao
import com.fhnav.app.data.local.dao.SearchHistoryDao
import com.fhnav.app.data.local.entity.PhraseEntity
import com.fhnav.app.data.local.entity.SearchHistoryEntity

@Database(
    entities = [SearchHistoryEntity::class, PhraseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FHNavDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun phraseDao(): PhraseDao
}
