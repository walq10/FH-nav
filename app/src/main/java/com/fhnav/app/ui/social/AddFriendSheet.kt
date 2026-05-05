package com.fhnav.app.ui.social

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurfaceVariant
import com.fhnav.app.ui.theme.FHSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendSheet(
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = FHSurface,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "添加好友",
                style = MaterialTheme.typography.titleLarge,
                color = FHCyan
            )

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    if (it.length >= 3) onSearch(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                label = { Text("搜索手机号或昵称") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FHCyan,
                    focusedLabelColor = FHCyan,
                    cursorColor = FHCyan,
                    unfocusedBorderColor = FHOnSurfaceVariant.copy(alpha = 0.3f)
                )
            )
        }
    }
}
