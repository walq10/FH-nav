package com.fhnav.app.ui.navigation_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.fhnav.app.ui.navigation_ui.hud.ArrivalInfo
import com.fhnav.app.ui.navigation_ui.hud.HudOverlay
import com.fhnav.app.ui.navigation_ui.hud.TurnIndicator
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    destinationName: String,
    destLat: Double,
    destLng: Double,
    originLat: Double,
    originLng: Double,
    onNavigateBack: () -> Unit,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val navState by viewModel.navState.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    Scaffold(
        containerColor = FHBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "导航中 → $destinationName",
                        style = MaterialTheme.typography.titleMedium,
                        color = FHCyan
                    )
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.stopNavigation()
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "结束导航",
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
            // Map placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FHBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "导航地图",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FHOnSurface.copy(alpha = 0.3f)
                )
            }

            // HUD Overlay
            HudOverlay(
                navState = navState,
                isMuted = isMuted,
                onMuteToggle = { viewModel.toggleMute() },
                onStopNavigation = {
                    viewModel.stopNavigation()
                    onNavigateBack()
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
