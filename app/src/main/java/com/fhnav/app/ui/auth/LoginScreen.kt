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
import com.fhnav.app.ui.theme.FHOnSurface
import com.fhnav.app.ui.theme.FHOnSurfaceVariant

@Composable
fun LoginScreen(
    onNavigateToVerify: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var phone by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onNavigateToMap()
    }

    LaunchedEffect(uiState.otpSent) {
        if (uiState.otpSent) {
            onNavigateToVerify(uiState.phone)
            viewModel.clearError()
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
            text = "FH 导航",
            style = MaterialTheme.typography.displayLarge,
            color = FHCyan
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "输入手机号登录",
            style = MaterialTheme.typography.bodyLarge,
            color = FHOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.filter { c -> c.isDigit() || c == '+' } },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("手机号") },
            placeholder = { Text("请输入手机号") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (phone.isNotBlank()) viewModel.sendOtp(phone)
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
            text = "获取验证码",
            onClick = { viewModel.sendOtp(phone) },
            enabled = phone.length >= 10 && !uiState.isLoading,
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
