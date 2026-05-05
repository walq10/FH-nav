package com.fhnav.app.data.model

import com.google.gson.annotations.SerializedName

data class UserLocation(
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
    @SerializedName("speed") val speed: Float = 0f,
    @SerializedName("bearing") val bearing: Float = 0f,
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("nickname") val nickname: String = "",
    @SerializedName("is_navigating") val isNavigating: Boolean = false
)
