package com.example.myfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.ui.home.HomeScreen
import com.example.myfinance.ui.theme.DarkBackground
import com.example.myfinance.ui.theme.MyFinanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AppApplication
        val repository = FinanceRepository(
            accountDao = app.database.accountDao(),
            categoryDao = app.database.categoryDao(),
            transactionDao = app.database.transactionDao(),
            budgetDao = app.database.budgetDao(),
            savingGoalDao = app.database.savingGoalDao()
        )

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