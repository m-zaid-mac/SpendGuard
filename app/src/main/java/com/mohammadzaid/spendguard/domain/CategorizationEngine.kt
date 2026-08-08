package com.mohammadzaid.spendguard.domain

import com.mohammadzaid.spendguard.data.model.Category

/**
 * Merchant-name-based spend categorization.
 *
 * This is deliberately a rule engine, not an ML model. Real spend categorization
 * (like Ramp's) usually starts here — merchant category codes + keyword rules
 * cover the overwhelming majority of transactions cheaply, and you only reach
 * for a model for the long tail of ambiguous merchants. Framing this honestly
 * in an interview beats overclaiming an ML pipeline that doesn't exist yet.
 *
 * Pure Kotlin: no Android or Room imports, so it's testable with plain JUnit
 * and could be lifted into a backend service unchanged.
 */
class CategorizationEngine {

    private val rules: List<Pair<Regex, Category>> = listOf(
        Regex("aws|amazon web services|google cloud|azure|heroku|vercel|digitalocean", RegexOption.IGNORE_CASE) to Category.SOFTWARE,
        Regex("figma|notion|slack|zoom|github|linear|asana|jira", RegexOption.IGNORE_CASE) to Category.SOFTWARE,
        Regex("delta|united|american air|marriott|hilton|airbnb|uber|lyft", RegexOption.IGNORE_CASE) to Category.TRAVEL,
        Regex("sweetgreen|doordash|grubhub|starbucks|chipotle|restaurant|cafe", RegexOption.IGNORE_CASE) to Category.MEALS,
        Regex("staples|office depot|amazon(?! web)", RegexOption.IGNORE_CASE) to Category.OFFICE,
        Regex("google ads|linkedin ads|facebook ads|meta ads|twitter ads", RegexOption.IGNORE_CASE) to Category.ADVERTISING,
        Regex("deloitte|mckinsey|pwc|consulting|law firm|llp", RegexOption.IGNORE_CASE) to Category.PROFESSIONAL_SERVICES,
        Regex("wework|regus|office space", RegexOption.IGNORE_CASE) to Category.OFFICE
    )

    /**
     * Returns the best-guess category for a merchant name.
     * Falls back to UNCATEGORIZED rather than guessing wrong — a false
     * "Uncategorized" is a cheap manual fix; a confidently wrong category
     * pollutes the spend dashboard and is harder to notice.
     */
    fun categorize(merchant: String): Category {
        return rules.firstOrNull { (pattern, _) -> pattern.containsMatchIn(merchant) }
            ?.second
            ?: Category.UNCATEGORIZED
    }
}
