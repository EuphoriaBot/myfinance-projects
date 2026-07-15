package com.example.myfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "accounts",
    indices = [Index(value = ["isActive"])]
)

data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val balance: Double,
    val type: String,
    val colorHex: String = "#6C63FF",
    val isActive: Boolean = true
)