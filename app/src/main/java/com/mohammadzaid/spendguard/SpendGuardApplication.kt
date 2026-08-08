package com.mohammadzaid.spendguard

import android.app.Application
import com.mohammadzaid.spendguard.data.repository.TransactionRepository

/**
 * Manual dependency provision instead of Hilt/Dagger. For an app this size
 * a DI framework is overhead without payoff — this is a deliberate scope
 * call, not an oversight, and worth saying out loud in an interview.
 */
class SpendGuardApplication : Application() {
    val repository: TransactionRepository by lazy { TransactionRepository(this) }
}
