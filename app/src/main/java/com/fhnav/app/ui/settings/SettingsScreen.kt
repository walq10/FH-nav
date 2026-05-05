package com.fhnav.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fhnav.app.ui.components.ErrorDialog
import com.fhnav.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Language section
            SettingsSection(title = "Language") {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = uiState.language.displayName,
                    onClick = { viewModel.showLanguagePicker() }
                )
            }

            // Voice section
            SettingsSection(title = "Voice") {
                SettingsToggleItem(
                    icon = Icons.Default.VolumeUp,
                    title = "Voice Navigation",
                    subtitle = if (uiState.isVoiceEnabled) "Enabled" else "Disabled",
                    checked = uiState.isVoiceEnabled,
                    onCheckedChange = { viewModel.toggleVoice() }
                )

                if (uiState.isVoiceEnabled) {
                    SettingsSliderItem(
                        icon = Icons.Default.VolumeDown,
                        title = "Volume",
                        value = uiState.voiceVolume,
                        onValueChange = { viewModel.setVoiceVolume(it) }
                    )
                }
            }

            // Map section
            SettingsSection(title = "Map") {
                SettingsToggleItem(
                    icon = if (uiState.isDarkMapStyle) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title = "Dark Map Style",
                    subtitle = if (uiState.isDarkMapStyle) "Dark theme" else "Light theme",
                    checked = uiState.isDarkMapStyle,
                    onCheckedChange = { viewModel.toggleDarkMapStyle() }
                )
            }

            // Privacy section
            SettingsSection(title = "Privacy") {
                SettingsToggleItem(
                    icon = Icons.Default.ShareLocation,
                    title = "Location Sharing",
                    subtitle = if (uiState.isLocationSharing) "Friends can see your location" else "Location is private",
                    checked = uiState.isLocationSharing,
                    onCheckedChange = { viewModel.toggleLocationSharing() }
                )
            }

            // About section
            SettingsSection(title = "Other") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "Version ${uiState.appVersion}",
                    onClick = onNavigateToAbout
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout button
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FH4Error.copy(alpha = 0.15f),
                    contentColor = FH4Error
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = FH4Error,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Language picker dialog
        if (uiState.showLanguagePicker) {
            LanguagePickerDialog(
                currentLanguage = uiState.language,
                onSelect = { viewModel.setLanguage(it) },
                onDismiss = { viewModel.hideLanguagePicker() }
            )
        }

        // Error dialog
        uiState.error?.let { error ->
            ErrorDialog(
                message = error,
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (1.5).dp
            ),
            color = FH4Primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FH4Surface)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FH4TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = FH4Text
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = FH4TextSecondary
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = FH4Disabled,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FH4TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = FH4Text
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = FH4TextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = FH4Primary,
                checkedTrackColor = FH4Primary.copy(alpha = 0.3f),
                uncheckedThumbColor = FH4TextSecondary,
                uncheckedTrackColor = FH4SurfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FH4TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = FH4Text
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(
                    thumbColor = FH4Primary,
                    activeTrackColor = FH4Primary,
                    inactiveTrackColor = FH4SurfaceVariant
                )
            )
        }
        Text(
            text = "${(value * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
            color = FH4TextSecondary
        )
    }
}

@Composable
private fun LanguagePickerDialog(
    currentLanguage: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Language",
                color = FH4Text
            )
        },
        text = {
            Column {
                AppLanguage.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(language) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = language == currentLanguage,
                            onClick = { onSelect(language) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = FH4Primary,
                                unselectedColor = FH4TextSecondary
                            )
                        )
                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = FH4Text
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FH4TextSecondary)
            }
        },
        containerColor = FH4Surface,
        shape = DialogShape
    )
}
