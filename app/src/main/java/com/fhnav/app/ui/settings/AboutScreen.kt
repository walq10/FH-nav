package com.fhnav.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fhnav.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FH4Background,
                    titleContentColor = FH4Text,
                    navigationIconContentColor = FH4Text
                )
            )
        },
        containerColor = FH4Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App icon/logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = FH4Primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FH",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    ),
                    color = FH4Primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "FH Navigation",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FH4Text
            )

            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = FH4TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Description
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FH4Surface)
            ) {
                Text(
                    text = "FH Navigation is a social navigation app inspired by Forza Horizon 4. " +
                           "Navigate with friends, share your location, and enjoy the ride together.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FH4TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Credits
            Text(
                text = "CREDITS",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = FH4Primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FH4Surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CreditItem(
                        icon = Icons.Default.Map,
                        title = "Maps",
                        subtitle = "AMap / 高德地图"
                    )
                    CreditItem(
                        icon = Icons.Default.Palette,
                        title = "Design",
                        subtitle = "Inspired by Forza Horizon 4"
                    )
                    CreditItem(
                        icon = Icons.Default.Code,
                        title = "Built with",
                        subtitle = "Jetpack Compose + Material 3"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Links
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FH4Surface)
            ) {
                Column {
                    LinkItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = { /* TODO: Open privacy policy */ }
                    )
                    LinkItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = { /* TODO: Open terms */ }
                    )
                    LinkItem(
                        icon = Icons.Default.BugReport,
                        title = "Report a Bug",
                        onClick = { /* TODO: Open bug report */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Copyright
            Text(
                text = "© 2024 FH Navigation Team",
                style = MaterialTheme.typography.bodySmall,
                color = FH4Disabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CreditItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FH4Primary,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = FH4Text
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = FH4TextSecondary
            )
        }
    }
}

@Composable
private fun LinkItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FH4TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = FH4Text,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = FH4Disabled,
            modifier = Modifier.size(20.dp)
        )
    }
}
