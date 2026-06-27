package com.example.myfinance.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val accounts: List<AccountEntity> = emptyList(),
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
                repository.getTotalByTypeAndDateRange("EXPENSE", startOfMonth, endOfMonth)
            ) { accounts, totalBalance, recentTransactions, totalIncome, totalExpense ->
                HomeUiState(
                    totalBalance = totalBalance ?: 0.0,
                    accounts = accounts,
                    totalIncome = totalIncome ?: 0.0,
                    totalExpense = totalExpense ?: 0.0,
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