package com.mohammadzaid.spendguard.domain

import com.mohammadzaid.spendguard.data.model.Category
import org.junit.Assert.assertEquals
import org.junit.Test

class CategorizationEngineTest {

    private val engine = CategorizationEngine()

    @Test
    fun `AWS is categorized as software`() {
        assertEquals(Category.SOFTWARE, engine.categorize("Amazon Web Services"))
    }

    @Test
    fun `plain Amazon is categorized as office, not software`() {
        // Regression test: Amazon Web Services and generic Amazon purchases
        // (office supplies) must not collide on the "amazon" substring.
        assertEquals(Category.OFFICE, engine.categorize("Amazon.com Purchase"))
    }

    @Test
    fun `Delta Air Lines is categorized as travel`() {
        assertEquals(Category.TRAVEL, engine.categorize("Delta Air Lines"))
    }

    @Test
    fun `unknown merchant falls back to uncategorized rather than guessing`() {
        assertEquals(Category.UNCATEGORIZED, engine.categorize("Zzyzx Widgets LLC"))
    }

    @Test
    fun `matching is case insensitive`() {
        assertEquals(Category.SOFTWARE, engine.categorize("FIGMA"))
    }
}
