package com.example.myfinance.data.local.dao

import androidx.room.*
import com.example.myfinance.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE isActive = 1")
    fun getAllActive(): Flow<List<AccountEntity>>

    @Query("SELECT SUM(balance) FROM accounts WHERE isActive = 1")
    fun getTotalBalance(): Flow<Double>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}