package com.fhnav.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fhnav.app.ui.components.ErrorDialog
import com.fhnav.app.ui.components.FHNavButton
import com.fhnav.app.ui.theme.FHBackground
import com.fhnav.app.ui.theme.FHCyan
import com.fhnav.app.ui.theme.FHOnSurfaceVariant

@Composable
fun VerifyCodeScreen(
    phone: String,
    onNavigateToNickname: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var code by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && uiState.user?.nickname.isNullOrBlank()) {
            onNavigateToNickname()
        } else if (uiState.isLoggedIn) {
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
            text = "输入验证码",
            style = MaterialTheme.typography.headlineLarge,
            color = FHCyan
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "验证码已发送至 $phone",
            style = MaterialTheme.typography.bodyMedium,
            color = FHOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(6) { index ->
                val digit = code.getOrNull(index)?.toString() ?: ""
                OutlinedTextField(
                    value = digit,
                    onValueChange = { },
                    modifier = Modifier
                        .width(48.dp)
                        .height(56.dp),
                    readOnly = true,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FHCyan,
                        unfocusedBorderColor = FHOnSurfaceVariant.copy(alpha = 0.3f)
                    )
                )
                if (index < 5) Spacer(modifier = Modifier.width(4.dp))
            }
        }

        // Hidden input field
        OutlinedTextField(
            value = code,
            onValueChange = { newCode ->
                code = newCode.filter { it.isDigit() }.take(6)
                if (code.length == 6) {
                    viewModel.verifyOtp(code)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(top = 1.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (code.length == 6) viewModel.verifyOtp(code) }
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FHCyan,
                cursorColor = FHCyan
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        FHNavButton(
            text = "验证",
            onClick = { viewModel.verifyOtp(code) },
            enabled = code.length == 6 && !uiState.isLoading,
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
