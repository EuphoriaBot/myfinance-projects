package com.example.myfinance.domain.model

enum class CategoryType {
    INCOME,
    EXPENSE
}

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val type: CategoryType,
    val colorHex: String = "#6C63FF"
)