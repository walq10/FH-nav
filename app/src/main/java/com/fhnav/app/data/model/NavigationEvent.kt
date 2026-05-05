package com.fhnav.app.data.model

import com.google.gson.annotations.SerializedName

data class NavigationEvent(
    @SerializedName("type") val type: NavigationEventType,
    @SerializedName("data") val data: String = "",
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

enum class NavigationEventType {
    START,
    STOP,
    REROUTE,
    ARRIVED,
    SPEED_ALERT,
    TURN,
    CAMERA,
    TRAFFIC
}
