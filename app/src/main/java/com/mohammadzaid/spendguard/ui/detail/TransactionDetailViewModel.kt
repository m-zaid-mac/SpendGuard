package com.mohammadzaid.spendguard.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammadzaid.spendguard.data.model.Category
import com.mohammadzaid.spendguard.data.model.Transaction
import com.mohammadzaid.spendguard.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionDetailViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    fun transactionStream(transactionId: String): StateFlow<Transaction?> =
        repository.observeTransactions()
            .map { list -> list.firstOrNull { it.id == transactionId } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun recategorize(transactionId: String, category: Category) {
        viewModelScope.launch {
            repository.setManualCategory(transactionId, category)
        }
    }
}
