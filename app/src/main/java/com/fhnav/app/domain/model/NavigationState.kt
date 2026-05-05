package com.fhnav.app.domain.model

import com.fhnav.app.data.model.Route

sealed class NavigationState {
    data object Idle : NavigationState()

    data class Searching(val query: String) : NavigationState()

    data class RoutesDisplayed(
        val routes: List<Route>,
        val destination: String
    ) : NavigationState()

    data class Navigating(
        val route: Route,
        val currentSpeed: Float,
        val remainingDistance: Int,
        val remainingTime: Int,
        val currentRoad: String,
        val nextTurn: String?,
        val nextTurnDistance: Int,
        val progress: Float
    ) : NavigationState()

    data object Arrived : NavigationState()
}
