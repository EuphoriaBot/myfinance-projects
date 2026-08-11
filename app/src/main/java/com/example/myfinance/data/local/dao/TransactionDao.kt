package com.example.myfinance.data.local.dao

import androidx.room.*
import com.example.myfinance.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int = 5): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions 
        WHERE date >= :startDate AND date <= :endDate 
        ORDER BY date DESC
    """)
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE type = :type 
        AND date >= :startDate AND date <= :endDate
    """)
    fun getTotalByTypeAndDateRange(
        type: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>

    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE categoryId = :categoryId 
        AND date >= :startDate AND date <= :endDate
    """)
    fun getTotalByCategoryAndDateRange(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getByAccount(accountId: Long): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query("""
        SELECT *
        FROM transactions
        WHERE categoryId = :categoryId
        AND type = 'EXPENSE'
        AND date BETWEEN :startDate AND :endDate
        """)
    fun getTransactionsByCategoryAndDateRange(
        categoryId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>>

    @Query("""
    SELECT EXISTS(
        SELECT 1
        FROM transactions
        WHERE accountId = :accountId
        AND type = :type
        AND amount = :amount
        AND isRecurring = 0
        AND date >= :startDate
        AND date <= :endDate
    )
""")
    suspend fun hasGeneratedRecurringTransaction(
        accountId: Long,
        type: String,
        amount: Double,
        startDate: Long,
        endDate: Long
    ): Boolean

    @Query("""
    SELECT EXISTS(
        SELECT 1
        FROM transactions
        WHERE recurringSourceId = :sourceId
        AND date >= :startDate
        AND date <= :endDate
    )
""")
    suspend fun hasRecurringTransactionInPeriod(
        sourceId: Long,
        startDate: Long,
        endDate: Long
    ): Boolean
}