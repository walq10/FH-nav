package com.fhnav.app.data.remote.supabase

import com.fhnav.app.data.model.Phrase
import com.fhnav.app.data.model.PhraseCategory
import com.fhnav.app.data.model.PhraseMessage
import com.fhnav.app.data.repository.PhraseRepository
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.UUID

class PhraseRepositoryImpl : PhraseRepository {

    private val supabase = SupabaseClient.client
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _incomingMessages = MutableSharedFlow<PhraseMessage>(extraBufferCapacity = 64)

    // ── Built-in phrases ────────────────────────────────────────────────────

    private val builtinPhrases = listOf(
        // DAILY
        Phrase("b1", "我在前面等你", PhraseCategory.DAILY, sortOrder = 0),
        Phrase("b2", "我先出发了", PhraseCategory.DAILY, sortOrder = 1),
        Phrase("b3", "到了告诉我", PhraseCategory.DAILY, sortOrder = 2),
        Phrase("b4", "路上小心", PhraseCategory.DAILY, sortOrder = 3),
        Phrase("b5", "加油，快到了！", PhraseCategory.DAILY, sortOrder = 4),
        Phrase("b6", "马上到", PhraseCategory.DAILY, sortOrder = 5),
        Phrase("b7", "等我一下", PhraseCategory.DAILY, sortOrder = 6),
        Phrase("b8", "我不去了", PhraseCategory.DAILY, sortOrder = 7),

        // TRAFFIC
        Phrase("t1", "前面堵车，注意减速", PhraseCategory.TRAFFIC, sortOrder = 0),
        Phrase("t2", "前方有测速", PhraseCategory.TRAFFIC, sortOrder = 1),
        Phrase("t3", "注意前方施工", PhraseCategory.TRAFFIC, sortOrder = 2),
        Phrase("t4", "前面有事故，换条路", PhraseCategory.TRAFFIC, sortOrder = 3),
        Phrase("t5", "走高速", PhraseCategory.TRAFFIC, sortOrder = 4),
        Phrase("t6", "走辅路", PhraseCategory.TRAFFIC, sortOrder = 5),
        Phrase("t7", "注意红绿灯", PhraseCategory.TRAFFIC, sortOrder = 6),

        // GREETING
        Phrase("g1", "出发了吗？", PhraseCategory.GREETING, sortOrder = 0),
        Phrase("g2", "今天一起走", PhraseCategory.GREETING, sortOrder = 1),
        Phrase("g3", "晚上一起吃饭", PhraseCategory.GREETING, sortOrder = 2),
        Phrase("g4", "周末去兜风", PhraseCategory.GREETING, sortOrder = 3),

        // FH4 (Forza Horizon 4 style)
        Phrase("f1", "Boop boop!", PhraseCategory.FH4, sortOrder = 0),
        Phrase("f2", "Nice driving!", PhraseCategory.FH4, sortOrder = 1),
        Phrase("f3", "Great drift!", PhraseCategory.FH4, sortOrder = 2),
        Phrase("f4", "Head to the festival!", PhraseCategory.FH4, sortOrder = 3),
        Phrase("f5", "Race me!", PhraseCategory.FH4, sortOrder = 4),
        Phrase("f6", "You're a legend!", PhraseCategory.FH4, sortOrder = 5),
    )

    init {
        listenToIncomingMessages()
    }

    private fun listenToIncomingMessages() {
        scope.launch {
            try {
                val channel = supabase.realtime.channel("phrase-messages-changes")
                val changesFlow = channel.postgresChangeFlow(io.github.jan.supabase.realtime.realtime.PostgresAction::class) {
                    schema = "public"
                    table = "phrase_messages"
                }
                channel.subscribe()

                changesFlow.collect { action ->
                    // On INSERT, decode and emit to incoming messages flow
                    val newRecord = action.record
                    if (newRecord != null) {
                        try {
                            val message = PhraseMessage(
                                id = newRecord["id"] as? String ?: return@collect,
                                senderId = newRecord["sender_id"] as? String ?: return@collect,
                                receiverId = newRecord["receiver_id"] as? String ?: return@collect,
                                phraseId = newRecord["phrase_id"] as? String ?: return@collect,
                                phraseText = newRecord["phrase_text"] as? String ?: return@collect,
                                senderNickname = newRecord["sender_nickname"] as? String ?: return@collect,
                                createdAt = (newRecord["created_at"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            )

                            // Only emit if we are the receiver
                            val currentUserId = supabase.gotrue.currentUserOrNull()?.id
                            if (currentUserId != null && message.receiverId == currentUserId) {
                                _incomingMessages.emit(message)
                            }
                        } catch (_: Exception) { }
                    }
                }
            } catch (_: Exception) {
                // Realtime connection failed
            }
        }
    }

    override fun getBuiltinPhrases(): List<Phrase> = builtinPhrases

    override fun getCustomPhrases(userId: String): Flow<List<Phrase>> = flow {
        try {
            val phrases = supabase.postgrest
                .from("phrases")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("user_id", userId)
                        eq("is_builtin", false)
                    }
                }
                .decodeList<Phrase>()
            emit(phrases)
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun sendPhrase(receiverId: String, phraseId: String): Result<Unit> {
        return try {
            val currentUser = supabase.gotrue.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("Not logged in"))

            // Look up phrase text
            val phraseText = builtinPhrases.find { it.id == phraseId }?.text
                ?: run {
                    // Try fetching from Supabase
                    supabase.postgrest
                        .from("phrases")
                        .select(columns = Columns.ALL) {
                            filter { eq("id", phraseId) }
                        }
                        .decodeSingle<Phrase>()
                        .text
                }

            val nickname = currentUser.userMetadata?.get("nickname") as? String ?: ""

            supabase.postgrest
                .from("phrase_messages")
                .insert(
                    mapOf(
                        "id" to UUID.randomUUID().toString(),
                        "sender_id" to currentUser.id,
                        "receiver_id" to receiverId,
                        "phrase_id" to phraseId,
                        "phrase_text" to phraseText,
                        "sender_nickname" to nickname,
                        "created_at" to System.currentTimeMillis()
                    )
                )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getIncomingMessages(): Flow<PhraseMessage> = _incomingMessages
}
