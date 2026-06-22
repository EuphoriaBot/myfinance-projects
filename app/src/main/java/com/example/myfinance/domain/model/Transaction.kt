package com.example.myfinance.domain.model

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

enum class RecurringInterval {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val note: String = "",
    val type: TransactionType,
    val categoryId: Long,
    val accountId: Long,
    val toAccountId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val recurringInterval: RecurringInterval? = null, 
    val createdAt: Long = System.currentTimeMillis()
)