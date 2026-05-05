package com.fhnav.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fhnav.app.ui.components.ErrorDialog
import com.fhnav.app.ui.components.FHNavButton
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurfaceVariant

@Composable
fun NicknameScreen(
    onNavigateToMap: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var nickname by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Navigate when nickname is set and user exists
    LaunchedEffect(uiState.user?.nickname) {
        if (!uiState.user?.nickname.isNullOrBlank()) {
            onNavigateToMap()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FHBackground)
            .padding(horizontal = 32.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "设置昵称",
            style = MaterialTheme.typography.headlineLarge,
            color = FHCyan
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "好友将通过昵称找到你",
            style = MaterialTheme.typography.bodyMedium,
            color = FHOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = { if (it.length <= 20) nickname = it },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("昵称") },
            placeholder = { Text("输入你的昵称") },
            supportingText = { Text("${nickname.length}/20") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (nickname.isNotBlank()) viewModel.updateNickname(nickname)
                }
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FHCyan,
                focusedLabelColor = FHCyan,
                cursorColor = FHCyan,
                unfocusedBorderColor = FHOnSurfaceVariant.copy(alpha = 0.3f),
                unfocusedLabelColor = FHOnSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        FHNavButton(
            text = "开始使用",
            onClick = { viewModel.updateNickname(nickname) },
            enabled = nickname.isNotBlank() && !uiState.isLoading,
            isLoading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }

    uiState.error?.let { error ->
        ErrorDialog(
            message = error,
            onDismiss = { viewModel.clearError() }
        )
    }
}
