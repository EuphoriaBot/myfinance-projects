package com.example.myfinance.data.local.dao

import androidx.room.*
import com.example.myfinance.data.local.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: SavingGoalEntity): Long

    @Update
    suspend fun update(goal: SavingGoalEntity)

    @Delete
    suspend fun delete(goal: SavingGoalEntity)

    @Query("SELECT * FROM saving_goals ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SavingGoalEntity>>

    @Query("SELECT * FROM saving_goals WHERE isCompleted = 0")
    fun getActive(): Flow<List<SavingGoalEntity>>

    @Query("SELECT * FROM saving_goals WHERE id = :id")
    suspend fun getById(id: Long): SavingGoalEntity?
}