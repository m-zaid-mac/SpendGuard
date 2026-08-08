package com.mohammadzaid.spendguard.data.model

/**
 * Spend categories. Kept as a closed set so the categorization engine
 * and dashboard aggregation stay in sync.
 */
enum class Category(val displayName: String) {
    SOFTWARE("Software & SaaS"),
    TRAVEL("Travel"),
    MEALS("Meals & entertainment"),
    OFFICE("Office supplies"),
    ADVERTISING("Advertising"),
    PROFESSIONAL_SERVICES("Professional services"),
    UNCATEGORIZED("Uncategorized")
}
