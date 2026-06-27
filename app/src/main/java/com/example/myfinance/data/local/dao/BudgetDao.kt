package com.example.myfinance.data.local.dao

import androidx.room.*
import com.example.myfinance.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("SELECT * FROM budgets")
    fun getAll(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId")
    suspend fun getByCategoryId(categoryId: Long): BudgetEntity?
}