package com.mohammadzaid.spendguard.domain

import com.mohammadzaid.spendguard.data.model.CategorizedTransaction
import com.mohammadzaid.spendguard.data.model.RiskFlag
import java.time.Duration
import kotlin.math.abs

/**
 * Rule-based anomaly detection over a batch of transactions.
 *
 * Unlike CategorizationEngine, this can't work one row at a time — "is this
 * amount unusual" and "is this a duplicate" only make sense relative to the
 * rest of the batch. That's why it takes a List and returns a Map, rather
 * than exposing a per-transaction function.
 *
 * Thresholds are tunable constants rather than magic numbers so they're easy
 * to justify and adjust — a real system would likely learn these per-company
 * rather than hardcoding them globally.
 */
class RiskEngine(
    private val unusualAmountMultiplier: Double = 3.0,
    private val duplicateWindow: Duration = Duration.ofMinutes(30),
    private val velocityWindow: Duration = Duration.ofHours(1),
    private val velocityThreshold: Int = 4,
    private val newMerchantHighAmount: Double = 1000.0
) {

    fun evaluate(transactions: List<CategorizedTransaction>): Map<String, List<RiskFlag>> {
        val flags = mutableMapOf<String, MutableList<RiskFlag>>()
        fun flag(id: String, riskFlag: RiskFlag) {
            flags.getOrPut(id) { mutableListOf() }.add(riskFlag)
        }

        checkUnusualAmounts(transactions, ::flag)
        checkDuplicates(transactions, ::flag)
        checkVelocity(transactions, ::flag)
        checkNewMerchantHighAmount(transactions, ::flag)

        return flags
    }

    /** Flags transactions whose amount is far above their category's average. */
    private fun checkUnusualAmounts(
        transactions: List<CategorizedTransaction>,
        flag: (String, RiskFlag) -> Unit
    ) {
        transactions.groupBy { it.category }.forEach { (_, group) ->
            if (group.size < 3) return@forEach // not enough data to call anything "unusual"
            val average = group.sumOf { it.amount } / group.size
            group.forEach { txn ->
                if (average > 0 && txn.amount >= average * unusualAmountMultiplier) {
                    flag(txn.id, RiskFlag.UnusualAmount(txn.amount, average))
                }
            }
        }
    }

    /** Flags charges to the same merchant, for the same amount, close together in time. */
    private fun checkDuplicates(
        transactions: List<CategorizedTransaction>,
        flag: (String, RiskFlag) -> Unit
    ) {
        val sorted = transactions.sortedBy { it.timestamp }
        for (i in sorted.indices) {
            for (j in (i + 1) until sorted.size) {
                val a = sorted[i]
                val b = sorted[j]
                val gap = Duration.between(a.timestamp, b.timestamp)
                if (gap > duplicateWindow) break // sorted by time, nothing further will be closer
                val sameCharge = a.merchant == b.merchant && abs(a.amount - b.amount) < 0.01
                if (sameCharge) {
                    flag(b.id, RiskFlag.DuplicateCharge(a.merchant, gap.toMinutes()))
                }
            }
        }
    }

    /** Flags bursts of activity on the same card within a short window. */
    private fun checkVelocity(
        transactions: List<CategorizedTransaction>,
        flag: (String, RiskFlag) -> Unit
    ) {
        transactions.groupBy { it.cardLastFour }.forEach { (_, cardTxns) ->
            val sorted = cardTxns.sortedBy { it.timestamp }
            for (i in sorted.indices) {
                val windowEnd = sorted[i].timestamp.plus(velocityWindow)
                val windowTxns = sorted.drop(i).takeWhile { it.timestamp <= windowEnd }
                if (windowTxns.size >= velocityThreshold) {
                    windowTxns.forEach { flag(it.id, RiskFlag.HighVelocity(windowTxns.size)) }
                }
            }
        }
    }

    /** Flags a large first-time charge to a merchant never seen before in this batch. */
    private fun checkNewMerchantHighAmount(
        transactions: List<CategorizedTransaction>,
        flag: (String, RiskFlag) -> Unit
    ) {
        val sorted = transactions.sortedBy { it.timestamp }
        val seenMerchants = mutableSetOf<String>()
        for (txn in sorted) {
            if (txn.merchant !in seenMerchants && txn.amount >= newMerchantHighAmount) {
                flag(txn.id, RiskFlag.NewMerchantHighAmount(txn.amount))
            }
            seenMerchants.add(txn.merchant)
        }
    }
}
