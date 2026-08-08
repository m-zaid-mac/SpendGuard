package com.mohammadzaid.spendguard.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohammadzaid.spendguard.data.model.Category
import com.mohammadzaid.spendguard.util.asCurrency
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val fullFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")

@Composable
fun TransactionDetailScreen(
    transactionId: String,
    viewModel: TransactionDetailViewModel
) {
    val stream = remember(transactionId) { viewModel.transactionStream(transactionId) }
    val transaction by stream.collectAsState()

    val txn = transaction
    if (txn == null) {
        CircularProgressIndicator(Modifier.padding(32.dp))
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(txn.merchant, style = MaterialTheme.typography.headlineSmall)
        Text(txn.amount.asCurrency(), style = MaterialTheme.typography.headlineMedium)
        Text(
            fullFormatter.format(txn.timestamp.atZone(ZoneId.systemDefault())),
            style = MaterialTheme.typography.bodyMedium
        )
        Text("Card ending ${txn.cardLastFour.takeLast(4)}", style = MaterialTheme.typography.bodyMedium)

        Column(Modifier.padding(top = 16.dp)) {
            Text("Category", style = MaterialTheme.typography.titleMedium)
            LazyRow(Modifier.padding(top = 8.dp)) {
                items(Category.values().toList()) { category ->
                    FilterChip(
                        selected = category == txn.category,
                        onClick = { viewModel.recategorize(txn.id, category) },
                        label = { Text(category.displayName) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        if (txn.isFlagged) {
            Column(Modifier.padding(top = 24.dp)) {
                Text("Why this was flagged", style = MaterialTheme.typography.titleMedium)
                txn.riskFlags.forEach { flag ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text(
                            flag.explanation,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
