package com.example.myfinance.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.BudgetEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.utils.PreferencesManager
import com.example.myfinance.utils.exportTransactionsToCsv
import com.example.myfinance.utils.shareFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.insertBudget(budget)
        }
    }

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            val transactions = repository.getAllTransactions().first()
            val accounts = repository.getAllAccounts().first()
            val categories = repository.getAllCategories().first()
            val uri = exportTransactionsToCsv(context, transactions, accounts, categories)
            if (uri != null) shareFile(context, uri)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            preferencesManager.clearAll()
        }
    }
}