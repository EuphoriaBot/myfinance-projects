package com.example.myfinance.data.repository

import com.example.myfinance.data.local.entity.BudgetEntity
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val financeRepository: FinanceRepository
) {

    fun getAllBudgets() =
        financeRepository.getAllBudgets()

    suspend fun insertBudget(
        budget: BudgetEntity
    ) =
        financeRepository.insertBudget(budget)

    suspend fun updateBudget(
        budget: BudgetEntity
    ) =
        financeRepository.updateBudget(budget)

    suspend fun deleteBudget(
        budget: BudgetEntity
    ) =
        financeRepository.deleteBudget(budget)

    suspend fun getSpentAmountByCategory(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ) =
        financeRepository.getSpentAmountByCategory(
            categoryId,
            startDate,
            endDate
        )
}