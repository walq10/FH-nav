package com.fhnav.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fhnav.app.ui.theme.*

@Composable
fun FHNavButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: FHNavButtonVariant = FHNavButtonVariant.PRIMARY
) {
    val containerColor = when (variant) {
        FHNavButtonVariant.PRIMARY -> FH4Primary
        FHNavButtonVariant.SECONDARY -> FH4Secondary
        FHNavButtonVariant.DANGER -> FH4Error
        FHNavButtonVariant.GHOST -> Color.Transparent
    }

    val contentColor = when (variant) {
        FHNavButtonVariant.PRIMARY -> FH4Background
        FHNavButtonVariant.SECONDARY -> FH4Background
        FHNavButtonVariant.DANGER -> Color.White
        FHNavButtonVariant.GHOST -> FH4Primary
    }

    val glowColor = when (variant) {
        FHNavButtonVariant.PRIMARY -> FH4GlowCyan
        FHNavButtonVariant.SECONDARY -> FH4GlowGreen
        FHNavButtonVariant.DANGER -> FH4Error.copy(alpha = 0.3f)
        FHNavButtonVariant.GHOST -> Color.Transparent
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .drawBehind {
                if (enabled && variant != FHNavButtonVariant.GHOST) {
                    drawRoundRect(
                        color = glowColor,
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        topLeft = androidx.compose.ui.geometry.Offset(
                            0f,
                            size.height * 0.3f
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            size.width,
                            size.height * 0.7f
                        )
                    )
                }
            },
        enabled = enabled && !isLoading,
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = FH4Disabled.copy(alpha = 0.3f),
            disabledContentColor = FH4Disabled
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (variant != FHNavButtonVariant.GHOST) 4.dp else 0.dp,
            pressedElevation = if (variant != FHNavButtonVariant.GHOST) 1.dp else 0.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                    fadeOut(animationSpec = tween(200))
            },
            label = "button_content"
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

enum class FHNavButtonVariant {
    PRIMARY,
    SECONDARY,
    DANGER,
    GHOST
}

@Composable
fun FHNavIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(FH4Surface.copy(alpha = 0.9f)),
        enabled = enabled
    ) {
        icon()
    }
}
