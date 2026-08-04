package com.example.myfinance.data.repository

import com.example.myfinance.data.local.dao.*
import com.example.myfinance.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.example.myfinance.data.local.database.AppDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.flow.first

class FinanceRepository @Inject constructor(
    private val database: AppDatabase,
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

    fun getAllCategories(): Flow<List<CategoryEntity>> =
        categoryDao.getAll()

    suspend fun insertCategory(category: CategoryEntity): Long =
        categoryDao.insert(category)

    suspend fun updateCategory(category: CategoryEntity) =
        categoryDao.update(category)

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

    fun getTransactionsByCategoryAndDateRange(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByCategoryAndDateRange(
            categoryId,
            startDate,
            endDate
        )

    suspend fun getSpentAmountByCategory(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Double {
        return transactionDao
            .getTransactionsByCategoryAndDateRange(
                categoryId,
                startDate,
                endDate
            )
            .first()
            .sumOf { it.amount }
    }

    fun getAllBudgets(): Flow<List<BudgetEntity>> =
        budgetDao.getAll()

    suspend fun insertBudget(budget: BudgetEntity): Long =
        budgetDao.insert(budget)

    suspend fun updateBudget(budget: BudgetEntity) =
        budgetDao.update(budget)

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
        database.withTransaction {
            transactionDao.deleteAll()
            budgetDao.deleteAll()
            savingGoalDao.deleteAll()
            accountDao.deleteAll()
            categoryDao.deleteAll()
        }
    }

    suspend fun getAccountById(id: Long): AccountEntity? =
        accountDao.getById(id)

    suspend fun transferMoney(
        fromAccount: AccountEntity,
        toAccount: AccountEntity,
        amount: Double
    ) {
        database.withTransaction {
            accountDao.update(
                fromAccount.copy(
                    balance = fromAccount.balance - amount
                )
            )
            accountDao.update(
                toAccount.copy(
                    balance = toAccount.balance + amount
                )
            )
        }
    }

    suspend fun updateTransfer(
        oldTransaction: TransactionEntity,
        newTransaction: TransactionEntity
    ) {
        database.withTransaction {
            val oldFrom = accountDao.getById(oldTransaction.accountId)!!
            val oldTo = accountDao.getById(oldTransaction.toAccountId!!)!!

            accountDao.update(
                oldFrom.copy(
                    balance = oldFrom.balance + oldTransaction.amount
                )
            )

            accountDao.update(
                oldTo.copy(
                    balance = oldTo.balance - oldTransaction.amount
                )
            )

            transactionDao.update(newTransaction)

            val newFrom = accountDao.getById(newTransaction.accountId)!!
            val newTo = accountDao.getById(newTransaction.toAccountId!!)!!

            accountDao.update(
                newFrom.copy(
                    balance = newFrom.balance - newTransaction.amount
                )
            )

            accountDao.update(
                newTo.copy(
                    balance = newTo.balance + newTransaction.amount
                )
            )
        }
    }

    suspend fun softDeleteAccount(account: AccountEntity) {
        accountDao.update(
            account.copy(isActive = false)
        )
    }
}