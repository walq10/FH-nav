package com.fhnav.app.data.repository

import com.fhnav.app.data.model.Route
import com.fhnav.app.data.model.RoutePoint
import com.fhnav.app.data.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface AMapRepository {
    fun searchPoi(keyword: String, city: String = ""): Flow<List<SearchResult>>
    fun searchNearby(keyword: String, latitude: Double, longitude: Double): Flow<List<SearchResult>>
    fun calculateRoute(origin: RoutePoint, destination: RoutePoint, strategy: Int = 0): Flow<Route?>
    fun getRegeoAddress(latitude: Double, longitude: Double): Flow<String>

    suspend fun startNavi(route: Route)
    suspend fun stopNavi()
    fun isNaviRunning(): Flow<Boolean>
}
