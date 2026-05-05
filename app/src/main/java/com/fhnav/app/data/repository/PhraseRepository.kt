package com.fhnav.app.data.repository

import com.fhnav.app.data.model.Phrase
import com.fhnav.app.data.model.PhraseCategory
import com.fhnav.app.data.model.PhraseMessage
import kotlinx.coroutines.flow.Flow

interface PhraseRepository {
    fun getPhrases(category: PhraseCategory? = null): Flow<List<Phrase>>
    fun getMessages(friendId: String): Flow<List<PhraseMessage>>
    fun getUnreadMessages(): Flow<List<PhraseMessage>>

    suspend fun sendPhraseMessage(friendId: String, phraseId: String): Result<PhraseMessage>
    suspend fun markMessageRead(messageId: String): Result<Unit>
    suspend fun addCustomPhrase(phrase: Phrase): Result<Phrase>
    suspend fun deleteCustomPhrase(phraseId: String): Result<Unit>
}
