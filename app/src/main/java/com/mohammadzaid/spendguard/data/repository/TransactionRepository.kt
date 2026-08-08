package com.mohammadzaid.spendguard.data.repository

import android.content.Context
import com.mohammadzaid.spendguard.data.local.AppDatabase
import com.mohammadzaid.spendguard.data.local.SeedDataProvider
import com.mohammadzaid.spendguard.data.local.TransactionEntity
import com.mohammadzaid.spendguard.data.model.Category
import com.mohammadzaid.spendguard.data.model.CategorizedTransaction
import com.mohammadzaid.spendguard.data.model.Transaction
import com.mohammadzaid.spendguard.domain.CategorizationEngine
import com.mohammadzaid.spendguard.domain.RiskEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

/**
 * Single source of truth for transactions. The ViewModels never see Room or
 * the engines directly — they collect [observeTransactions] and get fully
 * resolved domain models back.
 *
 * On first run, seeds the local DB from the mock JSON feed so the app has
 * realistic-looking data without a backend. Swapping this for a real API
 * later only touches this class and SeedDataProvider — nothing above it.
 */
class TransactionRepository(
    context: Context,
    private val categorizationEngine: CategorizationEngine = CategorizationEngine(),
    private val riskEngine: RiskEngine = RiskEngine()
) {
    private val appContext = context.applicationContext
    private val dao = AppDatabase.getInstance(appContext).transactionDao()

    suspend fun seedIfEmpty() {
        if (dao.count() == 0) {
            dao.insertAll(SeedDataProvider.loadSeedTransactions(appContext))
        }
    }

    fun observeTransactions(): Flow<List<Transaction>> =
        dao.observeAll().map { entities -> resolve(entities) }

    suspend fun setManualCategory(transactionId: String, category: Category) {
        val entity = dao.getById(transactionId) ?: return
        dao.update(entity.copy(manualCategory = category.name))
    }

    /**
     * Two-pass resolution: categorize every row first (cheap, per-row), then
     * run risk detection over the categorized batch (needs the whole set).
     * This mirrors the engines' own constraints rather than fighting them.
     */
    private fun resolve(entities: List<TransactionEntity>): List<Transaction> {
        val categorized = entities.map { entity ->
            val category = entity.manualCategory
                ?.let { runCatching { Category.valueOf(it) }.getOrNull() }
                ?: categorizationEngine.categorize(entity.merchant)

            CategorizedTransaction(
                id = entity.id,
                merchant = entity.merchant,
                amount = entity.amountCents / 100.0,
                timestamp = Instant.ofEpochMilli(entity.timestampEpochMillis),
                cardLastFour = entity.cardLastFour,
                category = category
            )
        }

        val riskFlagsById = riskEngine.evaluate(categorized)

        return categorized.map { txn ->
            Transaction(
                id = txn.id,
                merchant = txn.merchant,
                amount = txn.amount,
                timestamp = txn.timestamp,
                cardLastFour = txn.cardLastFour,
                category = txn.category,
                riskFlags = riskFlagsById[txn.id].orEmpty()
            )
        }
    }
}
