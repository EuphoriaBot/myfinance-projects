package com.example.myfinance.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myfinance.data.local.dao.*
import com.example.myfinance.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        SavingGoalEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingGoalDao(): SavingGoalDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_transactions_date ON transactions(date)"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_transactions_type ON transactions(type)"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_transactions_accountId ON transactions(accountId)"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_transactions_categoryId ON transactions(categoryId)"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_transactions_date_type ON transactions(date, type)"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_accounts_isActive ON accounts(isActive)"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_budgets_categoryId ON budgets(categoryId)"
                )

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_saving_goals_isCompleted ON saving_goals(isCompleted)"
                )
            }
        }

        val PREPOPULATE_CALLBACK = object : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                CoroutineScope(Dispatchers.IO).launch {
                    db.execSQL(
                        """
                INSERT INTO categories (name, icon, type, colorHex) VALUES
                ('Makan & Minum', 'restaurant', 'EXPENSE', '#FF5C5C'),
                ('Transportasi', 'directions_car', 'EXPENSE', '#F5A623'),
                ('Belanja', 'shopping_bag', 'EXPENSE', '#6C63FF'),
                ('Hiburan', 'movie', 'EXPENSE', '#00C896'),
                ('Kesehatan', 'medical_services', 'EXPENSE', '#FF5C5C'),
                ('Tagihan', 'receipt', 'EXPENSE', '#F5A623'),
                ('Pendidikan', 'school', 'EXPENSE', '#6C63FF'),
                ('Lainnya', 'more_horiz', 'EXPENSE', '#7B7F9E'),
                ('Gaji', 'work', 'INCOME', '#00C896'),
                ('Freelance', 'laptop', 'INCOME', '#6C63FF'),
                ('Investasi', 'trending_up', 'INCOME', '#00C896'),
                ('Hadiah', 'card_giftcard', 'INCOME', '#F5A623'),
                ('Lainnya', 'more_horiz', 'INCOME', '#7B7F9E')
                """.trimIndent()
                    )
                }
            }
        }
    }
}

