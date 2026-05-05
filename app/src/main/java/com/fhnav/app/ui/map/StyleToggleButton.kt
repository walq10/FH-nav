package com.fhnav.app.ui.map

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHBackground

@Composable
fun StyleToggleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        containerColor = FHSurface,
        contentColor = FHCyan
    ) {
        Icon(
            imageVector = Icons.Default.Map,
            contentDescription = "切换地图样式"
        )
    }
}
