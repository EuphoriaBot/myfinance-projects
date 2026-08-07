package com.example.myfinance.data.repository

import com.example.myfinance.data.local.entity.SavingGoalEntity
import javax.inject.Inject

class SavingGoalRepository @Inject constructor(
    private val financeRepository: FinanceRepository
) {

    fun getAllSavingGoals() =
        financeRepository.getAllSavingGoals()

    suspend fun insertSavingGoal(
        goal: SavingGoalEntity
    ) =
        financeRepository.insertSavingGoal(goal)

    suspend fun updateSavingGoal(
        goal: SavingGoalEntity
    ) =
        financeRepository.updateSavingGoal(goal)

    suspend fun deleteSavingGoal(
        goal: SavingGoalEntity
    ) =
        financeRepository.deleteSavingGoal(goal)
}