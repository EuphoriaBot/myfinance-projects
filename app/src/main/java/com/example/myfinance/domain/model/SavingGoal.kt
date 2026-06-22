package com.example.myfinance.domain.model

data class SavingGoal(
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: Long? = null,
    val colorHex: String = "#6C63FF",
    val icon: String = "target",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)