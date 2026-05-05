package com.fhnav.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fhnav.app.ui.theme.*

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    title: String = "Error"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = FH4Error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FH4Text,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = FH4TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            if (onRetry != null) {
                Button(
                    onClick = {
                        onRetry()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FH4Primary,
                        contentColor = FH4Background
                    ),
                    shape = ButtonShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "OK",
                        color = FH4Primary,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        },
        dismissButton = if (onRetry != null) {
            {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Dismiss",
                        color = FH4TextSecondary
                    )
                }
            }
        } else null,
        containerColor = FH4Surface,
        shape = DialogShape
    )
}

@Composable
fun WarningDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "Warning",
    confirmText: String = "Confirm",
    dismissText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = FH4Text
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = FH4TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FH4Warning,
                    contentColor = FH4Background
                ),
                shape = ButtonShape
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = FH4TextSecondary)
            }
        },
        containerColor = FH4Surface,
        shape = DialogShape
    )
}
