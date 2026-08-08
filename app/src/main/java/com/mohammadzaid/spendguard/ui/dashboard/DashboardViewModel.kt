package com.mohammadzaid.spendguard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammadzaid.spendguard.data.model.Category
import com.mohammadzaid.spendguard.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategorySpend(val category: Category, val total: Double)

data class DashboardUiState(
    val totalSpend: Double = 0.0,
    val flaggedCount: Int = 0,
    val spendByCategory: List<CategorySpend> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
            repository.observeTransactions().collect { transactions ->
                val byCategory = transactions
                    .groupBy { it.category }
                    .map { (category, txns) -> CategorySpend(category, txns.sumOf { it.amount }) }
                    .sortedByDescending { it.total }

                _uiState.value = DashboardUiState(
                    totalSpend = transactions.sumOf { it.amount },
                    flaggedCount = transactions.count { it.isFlagged },
                    spendByCategory = byCategory,
                    isLoading = false
                )
            }
        }
    }
}
