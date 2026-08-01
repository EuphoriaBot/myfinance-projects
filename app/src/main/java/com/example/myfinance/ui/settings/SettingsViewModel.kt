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
import com.example.myfinance.utils.BackupManager
import kotlinx.coroutines.Dispatchers
import android.net.Uri
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class BackupState {
    object Idle : BackupState()
    object Loading : BackupState()
    object Success : BackupState()
    object Failed : BackupState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val preferencesManager: PreferencesManager,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState: StateFlow<BackupState> = _backupState.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val budgets: StateFlow<List<BudgetEntity>> = repository.getAllBudgets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun backupDatabase() {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading

            val success = withContext(Dispatchers.IO) {
                backupManager.createBackup()
            }

            _backupState.value = if (success) {
                BackupState.Success
            } else {
                BackupState.Failed
            }
        }
    }

    fun resetBackupState() {
        _backupState.value = BackupState.Idle
    }

    fun insertBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.insertBudget(budget)
        }
    }

    fun updateBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.updateBudget(budget)
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
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

    suspend fun restoreDatabase(uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            backupManager.restoreBackup(uri)
        }
    }

    suspend fun resetAllData() {
        repository.resetAllData()
        preferencesManager.clearAll()
    }
}