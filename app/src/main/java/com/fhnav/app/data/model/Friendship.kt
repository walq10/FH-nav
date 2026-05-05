package com.fhnav.app.data.model

import com.google.gson.annotations.SerializedName

data class Friendship(
    @SerializedName("id") val id: String = "",
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("friend_id") val friendId: String = "",
    @SerializedName("status") val status: String = "pending", // pending, accepted, rejected
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("friend") val friend: User? = null
)
