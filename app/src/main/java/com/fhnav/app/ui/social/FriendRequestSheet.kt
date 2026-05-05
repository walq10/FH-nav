package com.fhnav.app.ui.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fhnav.app.data.model.Friendship
import com.fhnav.app.ui.components.FHNavButton
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestSheet(
    requests: List<Friendship>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onDismiss: () -> Unit
) {
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
                text = "好友请求 (${requests.size})",
                style = MaterialTheme.typography.titleLarge,
                color = FHCyan
            )

            Spacer(modifier = Modifier.height(16.dp))

            requests.forEach { request ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = FHSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = request.friend?.nickname ?: "未知用户",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FHOnSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FHNavButton(text = "接受", onClick = { onAccept(request.id) })
                            FHNavButton(text = "拒绝", onClick = { onReject(request.id) }, isSecondary = true)
                        }
                    }
                }
            }

            if (requests.isEmpty()) {
                Text(
                    text = "暂无好友请求",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FHOnSurface,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            }
        }
    }
}
