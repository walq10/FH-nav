package com.fhnav.app.data.model

import com.google.gson.annotations.SerializedName

data class PhraseMessage(
    @SerializedName("id") val id: String = "",
    @SerializedName("sender_id") val senderId: String = "",
    @SerializedName("receiver_id") val receiverId: String = "",
    @SerializedName("phrase_id") val phraseId: String = "",
    @SerializedName("text") val text: String = "",
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("is_read") val isRead: Boolean = false
)
