package com.example.myfinance.data.repository

import com.example.myfinance.data.local.entity.TransactionEntity
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val financeRepository: FinanceRepository
) {

    fun getAllTransactions() =
        financeRepository.getAllTransactions()

    fun getRecentTransactions(limit: Int = 5) =
        financeRepository.getRecentTransactions(limit)

    fun getTransactionsByDateRange(
        start: Long,
        end: Long
    ) =
        financeRepository.getTransactionsByDateRange(start, end)

    fun getAllAccounts() =
        financeRepository.getAllAccounts()

    fun getAllCategories() =
        financeRepository.getAllCategories()

    suspend fun getAccountById(id: Long) =
        financeRepository.getAccountById(id)

    suspend fun addTransaction(
        transaction: TransactionEntity
    ) =
        financeRepository.addTransaction(transaction)

    suspend fun addTransfer(
        transaction: TransactionEntity
    ) =
        financeRepository.addTransfer(transaction)

    suspend fun updateTransaction(
        oldTransaction: TransactionEntity,
        newTransaction: TransactionEntity
    ) =
        financeRepository.updateTransaction(
            oldTransaction,
            newTransaction
        )

    suspend fun updateTransfer(
        oldTransaction: TransactionEntity,
        newTransaction: TransactionEntity
    ) =
        financeRepository.updateTransfer(
            oldTransaction,
            newTransaction
        )

    suspend fun deleteTransaction(
        transaction: TransactionEntity
    ) =
        financeRepository.deleteTransaction(transaction)
}