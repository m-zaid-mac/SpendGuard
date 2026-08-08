package com.mohammadzaid.spendguard.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BiometricLockScreen(
    authManager: BiometricAuthManager,
    onUnlocked: () -> Unit
) {
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text("SpendGuard is locked", style = MaterialTheme.typography.titleLarge)
        Text(
            "Verify it's you to view company spend",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )
        Button(onClick = {
            authManager.authenticate(
                onSuccess = onUnlocked,
                onFailure = { message -> error = message }
            )
        }) {
            Text("Unlock")
        }
        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
