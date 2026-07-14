package com.example.myfinance.data.repository

import com.example.myfinance.data.local.dao.*
import com.example.myfinance.data.local.entity.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val savingGoalDao: SavingGoalDao
) {
    fun getAllAccounts(): Flow<List<AccountEntity>> =
        accountDao.getAllActive()

    fun getTotalBalance(): Flow<Double> =
        accountDao.getTotalBalance()

    suspend fun insertAccount(account: AccountEntity): Long =
        accountDao.insert(account)

    suspend fun updateAccount(account: AccountEntity) =
        accountDao.update(account)

    suspend fun deleteAccount(account: AccountEntity) =
        accountDao.delete(account)

    fun getAllCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAll()

    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>> =
        categoryDao.getByType(type)

    suspend fun insertCategory(category: CategoryEntity): Long =
        categoryDao.insert(category)

    suspend fun deleteCategory(category: CategoryEntity) =
        categoryDao.delete(category)

    fun getAllTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getAll()

    fun getRecentTransactions(limit: Int = 5): Flow<List<TransactionEntity>> =
        transactionDao.getRecent(limit)

    fun getTransactionsByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>> =
        transactionDao.getByDateRange(startDate, endDate)

    fun getTotalByTypeAndDateRange(
        type: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?> =
        transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insert(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.update(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.delete(transaction)

    fun getAllBudgets(): Flow<List<BudgetEntity>> =
        budgetDao.getAll()

    suspend fun insertBudget(budget: BudgetEntity): Long =
        budgetDao.insert(budget)

    suspend fun deleteBudget(budget: BudgetEntity) =
        budgetDao.delete(budget)

    fun getAllSavingGoals(): Flow<List<SavingGoalEntity>> =
        savingGoalDao.getAll()

    suspend fun insertSavingGoal(goal: SavingGoalEntity): Long =
        savingGoalDao.insert(goal)

    suspend fun updateSavingGoal(goal: SavingGoalEntity) =
        savingGoalDao.update(goal)

    suspend fun deleteSavingGoal(goal: SavingGoalEntity) =
        savingGoalDao.delete(goal)

    suspend fun resetAllData() {
        val accounts = getAllAccounts().first()
        accounts.forEach { deleteAccount(it) }

        val transactions = getAllTransactions().first()
        transactions.forEach { deleteTransaction(it) }

        val budgets = getAllBudgets().first()
        budgets.forEach { deleteBudget(it) }

        val goals = getAllSavingGoals().first()
        goals.forEach { deleteSavingGoal(it) }
    }
}