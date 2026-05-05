package com.fhnav.app.ui.navigation_ui.hud

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHOnSurfaceVariant
import com.fhnav.app.ui.theme.FHSpeedDanger
import com.fhnav.app.ui.theme.FHSpeedNormal
import com.fhnav.app.ui.theme.FHSpeedWarning

@Composable
fun SpeedDisplay(
    speed: Float,
    speedLimit: Int,
    modifier: Modifier = Modifier
) {
    val speedColor = when {
        speedLimit > 0 && speed > speedLimit * 1.2f -> FHSpeedDanger
        speedLimit > 0 && speed > speedLimit -> FHSpeedWarning
        else -> FHSpeedNormal
    }

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Speed arc background
        Canvas(modifier = Modifier.size(80.dp)) {
            val strokeWidth = 6.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background arc
            drawArc(
                color = FHOnSurfaceVariant.copy(alpha = 0.2f),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Speed arc
            val maxDisplaySpeed = if (speedLimit > 0) speedLimit.toFloat() * 1.5f else 180f
            val sweepAngle = (speed / maxDisplaySpeed * 240f).coerceIn(0f, 240f)
            drawArc(
                color = speedColor,
                startAngle = 150f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Speed number
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = speed.toInt().toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = speedColor
            )
            Text(
                text = "km/h",
                style = MaterialTheme.typography.labelSmall,
                color = FHOnSurfaceVariant
            )
        }
    }
}
