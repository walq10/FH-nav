package com.fhnav.app.ui.navigation_ui.hud

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.fhnav.app.domain.model.TurnType
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurfaceVariant

@Composable
fun TurnIndicator(
    turnType: TurnType,
    distance: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(48.dp)) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val arrowSize = size.width * 0.35f

            val path = Path().apply {
                when (turnType) {
                    TurnType.LEFT, TurnType.SLIGHT_LEFT -> {
                        moveTo(centerX + arrowSize / 2, centerY + arrowSize)
                        lineTo(centerX - arrowSize, centerY)
                        lineTo(centerX + arrowSize / 2, centerY - arrowSize)
                    }
                    TurnType.RIGHT, TurnType.SLIGHT_RIGHT -> {
                        moveTo(centerX - arrowSize / 2, centerY + arrowSize)
                        lineTo(centerX + arrowSize, centerY)
                        lineTo(centerX - arrowSize / 2, centerY - arrowSize)
                    }
                    TurnType.U_TURN -> {
                        moveTo(centerX - arrowSize, centerY + arrowSize)
                        quadraticBezierTo(
                            centerX - arrowSize, centerY - arrowSize,
                            centerX + arrowSize, centerY - arrowSize
                        )
                        quadraticBezierTo(
                            centerX + arrowSize * 1.5f, centerY,
                            centerX, centerY + arrowSize * 0.5f
                        )
                    }
                    TurnType.DESTINATION -> {
                        val r = arrowSize * 0.8f
                        addOval(
                            androidx.compose.ui.geometry.Rect(
                                centerX - r, centerY - r,
                                centerX + r, centerY + r
                            )
                        )
                    }
                    else -> {
                        // STRAIGHT
                        moveTo(centerX, centerY + arrowSize)
                        lineTo(centerX, centerY - arrowSize)
                        moveTo(centerX - arrowSize / 2, centerY - arrowSize / 3)
                        lineTo(centerX, centerY - arrowSize)
                        lineTo(centerX + arrowSize / 2, centerY - arrowSize / 3)
                    }
                }
            }

            drawPath(
                path = path,
                color = FHCyan,
                style = Fill
            )
        }

        Text(
            text = when (turnType) {
                TurnType.LEFT -> "左转"
                TurnType.RIGHT -> "右转"
                TurnType.SLIGHT_LEFT -> "左前方"
                TurnType.SLIGHT_RIGHT -> "右前方"
                TurnType.U_TURN -> "掉头"
                TurnType.DESTINATION -> "到达"
                else -> "直行"
            },
            style = MaterialTheme.typography.labelMedium,
            color = FHCyan,
            modifier = Modifier.padding(top = 4.dp)
        )

        if (distance > 0) {
            Text(
                text = formatDistance(distance),
                style = MaterialTheme.typography.bodySmall,
                color = FHOnSurfaceVariant
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
