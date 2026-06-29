package com.example.myfinance.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val accounts: List<AccountEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
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
                repository.getTotalByTypeAndDateRange("EXPENSE", startOfMonth, endOfMonth),
                repository.getAllCategories()
            ) { values ->
                val accounts = values[0] as List<AccountEntity>
                val totalBalance = (values[1] as Double?) ?: 0.0
                val recentTransactions = values[2] as List<TransactionEntity>
                val totalIncome = (values[3] as Double?) ?: 0.0
                val totalExpense = (values[4] as Double?) ?: 0.0
                val categories = values[5] as List<CategoryEntity>

                HomeUiState(
                    totalBalance = totalBalance,
                    accounts = accounts,
                    categories = categories,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    recentTransactions = recentTransactions,
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