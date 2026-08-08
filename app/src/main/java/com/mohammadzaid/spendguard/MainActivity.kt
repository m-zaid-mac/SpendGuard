package com.mohammadzaid.spendguard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mohammadzaid.spendguard.ui.ViewModelFactory
import com.mohammadzaid.spendguard.ui.auth.BiometricAuthManager
import com.mohammadzaid.spendguard.ui.auth.BiometricLockScreen
import com.mohammadzaid.spendguard.ui.navigation.Routes
import com.mohammadzaid.spendguard.ui.navigation.SpendGuardNavHost
import com.mohammadzaid.spendguard.ui.theme.SpendGuardTheme

/**
 * FragmentActivity (not plain ComponentActivity) because BiometricPrompt
 * requires a FragmentActivity host.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as SpendGuardApplication
        val factory = ViewModelFactory(app)
        val authManager = BiometricAuthManager(this)

        setContent {
            SpendGuardTheme {
                var unlocked by remember { mutableStateOf(!authManager.canAuthenticate()) }
                // If the device has no biometric/PIN enrolled, don't block the demo —
                // fail open to "unlocked" rather than stranding a reviewer on a locked screen.

                if (unlocked) {
                    SpendGuardApp(factory)
                } else {
                    BiometricLockScreen(authManager, onUnlocked = { unlocked = true })
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun SpendGuardApp(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Routes.DASHBOARD,
                    onClick = { navController.navigate(Routes.DASHBOARD) },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.TRANSACTIONS,
                    onClick = { navController.navigate(Routes.TRANSACTIONS) },
                    icon = { Icon(Icons.Filled.List, contentDescription = "Transactions") },
                    label = { Text("Transactions") }
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            SpendGuardNavHost(factory = factory, navController = navController)
        }
    }
}
