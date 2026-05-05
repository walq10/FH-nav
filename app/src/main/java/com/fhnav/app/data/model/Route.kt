package com.fhnav.app.data.model

import com.amap.api.navi.model.NaviLatLng
import com.google.gson.annotations.SerializedName

data class Route(
    @SerializedName("origin") val origin: RoutePoint = RoutePoint(),
    @SerializedName("destination") val destination: RoutePoint = RoutePoint(),
    @SerializedName("distance_meters") val distanceMeters: Long = 0,
    @SerializedName("duration_seconds") val durationSeconds: Long = 0,
    @SerializedName("polyline") val polyline: List<RoutePoint> = emptyList(),
    @SerializedName("strategy") val strategy: Int = 0
) {
    fun toNaviOrigin(): NaviLatLng = NaviLatLng(origin.latitude, origin.longitude)
    fun toNaviDestination(): NaviLatLng = NaviLatLng(destination.latitude, destination.longitude)
}

data class RoutePoint(
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0
)

data class SearchResult(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: String = "",
    val distance: String = ""
)
