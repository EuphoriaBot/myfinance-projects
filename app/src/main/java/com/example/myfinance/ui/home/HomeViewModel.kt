package com.example.myfinance.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.BudgetEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class BudgetWithSpending(
    val categoryName: String,
    val spent: Double,
    val limit: Double
)

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val accounts: List<AccountEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val budgets: List<BudgetWithSpending> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val startOfMonth = calendar.timeInMillis
            val endOfMonth = System.currentTimeMillis()

            combine(
                repository.getAllAccounts(),
                repository.getTotalBalance(),
                repository.getRecentTransactions(5),
                repository.getTotalByTypeAndDateRange("INCOME", startOfMonth, endOfMonth),
                repository.getTotalByTypeAndDateRange("EXPENSE", startOfMonth, endOfMonth)
            ) { accounts, totalBalance, recentTransactions, totalIncome, totalExpense ->
                Quintuple(accounts, totalBalance, recentTransactions, totalIncome, totalExpense)
            }.combine(repository.getAllCategories()) { quint, categories ->
                Sextuple(quint.a, quint.b, quint.c, quint.d, quint.e, categories)
            }.combine(repository.getAllBudgets()) { sext, budgets ->
                val accounts = sext.a
                val totalBalance = sext.b ?: 0.0
                val recentTransactions = sext.c
                val totalIncome = sext.d ?: 0.0
                val totalExpense = sext.e ?: 0.0
                val categories = sext.f

                HomeUiState(
                    totalBalance = totalBalance,
                    accounts = accounts,
                    categories = categories,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    recentTransactions = recentTransactions,
                    budgets = budgets.map { budget ->
                        val categoryName = categories
                            .find { it.id == budget.categoryId }?.name ?: "Lainnya"
                        val spent = recentTransactions
                            .filter { it.categoryId == budget.categoryId && it.type == "EXPENSE" }
                            .sumOf { it.amount }
                        BudgetWithSpending(
                            categoryName = categoryName,
                            spent = spent,
                            limit = budget.limitAmount
                        )
                    },
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun insertAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.insertAccount(account)
        }
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

private data class Quintuple<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
private data class Sextuple<A, B, C, D, E, F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F)