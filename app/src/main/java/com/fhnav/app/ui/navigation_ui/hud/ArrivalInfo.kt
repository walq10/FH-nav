package com.fhnav.app.ui.navigation_ui.hud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHOnSurfaceVariant

@Composable
fun ArrivalInfo(
    remainingDistance: Long,
    remainingTime: Long,
    destinationName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "剩余距离",
                style = MaterialTheme.typography.labelSmall,
                color = FHOnSurfaceVariant
            )
            Text(
                text = formatDistance(remainingDistance),
                style = MaterialTheme.typography.titleLarge,
                color = FHCyan
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "预计到达",
                style = MaterialTheme.typography.labelSmall,
                color = FHOnSurfaceVariant
            )
            Text(
                text = formatDuration(remainingTime),
                style = MaterialTheme.typography.titleLarge,
                color = FHCyan
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "目的地",
                style = MaterialTheme.typography.labelSmall,
                color = FHOnSurfaceVariant
            )
            Text(
                text = destinationName,
                style = MaterialTheme.typography.titleMedium,
                color = FHOnSurface,
                maxLines = 1
            )
        }
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
