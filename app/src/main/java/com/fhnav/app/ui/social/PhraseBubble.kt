package com.fhnav.app.ui.social

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fhnav.app.ui.map.ReceivedPhrase
import com.fhnav.app.ui.theme.*

@Composable
fun PhraseBubble(
    phrase: ReceivedPhrase,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Slide-in animation
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(phrase.id) {
        isVisible = true
        kotlinx.coroutines.delay(3000)
        isVisible = false
        kotlinx.coroutines.delay(400) // Wait for exit animation
        onDismiss()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = FH4Surface.copy(alpha = 0.95f)
            ),
            elevation = androidx.compose.material3.CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sender name with accent color
                Text(
                    text = phrase.senderName,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = FH4Primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(16.dp)
                        .background(FH4Outline)
                )

                // Phrase text
                Text(
                    text = phrase.phrase,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = FH4Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
