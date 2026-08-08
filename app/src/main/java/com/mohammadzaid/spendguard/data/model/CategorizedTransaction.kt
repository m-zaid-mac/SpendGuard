package com.mohammadzaid.spendguard.data.model

import java.time.Instant

/**
 * A transaction after CategorizationEngine has run but before RiskEngine has.
 * Risk detection needs the whole batch (to compute category averages, spot
 * duplicates, and measure velocity) so it can't happen row-by-row like
 * categorization can — this type marks that pipeline stage explicitly.
 */
data class CategorizedTransaction(
    val id: String,
    val merchant: String,
    val amount: Double,
    val timestamp: Instant,
    val cardLastFour: String,
    val category: Category
)
