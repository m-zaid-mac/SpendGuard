package com.mohammadzaid.spendguard.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohammadzaid.spendguard.util.asCurrency

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("This month", style = MaterialTheme.typography.labelLarge)
        Text(state.totalSpend.asCurrency(), style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(8.dp))

        if (state.flaggedCount > 0) {
            Card(
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${state.flaggedCount} transaction${if (state.flaggedCount == 1) "" else "s"} flagged for review",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Spend by category", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(state.spendByCategory) { entry ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(entry.category.displayName, style = MaterialTheme.typography.bodyLarge)
                    Text(entry.total.asCurrency(), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
