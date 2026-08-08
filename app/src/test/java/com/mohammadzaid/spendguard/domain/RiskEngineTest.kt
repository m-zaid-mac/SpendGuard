package com.mohammadzaid.spendguard.domain

import com.mohammadzaid.spendguard.data.model.Category
import com.mohammadzaid.spendguard.data.model.CategorizedTransaction
import com.mohammadzaid.spendguard.data.model.RiskFlag
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class RiskEngineTest {

    private val engine = RiskEngine()
    private val baseTime = Instant.parse("2026-08-01T12:00:00Z")

    private fun txn(id: String, merchant: String, amount: Double, minutesOffset: Long = 0, card: String = "1234") =
        CategorizedTransaction(
            id = id,
            merchant = merchant,
            amount = amount,
            timestamp = baseTime.plus(minutesOffset, ChronoUnit.MINUTES),
            cardLastFour = card,
            category = Category.MEALS
        )

    @Test
    fun `flags amount far above category average`() {
        val transactions = listOf(
            txn("1", "Sweetgreen", 15.0, minutesOffset = 0),
            txn("2", "Sweetgreen", 18.0, minutesOffset = 60 * 24),
            txn("3", "Sweetgreen", 20.0, minutesOffset = 60 * 24 * 2),
            txn("4", "Sweetgreen", 410.0, minutesOffset = 60 * 24 * 3) // ~3.5x the ~116 average
        )
        val flags = engine.evaluate(transactions)
        assertTrue(flags["4"]?.any { it is RiskFlag.UnusualAmount } == true)
        assertFalse(flags.containsKey("1"))
    }

    @Test
    fun `does not flag unusual amount with too little data`() {
        // Only 2 transactions in the category — not enough to call anything "average"
        val transactions = listOf(txn("1", "Sweetgreen", 15.0), txn("2", "Sweetgreen", 400.0))
        val flags = engine.evaluate(transactions)
        assertFalse(flags.containsKey("2"))
    }

    @Test
    fun `flags duplicate charge within window`() {
        val transactions = listOf(
            txn("1", "WeWork", 550.0, minutesOffset = 0),
            txn("2", "WeWork", 550.0, minutesOffset = 4)
        )
        val flags = engine.evaluate(transactions)
        assertTrue(flags["2"]?.any { it is RiskFlag.DuplicateCharge } == true)
        assertFalse(flags.containsKey("1")) // only the second (later) charge is flagged
    }

    @Test
    fun `does not flag same merchant different amount as duplicate`() {
        val transactions = listOf(
            txn("1", "WeWork", 550.0, minutesOffset = 0),
            txn("2", "WeWork", 600.0, minutesOffset = 4)
        )
        val flags = engine.evaluate(transactions)
        assertFalse(flags.containsKey("2"))
    }

    @Test
    fun `flags high velocity burst on same card`() {
        val transactions = (0 until 5).map { i ->
            txn("burst-$i", "Merchant $i", 20.0, minutesOffset = i * 8L, card = "9999")
        }
        val flags = engine.evaluate(transactions)
        assertTrue(flags["burst-0"]?.any { it is RiskFlag.HighVelocity } == true)
    }

    @Test
    fun `does not flag normal-paced transactions as high velocity`() {
        val transactions = (0 until 3).map { i ->
            txn("normal-$i", "Merchant $i", 20.0, minutesOffset = i * 60L, card = "9999")
        }
        val flags = engine.evaluate(transactions)
        assertTrue(flags.isEmpty())
    }

    @Test
    fun `flags first large charge to a new merchant`() {
        val transactions = listOf(txn("1", "Apex Industrial Equipment", 3400.0))
        val flags = engine.evaluate(transactions)
        assertTrue(flags["1"]?.any { it is RiskFlag.NewMerchantHighAmount } == true)
    }

    @Test
    fun `does not flag second charge to an already-seen merchant`() {
        val transactions = listOf(
            txn("1", "Apex Industrial Equipment", 3400.0, minutesOffset = 0),
            txn("2", "Apex Industrial Equipment", 3400.0, minutesOffset = 60 * 24 * 10L)
        )
        val flags = engine.evaluate(transactions)
        assertFalse(flags["2"]?.any { it is RiskFlag.NewMerchantHighAmount } == true)
    }
}
