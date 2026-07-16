package com.example.myfinance.di

import android.content.Context
import com.example.myfinance.data.local.dao.AccountDao
import com.example.myfinance.data.local.dao.BudgetDao
import com.example.myfinance.data.local.dao.CategoryDao
import com.example.myfinance.data.local.dao.SavingGoalDao
import com.example.myfinance.data.local.dao.TransactionDao
import com.example.myfinance.data.local.database.AppDatabase
import com.example.myfinance.utils.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideAccountDao(
        database: AppDatabase
    ): AccountDao = database.accountDao()

    @Provides
    fun provideCategoryDao(
        database: AppDatabase
    ): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(
        database: AppDatabase
    ): TransactionDao = database.transactionDao()

    @Provides
    fun provideBudgetDao(
        database: AppDatabase
    ): BudgetDao = database.budgetDao()

    @Provides
    fun provideSavingGoalDao(
        database: AppDatabase
    ): SavingGoalDao = database.savingGoalDao()

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }
}