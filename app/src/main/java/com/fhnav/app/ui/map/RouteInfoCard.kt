package com.fhnav.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fhnav.app.data.model.Route
import com.fhnav.app.ui.components.FHNavButton
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHOnSurfaceVariant
import com.fhnav.app.ui.theme.FHSurface
import com.fhnav.app.ui.theme.FHSurfaceVariant

@Composable
fun RouteInfoCard(
    destinationName: String,
    route: Route?,
    onStartNavigation: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FHSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = destinationName,
                    style = MaterialTheme.typography.titleMedium,
                    color = FHOnSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = FHOnSurfaceVariant
                    )
                }
            }

            if (route != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RouteStat(
                        label = "距离",
                        value = formatDistance(route.distanceMeters)
                    )
                    RouteStat(
                        label = "预计时间",
                        value = formatDuration(route.durationSeconds)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FHNavButton(
                    text = "开始导航",
                    onClick = onStartNavigation,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "正在计算路线...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FHOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RouteStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = FHCyan
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = FHOnSurfaceVariant
        )
    }
}

private fun formatDistance(meters: Long): String {
    return if (meters >= 1000) {
        String.format("%.1f km", meters / 1000.0)
    } else {
        "$meters m"
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}h ${minutes}min"
        minutes > 0 -> "${minutes}min"
        else -> "<1min"
    }
}
