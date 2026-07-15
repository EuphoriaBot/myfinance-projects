package com.example.myfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "budgets",
    indices = [Index(value = ["categoryId"])]
)

data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val limitAmount: Double,
    val period: String = "MONTHLY",
    val createdAt: Long = System.currentTimeMillis()
)