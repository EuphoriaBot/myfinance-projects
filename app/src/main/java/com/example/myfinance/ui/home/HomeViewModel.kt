package com.example.myfinance.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val startOfMonth = calendar.timeInMillis

                combine(
                    repository.getAllAccounts(),
                    repository.getTotalBalance(),
                    repository.getRecentTransactions(5),
                    repository.getTransactionsByDateRange(startOfMonth, Long.MAX_VALUE)
                ) { accounts, totalBalance, recentTransactions, monthTransactions ->
                    Quadruple(accounts, totalBalance, recentTransactions, monthTransactions)
                }.combine(repository.getAllCategories()) { quad, categories ->
                    Quintuple(quad.a, quad.b, quad.c, quad.d, categories)
                }.combine(repository.getAllBudgets()) { quint, budgets ->
                    val accounts = quint.a
                    val totalBalance = quint.b
                    val recentTransactions = quint.c
                    val monthTransactions = quint.d
                    val categories = quint.e

                    val totalIncome = monthTransactions
                        .filter { it.type == TransactionType.INCOME.name }
                        .sumOf { it.amount }

                    val totalExpense = monthTransactions
                        .filter { it.type == TransactionType.EXPENSE.name }
                        .sumOf { it.amount }

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
                            val spent = monthTransactions
                                .filter { it.categoryId == budget.categoryId && it.type == TransactionType.EXPENSE.name }
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
            } catch (e: Exception) {
                Timber.e(e, "Error loading home data")
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
private data class Quintuple<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)