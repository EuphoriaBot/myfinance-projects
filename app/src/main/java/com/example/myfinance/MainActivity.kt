package com.example.myfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myfinance.data.local.database.AppDatabase
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.ui.home.HomeScreen
import com.example.myfinance.ui.theme.DarkBackground
import com.example.myfinance.ui.theme.MyFinanceTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getInstance(this) }

    private val repository by lazy {
        FinanceRepository(
            accountDao = database.accountDao(),
            categoryDao = database.categoryDao(),
            transactionDao = database.transactionDao(),
            budgetDao = database.budgetDao(),
            savingGoalDao = database.savingGoalDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    HomeScreen(repository = repository)
                }
            }
        }
    }
}