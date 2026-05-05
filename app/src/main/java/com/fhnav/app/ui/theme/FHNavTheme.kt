package com.fhnav.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FHCyan,
    onPrimary = FHBackground,
    primaryContainer = FHCyanDark,
    onPrimaryContainer = FHOnSurface,
    secondary = FHGreen,
    onSecondary = FHBackground,
    secondaryContainer = FHGreenDark,
    onSecondaryContainer = FHOnSurface,
    tertiary = FHPurple,
    onTertiary = FHBackground,
    background = FHBackground,
    onBackground = FHOnSurface,
    surface = FHSurface,
    onSurface = FHOnSurface,
    surfaceVariant = FHSurfaceVariant,
    onSurfaceVariant = FHOnSurfaceVariant,
    error = FHRed,
    onError = Color.White,
    outline = FHDivider,
    outlineVariant = FHDivider
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00838F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2EBF2),
    onPrimaryContainer = Color(0xFF003544),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF003D33),
    tertiary = Color(0xFF5C6BC0),
    onTertiary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF44474F),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6CF)
)

@Composable
fun FHNavTheme(
    darkTheme: Boolean = true, // Default dark for FH4 style
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FHNavTypography,
        shapes = FHNavShapes,
        content = content
    )
}
