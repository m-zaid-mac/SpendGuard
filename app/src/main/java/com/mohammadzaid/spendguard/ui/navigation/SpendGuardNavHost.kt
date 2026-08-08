package com.mohammadzaid.spendguard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mohammadzaid.spendguard.ui.ViewModelFactory
import com.mohammadzaid.spendguard.ui.dashboard.DashboardScreen
import com.mohammadzaid.spendguard.ui.dashboard.DashboardViewModel
import com.mohammadzaid.spendguard.ui.detail.TransactionDetailScreen
import com.mohammadzaid.spendguard.ui.detail.TransactionDetailViewModel
import com.mohammadzaid.spendguard.ui.transactions.TransactionListScreen
import com.mohammadzaid.spendguard.ui.transactions.TransactionListViewModel

object Routes {
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
    const val DETAIL = "transactions/{transactionId}"
    fun detail(id: String) = "transactions/$id"
}

@Composable
fun SpendGuardNavHost(
    factory: ViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            val vm: DashboardViewModel = viewModel(factory = factory)
            DashboardScreen(vm)
        }
        composable(Routes.TRANSACTIONS) {
            val vm: TransactionListViewModel = viewModel(factory = factory)
            TransactionListScreen(vm, onTransactionClick = { id ->
                navController.navigate(Routes.detail(id))
            })
        }
        composable(Routes.DETAIL) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("transactionId").orEmpty()
            val vm: TransactionDetailViewModel = viewModel(factory = factory)
            TransactionDetailScreen(id, vm)
        }
    }
}
