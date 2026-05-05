package com.fhnav.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("nickname") val nickname: String = "",
    @SerializedName("avatar_url") val avatarUrl: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = ""
)
