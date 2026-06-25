package com.example.myfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saving_goals")
data class SavingGoalEntity(
    @PrimaryKey(autoGenerate = true)
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