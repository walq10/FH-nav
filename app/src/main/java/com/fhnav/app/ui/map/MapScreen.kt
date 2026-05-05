package com.fhnav.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHSurface
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToNavigation: (String, Double, Double, Double, Double) -> Unit,
    onNavigateToFriends: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = FHSurface
            ) {
                Text(
                    text = "FH 导航",
                    style = MaterialTheme.typography.headlineMedium,
                    color = FHCyan,
                    modifier = Modifier.padding(24.dp)
                )
                // Drawer items would go here
            }
        }
    ) {
        Scaffold(
            containerColor = FHBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.currentAddress,
                            style = MaterialTheme.typography.bodyMedium,
                            color = FHOnSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "菜单",
                                tint = FHCyan
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToFriends) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "好友",
                                tint = FHOnSurface
                            )
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "设置",
                                tint = FHOnSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = FHBackground
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Map placeholder - in production this would be an AMap composable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FHBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "地图加载中...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = FHOnSurface.copy(alpha = 0.3f)
                    )
                }

                // Search bar overlay
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onClear = { viewModel.clearSearch() },
                        isSearching = uiState.isSearching
                    )

                    if (uiState.showSearchResults) {
                        SearchResultList(
                            results = uiState.searchResults,
                            recentSearches = uiState.recentSearches,
                            onResultClick = { viewModel.selectDestination(it) },
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Route info card
                if (uiState.selectedDestination != null) {
                    RouteInfoCard(
                        destinationName = uiState.selectedDestination?.name ?: "",
                        route = uiState.currentRoute,
                        onStartNavigation = {
                            val dest = uiState.selectedDestination!!
                            onNavigateToNavigation(
                                dest.name,
                                dest.latitude,
                                dest.longitude,
                                uiState.currentLat,
                                uiState.currentLng
                            )
                        },
                        onDismiss = { viewModel.clearRoute() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }

                // Map style toggle
                StyleToggleButton(
                    onClick = { viewModel.toggleMapStyle() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 80.dp)
                )
            }
        }
    }
}
