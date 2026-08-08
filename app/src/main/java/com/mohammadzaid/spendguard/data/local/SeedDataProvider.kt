package com.mohammadzaid.spendguard.data.local

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class SeedTransaction(
    val id: String,
    val merchant: String,
    val amountCents: Long,
    val timestampEpochMillis: Long,
    val cardLastFour: String
)

/**
 * Reads app/src/main/assets/mock_transactions.json and converts it into
 * TransactionEntity rows. Stands in for a real transaction feed (a card
 * network webhook, in Ramp's case) so the rest of the app — Room, the
 * engines, the UI — behaves the same way it would against live data.
 */
object SeedDataProvider {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadSeedTransactions(context: Context): List<TransactionEntity> {
        val raw = context.assets.open("mock_transactions.json")
            .bufferedReader()
            .use { it.readText() }

        val seed = json.decodeFromString<List<SeedTransaction>>(raw)

        return seed.map {
            TransactionEntity(
                id = it.id,
                merchant = it.merchant,
                amountCents = it.amountCents,
                timestampEpochMillis = it.timestampEpochMillis,
                cardLastFour = it.cardLastFour
            )
        }
    }
}
