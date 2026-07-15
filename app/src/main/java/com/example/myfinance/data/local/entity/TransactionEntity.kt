package com.example.myfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["date"]),
        Index(value = ["type"]),
        Index(value = ["accountId"]),
        Index(value = ["categoryId"]),
        Index(value = ["date", "type"])
    ]
)

data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val note: String = "",
    val type: String,
    val categoryId: Long,
    val accountId: Long,
    val toAccountId: Long? = null,
    val date: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val recurringInterval: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)