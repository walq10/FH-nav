package com.fhnav.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fhnav.app.ui.theme.*

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = FH4Primary,
    strokeWidth: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep"
    )

    // Pulse effect
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        val stroke = Stroke(
            width = strokeWidth.toPx(),
            cap = StrokeCap.Round
        )

        // Background circle (track)
        drawCircle(
            color = color.copy(alpha = 0.15f),
            radius = (size.toPx() / 2) - strokeWidth.toPx(),
            style = Stroke(width = strokeWidth.toPx() * 0.5f)
        )

        // Rotating arc
        drawArc(
            color = color.copy(alpha = pulseAlpha),
            startAngle = rotation,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke,
            topLeft = Offset(strokeWidth.toPx() / 2, strokeWidth.toPx() / 2),
            size = androidx.compose.ui.geometry.Size(
                size.toPx() - strokeWidth.toPx(),
                size.toPx() - strokeWidth.toPx()
            )
        )
    }
}

@Composable
fun FHNavLinearLoading(
    modifier: Modifier = Modifier,
    color: Color = FH4Primary,
    trackColor: Color = FH4SurfaceVariant,
    height: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "linear_loading")

    val offset by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        // Track
        drawRoundRect(
            color = trackColor,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(height.toPx() / 2)
        )

        // Animated indicator
        val indicatorWidth = size.width * 0.3f
        val x = offset * size.width

        drawRoundRect(
            color = color,
            topLeft = Offset(x, 0f),
            size = androidx.compose.ui.geometry.Size(indicatorWidth, height.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(height.toPx() / 2)
        )
    }
}
