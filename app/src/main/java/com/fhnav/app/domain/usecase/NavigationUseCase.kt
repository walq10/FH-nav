package com.fhnav.app.domain.usecase

import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItem
import com.fhnav.app.data.model.NavigationEvent
import com.fhnav.app.data.model.Route
import com.fhnav.app.data.repository.AMapRepository
import com.fhnav.app.data.repository.AuthRepository
import com.fhnav.app.domain.model.NavigationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Orchestrates the full navigation flow:
 * search → plan routes → select route → start navigation → handle events.
 */
class NavigationUseCase(
    private val amapRepository: AMapRepository,
    private val authRepository: AuthRepository
) {

    private val _state = MutableStateFlow<NavigationState>(NavigationState.Idle)
    val state: Flow<NavigationState> = _state.asStateFlow()

    private var currentDestination: String = ""
    private var currentRoutes: List<Route> = emptyList()
    private var selectedRoute: Route? = null
    private var totalDistance: Int = 0

    /**
     * Search for POIs by keyword in a city.
     */
    suspend fun searchDestination(keyword: String, city: String): Result<List<PoiItem>> {
        _state.update { NavigationState.Searching(keyword) }
        val result = amapRepository.searchPoi(keyword, city)
        if (result.isFailure) {
            _state.update { NavigationState.Idle }
        }
        return result
    }

    /**
     * Plan routes from [from] to [to].
     * Updates state to [NavigationState.RoutesDisplayed].
     */
    suspend fun planRoute(from: LatLng, to: LatLng, destinationName: String): Result<List<Route>> {
        _state.update { NavigationState.Searching(destinationName) }
        currentDestination = destinationName

        val result = amapRepository.planRoute(from, to)
        return result.fold(
            onSuccess = { routes ->
                currentRoutes = routes
                _state.update {
                    NavigationState.RoutesDisplayed(routes = routes, destination = destinationName)
                }
                Result.success(routes)
            },
            onFailure = { e ->
                _state.update { NavigationState.Idle }
                Result.failure(e)
            }
        )
    }

    /**
     * Select a route from the displayed routes and start navigation.
     */
    suspend fun selectRouteAndNavigate(route: Route) {
        selectedRoute = route
        totalDistance = route.distance
        _state.update {
            NavigationState.Navigating(
                route = route,
                currentSpeed = 0f,
                remainingDistance = route.distance,
                remainingTime = route.duration,
                currentRoad = "",
                nextTurn = null,
                nextTurnDistance = 0,
                progress = 0f
            )
        }
        amapRepository.startNavigation(route)
    }

    /**
     * Select the recommended route and start navigation.
     */
    suspend fun selectRecommendedRoute() {
        val recommended = currentRoutes.firstOrNull { it.isRecommended }
            ?: currentRoutes.firstOrNull()
            ?: return
        selectRouteAndNavigate(recommended)
    }

    /**
     * Process a navigation event and update state accordingly.
     */
    fun handleNavigationEvent(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.Arrived -> {
                _state.update { NavigationState.Arrived }
            }
            is NavigationEvent.OffRoute -> {
                // Stay in current state but signal recalculation needed
                // The ViewModel should handle recalculation
            }
            is NavigationEvent.Started -> {
                // Already in navigating state
            }
            else -> {
                // Update navigating state with event info
                val current = _state.value
                if (current is NavigationState.Navigating) {
                    val nextTurn = when (event) {
                        is NavigationEvent.TurnLeft -> "左转"
                        is NavigationEvent.TurnRight -> "右转"
                        is NavigationEvent.GoStraight -> "直行"
                        is NavigationEvent.UTurn -> "掉头"
                        is NavigationEvent.KeepLeft -> "靠左行驶"
                        is NavigationEvent.KeepRight -> "靠右行驶"
                        is NavigationEvent.EnterRoundabout -> "进入环岛"
                        else -> current.nextTurn
                    }
                    val nextTurnDistance = when (event) {
                        is NavigationEvent.TurnLeft -> event.distance
                        is NavigationEvent.TurnRight -> event.distance
                        is NavigationEvent.KeepLeft -> event.distance
                        is NavigationEvent.KeepRight -> event.distance
                        else -> current.nextTurnDistance
                    }
                    _state.update {
                        current.copy(
                            nextTurn = nextTurn,
                            nextTurnDistance = nextTurnDistance
                        )
                    }
                }
            }
        }
    }

    /**
     * Update the current speed and remaining info during navigation.
     */
    fun updateNavigationProgress(
        currentSpeed: Float,
        remainingDistance: Int,
        remainingTime: Int,
        currentRoad: String
    ) {
        val current = _state.value
        if (current is NavigationState.Navigating) {
            val progress = if (totalDistance > 0) {
                1f - (remainingDistance.toFloat() / totalDistance.toFloat())
            } else 0f
            _state.update {
                current.copy(
                    currentSpeed = currentSpeed,
                    remainingDistance = remainingDistance,
                    remainingTime = remainingTime,
                    currentRoad = currentRoad,
                    progress = progress.coerceIn(0f, 1f)
                )
            }
        }
    }

    /**
     * Stop navigation and return to idle state.
     */
    suspend fun stopNavigation() {
        amapRepository.stopNavigation()
        selectedRoute = null
        currentRoutes = emptyList()
        currentDestination = ""
        _state.update { NavigationState.Idle }
    }

    /**
     * Reset state when arriving or when user wants a fresh start.
     */
    fun reset() {
        _state.update { NavigationState.Idle }
    }

    /**
     * Get the navigation event stream.
     */
    fun getNavigationEvents(): Flow<NavigationEvent> = amapRepository.getNavigationEvents()
}
