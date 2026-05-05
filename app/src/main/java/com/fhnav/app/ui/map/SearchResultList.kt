package com.fhnav.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fhnav.app.data.model.SearchResult
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHOnSurfaceVariant
import com.fhnav.app.ui.theme.FHSurface

@Composable
fun SearchResultList(
    results: List<SearchResult>,
    recentSearches: List<SearchResult>,
    onResultClick: (SearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(FHSurface.copy(alpha = 0.95f))
    ) {
        if (results.isEmpty() && recentSearches.isNotEmpty()) {
            item {
                Text(
                    text = "最近搜索",
                    style = MaterialTheme.typography.labelLarge,
                    color = FHOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(recentSearches) { result ->
                SearchResultItem(
                    result = result,
                    isRecent = true,
                    onClick = { onResultClick(result) }
                )
            }
        }

        if (results.isNotEmpty()) {
            item {
                Text(
                    text = "搜索结果",
                    style = MaterialTheme.typography.labelLarge,
                    color = FHOnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(results) { result ->
                SearchResultItem(
                    result = result,
                    isRecent = false,
                    onClick = { onResultClick(result) }
                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    result: SearchResult,
    isRecent: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isRecent) Icons.Default.History else Icons.Default.LocationOn,
            contentDescription = null,
            tint = if (isRecent) FHOnSurfaceVariant else FHCyan,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.name,
                style = MaterialTheme.typography.bodyLarge,
                color = FHOnSurface
            )
            if (result.address.isNotBlank()) {
                Text(
                    text = result.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = FHOnSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        if (result.distance.isNotBlank()) {
            Text(
                text = result.distance,
                style = MaterialTheme.typography.bodySmall,
                color = FHCyan
            )
        }
    }
}
