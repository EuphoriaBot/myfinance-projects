package com.example.myfinance.domain.model

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    YEARLY
}

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val limitAmount: Double,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val createdAt: Long = System.currentTimeMillis()
)