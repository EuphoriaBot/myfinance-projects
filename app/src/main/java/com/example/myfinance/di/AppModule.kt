package com.example.myfinance.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.myfinance.data.local.dao.AccountDao
import com.example.myfinance.data.local.dao.BudgetDao
import com.example.myfinance.data.local.dao.CategoryDao
import com.example.myfinance.data.local.dao.SavingGoalDao
import com.example.myfinance.data.local.dao.TransactionDao
import com.example.myfinance.data.local.database.AppDatabase
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.utils.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.sqlcipher.database.SupportFactory
import com.example.myfinance.utils.DatabasePassphraseManager
import com.example.myfinance.utils.KeystoreManager
import com.example.myfinance.utils.PinManager
import com.example.myfinance.data.repository.TransactionRepository
import com.example.myfinance.data.repository.AccountRepository

private val Context.dataStore by preferencesDataStore(
    name = "myfinance_prefs"
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        databasePassphraseManager: DatabasePassphraseManager
    ): AppDatabase {

        val passphrase =
            databasePassphraseManager
                .getOrCreateDatabasePassphrase(context)

        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "myfinance_database"
        )
            .openHelperFactory(factory)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .addCallback(AppDatabase.PREPOPULATE_CALLBACK)
            .build()
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
    fun provideFinanceRepository(
        database: AppDatabase,
        accountDao: AccountDao,
        categoryDao: CategoryDao,
        transactionDao: TransactionDao,
        budgetDao: BudgetDao,
        savingGoalDao: SavingGoalDao
    ): FinanceRepository {
        return FinanceRepository(
            database,
            accountDao,
            categoryDao,
            transactionDao,
            budgetDao,
            savingGoalDao
        )
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        financeRepository: FinanceRepository
    ): TransactionRepository {
        return TransactionRepository(financeRepository)
    }

    @Provides
    @Singleton
    fun provideAccountRepository(
        financeRepository: FinanceRepository
    ): AccountRepository {
        return AccountRepository(financeRepository)
    }

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun providePreferencesManager(
        dataStore: DataStore<Preferences>
    ): PreferencesManager {
        return PreferencesManager(dataStore)
    }

    @Provides
    @Singleton
    fun providePinManager(
        keystoreManager: KeystoreManager
    ): PinManager {
        return PinManager(keystoreManager)
    }
}