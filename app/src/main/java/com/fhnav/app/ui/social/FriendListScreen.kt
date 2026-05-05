package com.fhnav.app.ui.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fhnav.app.data.model.User
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHOnSurfaceVariant
import com.fhnav.app.ui.theme.FHSurface
import com.fhnav.app.ui.theme.FHSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(
    onNavigateBack: () -> Unit,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = FHBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text("好友", style = MaterialTheme.typography.titleLarge, color = FHOnSurface)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回", tint = FHOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FHBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Show add friend sheet */ },
                containerColor = FHCyan,
                contentColor = FHBackground
            ) {
                Icon(Icons.Default.Add, "添加好友")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pending requests section
            if (uiState.pendingRequests.isNotEmpty()) {
                item {
                    Text(
                        text = "待处理请求 (${uiState.pendingRequests.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = FHCyan,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(uiState.pendingRequests) { request ->
                    FriendRequestItem(
                        friendship = request,
                        onAccept = { viewModel.acceptRequest(request.id) },
                        onReject = { viewModel.rejectRequest(request.id) }
                    )
                }
            }

            // Friends list
            item {
                Text(
                    text = "好友 (${uiState.friends.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = FHOnSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(uiState.friends) { friend ->
                FriendItem(user = friend)
            }

            if (uiState.friends.isEmpty() && uiState.pendingRequests.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "还没有好友",
                            style = MaterialTheme.typography.bodyLarge,
                            color = FHOnSurfaceVariant
                        )
                        Text(
                            text = "点击右下角 + 添加好友",
                            style = MaterialTheme.typography.bodySmall,
                            color = FHOnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendItem(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FHSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FHSurfaceVariant)
                    .padding(8.dp),
                tint = FHCyan
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = user.nickname.ifBlank { user.phone },
                style = MaterialTheme.typography.bodyLarge,
                color = FHOnSurface
            )
        }
    }
}

@Composable
private fun FriendRequestItem(
    friendship: com.fhnav.app.data.model.Friendship,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FHSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = friendship.friend?.nickname ?: "未知用户",
                style = MaterialTheme.typography.bodyLarge,
                color = FHOnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FHNavButton(text = "接受", onClick = onAccept)
                FHNavButton(text = "拒绝", onClick = onReject, isSecondary = true)
            }
        }
    }
}
