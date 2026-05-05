package com.fhnav.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrases")
data class PhraseEntity(
    @PrimaryKey val id: String,
    val category: String,
    val textZh: String,
    val textEn: String,
    val audioUrl: String = "",
    val isPreset: Boolean = true,
    val lastUsedAt: Long = 0
)
