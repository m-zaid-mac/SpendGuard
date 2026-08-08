package com.mohammadzaid.spendguard.data.model

/**
 * A reason a transaction was flagged by [com.mohammadzaid.spendguard.domain.RiskEngine].
 * Modeled as a sealed class rather than a boolean so the detail screen can explain
 * *why* something was flagged, not just that it was.
 */
sealed class RiskFlag(val explanation: String) {

    data class UnusualAmount(val amount: Double, val categoryAverage: Double) :
        RiskFlag("This is ${"%.1f".format(amount / categoryAverage)}x this category's average spend")

    data class DuplicateCharge(val originalMerchant: String, val minutesApart: Long) :
        RiskFlag("Matches a charge from $originalMerchant $minutesApart minutes earlier")

    data class HighVelocity(val transactionCountInWindow: Int) :
        RiskFlag("$transactionCountInWindow charges from this card within 1 hour")

    data class NewMerchantHighAmount(val amount: Double) :
        RiskFlag("First transaction with this merchant, above the new-merchant threshold")
}
