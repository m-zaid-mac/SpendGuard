package com.mohammadzaid.spendguard.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammadzaid.spendguard.data.model.Transaction
import com.mohammadzaid.spendguard.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val query: String = "",
    val showFlaggedOnly: Boolean = false
)

class TransactionListViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val query = MutableStateFlow("")
    private val showFlaggedOnly = MutableStateFlow(false)

    val uiState: StateFlow<TransactionListUiState> =
        combine(
            repository.observeTransactions(),
            query,
            showFlaggedOnly
        ) { txns, q, flaggedOnly ->
            val filtered = txns
                .filter { !flaggedOnly || it.isFlagged }
                .filter { q.isBlank() || it.merchant.contains(q, ignoreCase = true) }
            TransactionListUiState(filtered, q, flaggedOnly)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionListUiState()
        )

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onToggleFlaggedOnly() {
        showFlaggedOnly.value = !showFlaggedOnly.value
    }
}
