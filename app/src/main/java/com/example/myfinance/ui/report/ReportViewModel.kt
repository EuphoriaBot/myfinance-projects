package com.example.myfinance.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.Calendar
import javax.inject.Inject

data class ReportUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val transactions: List<TransactionEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val accounts: List<AccountEntity> = emptyList()
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    private val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    private val startOfMonth = calendar.timeInMillis

    val uiState: StateFlow<ReportUiState> = combine(
        repository.getTotalByTypeAndDateRange("INCOME", startOfMonth, Long.MAX_VALUE),
        repository.getTotalByTypeAndDateRange("EXPENSE", startOfMonth, Long.MAX_VALUE),
        repository.getTransactionsByDateRange(startOfMonth, Long.MAX_VALUE),
        repository.getAllCategories(),
        repository.getAllAccounts()
    ) { income, expense, transactions, categories, accounts ->
        ReportUiState(
            totalIncome = income ?: 0.0,
            totalExpense = expense ?: 0.0,
            transactions = transactions,
            categories = categories,
            accounts = accounts
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportUiState()
    )
}