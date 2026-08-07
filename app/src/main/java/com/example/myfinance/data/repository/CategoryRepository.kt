package com.example.myfinance.data.repository

import com.example.myfinance.data.local.entity.CategoryEntity
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val financeRepository: FinanceRepository
) {

    fun getAllCategories() =
        financeRepository.getAllCategories()

    suspend fun insertCategory(
        category: CategoryEntity
    ) =
        financeRepository.insertCategory(category)

    suspend fun updateCategory(
        category: CategoryEntity
    ) =
        financeRepository.updateCategory(category)

    suspend fun deleteCategory(
        category: CategoryEntity
    ) =
        financeRepository.deleteCategory(category)
}