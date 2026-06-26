package com.example.myfinance.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingGoalDao(): SavingGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myfinance_database"
                )
                    .addCallback(PrepopulateCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private class PrepopulateCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            db.execSQL("""
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
            """.trimIndent())
        }
    }
}