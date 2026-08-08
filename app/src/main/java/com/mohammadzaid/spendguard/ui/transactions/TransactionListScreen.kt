package com.mohammadzaid.spendguard.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohammadzaid.spendguard.data.model.Transaction
import com.mohammadzaid.spendguard.util.asCurrency
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("MMM d")

@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel,
    onTransactionClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Search merchant") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(Modifier.padding(vertical = 8.dp)) {
            FilterChip(
                selected = state.showFlaggedOnly,
                onClick = viewModel::onToggleFlaggedOnly,
                label = { Text("Flagged only") }
            )
        }

        LazyColumn {
            items(state.transactions, key = { it.id }) { txn ->
                TransactionRow(txn, onClick = { onTransactionClick(txn.id) })
                Divider()
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(transaction.merchant) },
        supportingContent = {
            Text("${transaction.category.displayName} · ${dateFormatter.format(transaction.timestamp.atZone(ZoneId.systemDefault()))}")
        },
        trailingContent = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(transaction.amount.asCurrency())
                if (transaction.isFlagged) {
                    Text(
                        "Flagged",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    )
}
