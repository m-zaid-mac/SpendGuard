package com.mohammadzaid.spendguard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Raw persisted row. Deliberately "dumb" — no category or risk data lives here.
 * Those are derived at read time by CategorizationEngine / RiskEngine so the
 * business logic stays testable in plain Kotlin, independent of Room.
 *
 * manualCategory is set when a user overrides the engine's guess; the engine
 * result is used whenever this is null.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val merchant: String,
    val amountCents: Long,
    val timestampEpochMillis: Long,
    val manualCategory: String? = null,
    val cardLastFour: String
)
