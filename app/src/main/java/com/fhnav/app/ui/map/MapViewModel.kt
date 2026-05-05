package com.fhnav.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhnav.app.data.model.Route
import com.fhnav.app.data.model.RoutePoint
import com.fhnav.app.data.model.SearchResult
import com.fhnav.app.domain.usecase.LocationShareUseCase
import com.fhnav.app.domain.usecase.NavigationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val selectedDestination: SearchResult? = null,
    val currentRoute: Route? = null,
    val isCalculatingRoute: Boolean = false,
    val currentLat: Double = 39.9042,   // Default: Beijing
    val currentLng: Double = 116.4074,
    val currentAddress: String = "定位中...",
    val mapStyle: Int = 0, // 0=dark, 1=standard, 2=satellite
    val showSearchResults: Boolean = false,
    val recentSearches: List<SearchResult> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val navigationUseCase: NavigationUseCase,
    private val locationShareUseCase: LocationShareUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        collectLocation()
    }

    private fun collectLocation() {
        viewModelScope.launch {
            locationShareUseCase.locationUpdates.collect { location ->
                _uiState.value = _uiState.value.copy(
                    currentLat = location.latitude,
                    currentLng = location.longitude
                )
                // Reverse geocode
                navigationUseCase.getRegeoAddress(location.latitude, location.longitude)
                    .collect { address ->
                        _uiState.value = _uiState.value.copy(currentAddress = address)
                    }
            }
        }
        viewModelScope.launch {
            navigationUseCase.getRecentSearches().collect { searches ->
                _uiState.value = _uiState.value.copy(
                    recentSearches = searches.map {
                        SearchResult(
                            id = it.poiId,
                            name = it.poiName,
                            address = it.address,
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                    }
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, showSearchResults = query.isNotBlank())
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            _uiState.value = _uiState.value.copy(isSearching = true)
            navigationUseCase.searchPoi(query).collect { results ->
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false
                )
            }
        }
    }

    fun selectDestination(result: SearchResult) {
        viewModelScope.launch {
            navigationUseCase.saveSearchHistory(result)
        }
        _uiState.value = _uiState.value.copy(
            selectedDestination = result,
            showSearchResults = false,
            searchQuery = result.name
        )
        calculateRoute(result)
    }

    private fun calculateRoute(destination: SearchResult) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCalculatingRoute = true)
            val origin = RoutePoint(_uiState.value.currentLat, _uiState.value.currentLng)
            val dest = RoutePoint(destination.latitude, destination.longitude)
            navigationUseCase.calculateRoute(origin, dest).collect { route ->
                _uiState.value = _uiState.value.copy(
                    currentRoute = route,
                    isCalculatingRoute = false
                )
            }
        }
    }

    fun clearRoute() {
        _uiState.value = _uiState.value.copy(
            selectedDestination = null,
            currentRoute = null,
            searchQuery = ""
        )
    }

    fun toggleMapStyle() {
        val newStyle = (_uiState.value.mapStyle + 1) % 3
        _uiState.value = _uiState.value.copy(mapStyle = newStyle)
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            showSearchResults = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
