package com.fhnav.app.ui.navigation_ui.hud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fhnav.app.domain.model.NavigationState
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHOnSurfaceVariant
import com.fhnav.app.ui.theme.FHSurface

@Composable
fun HudOverlay(
    navState: NavigationState,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onStopNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(FHSurface.copy(alpha = 0.95f))
            .padding(16.dp)
    ) {
        // Turn indicator row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TurnIndicator(
                turnType = navState.nextTurnType,
                distance = navState.nextTurnDistance
            )

            SpeedDisplay(
                speed = navState.currentSpeed,
                speedLimit = navState.speedLimit
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress bar
        ProgressBar(
            currentStep = navState.currentStepIndex,
            totalSteps = navState.totalSteps,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Arrival info
        ArrivalInfo(
            remainingDistance = navState.remainingDistance,
            remainingTime = navState.remainingTime,
            destinationName = navState.destinationName
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onMuteToggle) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = if (isMuted) "开启声音" else "静音",
                    tint = if (isMuted) FHOnSurfaceVariant else FHCyan
                )
            }

            IconButton(onClick = onStopNavigation) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "结束导航",
                    tint = FHOnSurface
                )
            }
        }
    }
}
