package com.example.myfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val balance: Double,
    val type: String,
    val colorHex: String = "#6C63FF",
    val isActive: Boolean = true
)