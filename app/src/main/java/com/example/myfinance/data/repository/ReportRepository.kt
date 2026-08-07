package com.example.myfinance.data.repository

import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val financeRepository: FinanceRepository
) {

    fun getTransactionsByDateRange(
        start: Long,
        end: Long
    ) =
        financeRepository.getTransactionsByDateRange(start, end)

    fun getTotalByTypeAndDateRange(
        type: String,
        start: Long,
        end: Long
    ) =
        financeRepository.getTotalByTypeAndDateRange(
            type,
            start,
            end
        )

    fun getAllCategories() =
        financeRepository.getAllCategories()

    fun getAllAccounts() =
        financeRepository.getAllAccounts()
}