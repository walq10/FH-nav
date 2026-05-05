package com.fhnav.app.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fhnav.app.data.model.Phrase
import com.fhnav.app.data.model.PhraseCategory
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHOnSurfaceVariant
import com.fhnav.app.ui.theme.FHSurface
import com.fhnav.app.ui.theme.FHSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PhrasePanel(
    phrases: List<Phrase>,
    selectedCategory: PhraseCategory?,
    onCategorySelect: (PhraseCategory?) -> Unit,
    onPhraseClick: (Phrase) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Category chips
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelect(null) },
                label = { Text("全部") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FHCyan,
                    selectedLabelColor = FHBackground
                )
            )
            PhraseCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category.zhName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FHCyan,
                        selectedLabelColor = FHBackground
                    )
                )
            }
        }

        // Phrase list
        phrases.forEach { phrase ->
            PhraseBubble(
                phrase = phrase,
                onClick = { onPhraseClick(phrase) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
