package com.example.myfinance.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.data.repository.TransactionRepository
import com.example.myfinance.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine


@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    val transactions: StateFlow<List<TransactionEntity>> =
        repository.getAllTransactions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val accounts: StateFlow<List<AccountEntity>> =
        repository.getAllAccounts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val categories: StateFlow<List<CategoryEntity>> =
        repository.getAllCategories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val uiState: StateFlow<TransactionUiState> =
        combine(
            transactions,
            accounts,
            categories
        ) { transactions, accounts, categories -> TransactionUiState(
                transactions = transactions,
                accounts = accounts,
                categories = categories
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionUiState()
        )

    fun validateTransaction(
        amount: String,
        account: AccountEntity?,
        category: CategoryEntity?,
        type: String,
        toAccount: AccountEntity?
    ): String? {
        val amountValue = amount.toDoubleOrNull()

        if (amount.isBlank())
            return "Jumlah tidak boleh kosong"

        if (amountValue == null || amountValue <= 0)
            return "Jumlah harus lebih dari 0"

        if (account == null)
            return "Pilih akun"

        if (
            (type == TransactionType.EXPENSE.name || type == TransactionType.TRANSFER.name) &&
            amountValue > account.balance
        ) {
            return "Saldo akun tidak mencukupi"
        }

        if (type == TransactionType.TRANSFER.name) {

            if (toAccount == null)
                return "Pilih akun tujuan"

            if (account.id == toAccount.id)
                return "Akun asal dan tujuan tidak boleh sama"

        } else {

            if (category == null)
                return "Pilih kategori"

        }
        return null
    }

    fun validateEditTransfer(
        amount: String,
        oldTransaction: TransactionEntity,
        fromAccount: AccountEntity?,
        toAccount: AccountEntity?
    ): String? {

        val amountValue = amount.toDoubleOrNull()

        if (amount.isBlank())
            return "Jumlah tidak boleh kosong"

        if (amountValue == null || amountValue <= 0)
            return "Jumlah harus lebih dari 0"

        if (fromAccount == null)
            return "Pilih akun asal"

        if (toAccount == null)
            return "Pilih akun tujuan"

        if (fromAccount.id == toAccount.id)
            return "Akun asal dan tujuan tidak boleh sama"

        var availableBalance = fromAccount.balance

        when (fromAccount.id) {

            oldTransaction.accountId -> {
                availableBalance += oldTransaction.amount
            }

            oldTransaction.toAccountId -> {
                availableBalance -= oldTransaction.amount
            }
        }

        if (amountValue > availableBalance) {
            return "Saldo akun tidak mencukupi"
        }

        return null
    }

    fun addTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }

    fun addTransfer(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.addTransfer(transaction)
        }
    }

    fun updateTransaction(
        oldTransaction: TransactionEntity,
        newTransaction: TransactionEntity
    ) {
        viewModelScope.launch {
            repository.updateTransaction(
                oldTransaction,
                newTransaction
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    suspend fun getAccountById(id: Long): AccountEntity? {
        return repository.getAccountById(id)
    }

    fun updateTransfer(
        oldTransaction: TransactionEntity,
        newTransaction: TransactionEntity
    ) {
        viewModelScope.launch {
            repository.updateTransfer(
                oldTransaction,
                newTransaction
            )
        }
    }
}