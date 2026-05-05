package com.fhnav.app.ui.welcome

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fhnav.app.ui.components.LoadingIndicator
import com.fhnav.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }

    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(800),
        label = "content_alpha"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (showSubtitle) 1f else 0f,
        animationSpec = tween(600),
        label = "subtitle_alpha"
    )

    // Logo scale animation
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    // Loading dots animation
    val loadingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_alpha"
    )

    LaunchedEffect(Unit) {
        // Animate in content
        delay(300)
        showContent = true
        delay(500)
        showSubtitle = true

        // TODO: Play welcome voice
        // Simulate voice playing duration
        delay(2500)

        // TODO: Check login state from repository
        val isLoggedIn = false // placeholder
        if (isLoggedIn) {
            onNavigateToMap()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FH4Background,
                        FH4Surface,
                        FH4SurfaceVariant.copy(alpha = 0.5f),
                        FH4Background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(contentAlpha)
        ) {
            // Logo with pulse animation
            Text(
                text = "FH",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 100.sp,
                    letterSpacing = 12.sp
                ),
                color = FH4Primary,
                modifier = Modifier.scale(logoScale)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "NAVIGATION",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 16.sp
                ),
                color = FH4TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Subtitle with fade in
            Text(
                text = "Your journey begins here",
                style = MaterialTheme.typography.bodyLarge,
                color = FH4TextSecondary,
                modifier = Modifier.alpha(subtitleAlpha)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            LoadingIndicator(
                modifier = Modifier.alpha(loadingAlpha)
            )
        }

        // Bottom branding
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Powered by AMap",
                style = MaterialTheme.typography.bodySmall,
                color = FH4Disabled
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = FH4Disabled
            )
        }
    }
}
