package com.fhnav.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fhnav.app.ui.theme.*

@Composable
fun FHNavCard(
    modifier: Modifier = Modifier,
    showGlow: Boolean = false,
    glowColor: Color = FH4GlowCyan,
    elevation: Dp = 2.dp,
    borderStroke: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .then(
                if (showGlow) {
                    Modifier.drawBehind {
                        drawRoundRect(
                            color = glowColor,
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            size = size.copy(height = size.height * 0.5f)
                                .let { androidx.compose.ui.geometry.Size(it.width, it.height) },
                            topLeft = androidx.compose.ui.geometry.Offset(0f, size.height * 0.5f)
                        )
                    }
                } else Modifier
            ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = FH4Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        border = borderStroke
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun FHNavSurfaceCard(
    modifier: Modifier = Modifier,
    variant: FHNavCardVariant = FHNavCardVariant.DEFAULT,
    content: @Composable ColumnScope.() -> Unit
) {
    val backgroundColor = when (variant) {
        FHNavCardVariant.DEFAULT -> FH4Surface
        FHNavCardVariant.ELEVATED -> FH4SurfaceVariant
        FHNavCardVariant.ACCENT -> FH4Primary.copy(alpha = 0.1f)
        FHNavCardVariant.WARNING -> FH4Warning.copy(alpha = 0.1f)
        FHNavCardVariant.ERROR -> FH4Error.copy(alpha = 0.1f)
    }

    val borderColor = when (variant) {
        FHNavCardVariant.DEFAULT -> FH4Outline.copy(alpha = 0.3f)
        FHNavCardVariant.ELEVATED -> FH4Outline
        FHNavCardVariant.ACCENT -> FH4Primary.copy(alpha = 0.3f)
        FHNavCardVariant.WARNING -> FH4Warning.copy(alpha = 0.3f)
        FHNavCardVariant.ERROR -> FH4Error.copy(alpha = 0.3f)
    }

    Surface(
        modifier = modifier.clip(CardShape),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shape = CardShape
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

enum class FHNavCardVariant {
    DEFAULT,
    ELEVATED,
    ACCENT,
    WARNING,
    ERROR
}
