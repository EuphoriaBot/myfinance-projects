package com.example.myfinance.domain.model

enum class AccountType {
    CASH,
    BANK,
    E_WALLET
}

data class Account(
    val id: Long = 0,
    val name: String,
    val balance: Double,
    val type: AccountType,
    val colorHex: String = "#6C63FF",
    val isActive: Boolean = true
)