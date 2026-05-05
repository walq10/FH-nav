package com.fhnav.app.domain.usecase

import com.fhnav.app.data.model.Friendship
import com.fhnav.app.data.model.Phrase
import com.fhnav.app.data.model.PhraseCategory
import com.fhnav.app.data.model.PhraseMessage
import com.fhnav.app.data.model.User
import com.fhnav.app.data.repository.FriendRepository
import com.fhnav.app.data.repository.PhraseRepository
import com.fhnav.app.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Orchestrates friend management and phrase messaging.
 */
class SocialUseCase(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
    private val phraseRepository: PhraseRepository
) {

    // ── Friends ─────────────────────────────────────────────────────────────

    /**
     * Get the current user's accepted friends.
     */
    fun getFriends(): Flow<List<User>> = friendRepository.getFriends()

    /**
     * Get pending friend requests (incoming).
     */
    fun getPendingRequests(): Flow<List<Friendship>> = friendRepository.getPendingRequests()

    /**
     * Search for users by name or phone.
     */
    suspend fun searchUsers(query: String): Result<List<User>> {
        if (query.length < 2) {
            return Result.success(emptyList())
        }
        return userRepository.searchUsers(query)
    }

    /**
     * Get a specific user by ID.
     */
    suspend fun getUser(userId: String): Result<User> {
        return userRepository.getUser(userId)
    }

    /**
     * Send a friend request.
     */
    suspend fun sendFriendRequest(friendId: String): Result<Unit> {
        return friendRepository.sendRequest(friendId)
    }

    /**
     * Accept a friend request.
     */
    suspend fun acceptFriendRequest(friendshipId: String): Result<Unit> {
        return friendRepository.acceptRequest(friendshipId)
    }

    /**
     * Reject a friend request.
     */
    suspend fun rejectFriendRequest(friendshipId: String): Result<Unit> {
        return friendRepository.rejectRequest(friendshipId)
    }

    // ── Phrases ─────────────────────────────────────────────────────────────

    /**
     * Get all built-in phrases.
     */
    fun getBuiltinPhrases(): List<Phrase> = phraseRepository.getBuiltinPhrases()

    /**
     * Get custom phrases created by a user.
     */
    fun getCustomPhrases(userId: String): Flow<List<Phrase>> =
        phraseRepository.getCustomPhrases(userId)

    /**
     * Get all phrases (builtin + custom) grouped by category.
     */
    fun getAllPhrasesGrouped(userId: String): Flow<Map<PhraseCategory, List<Phrase>>> {
        val builtin = phraseRepository.getBuiltinPhrases()
        return phraseRepository.getCustomPhrases(userId).map { custom ->
            (builtin + custom).groupBy { it.category }
        }
    }

    /**
     * Send a phrase to a friend.
     */
    suspend fun sendPhraseToFriend(receiverId: String, phraseId: String): Result<Unit> {
        return phraseRepository.sendPhrase(receiverId, phraseId)
    }

    /**
     * Get incoming phrase messages.
     */
    fun getIncomingMessages(): Flow<PhraseMessage> = phraseRepository.getIncomingMessages()

    /**
     * Get incoming messages filtered for a specific receiver.
     */
    fun getIncomingMessagesFor(receiverId: String): Flow<PhraseMessage> {
        return phraseRepository.getIncomingMessages()
    }
}
