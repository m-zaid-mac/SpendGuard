package com.mohammadzaid.spendguard.data.model

import java.time.Instant

/**
 * Fully-resolved transaction: raw fields plus the category and risk flags
 * assigned by the domain engines. This is what the UI layer works with —
 * it never touches TransactionEntity directly.
 */
data class Transaction(
    val id: String,
    val merchant: String,
    val amount: Double,
    val timestamp: Instant,
    val cardLastFour: String,
    val category: Category,
    val riskFlags: List<RiskFlag>
) {
    val isFlagged: Boolean get() = riskFlags.isNotEmpty()
}
