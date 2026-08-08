package com.mohammadzaid.spendguard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.mohammadzaid.spendguard.SpendGuardApplication
import com.mohammadzaid.spendguard.ui.dashboard.DashboardViewModel
import com.mohammadzaid.spendguard.ui.detail.TransactionDetailViewModel
import com.mohammadzaid.spendguard.ui.transactions.TransactionListViewModel

class ViewModelFactory(private val app: SpendGuardApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return when (modelClass) {
            DashboardViewModel::class.java -> DashboardViewModel(app.repository) as T
            TransactionListViewModel::class.java -> TransactionListViewModel(app.repository) as T
            TransactionDetailViewModel::class.java -> TransactionDetailViewModel(app.repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
