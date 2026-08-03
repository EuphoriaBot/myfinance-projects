package com.example.myfinance.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: FinanceRepository
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
            (type == "EXPENSE" || type == "TRANSFER") &&
            amountValue > account.balance
        ) {
            return "Saldo akun tidak mencukupi"
        }

        if (type == "TRANSFER") {

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

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
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

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            repository.updateAccount(account)
        }
    }

    fun transferMoney(
        fromAccount: AccountEntity,
        toAccount: AccountEntity,
        amount: Double
    ) {
        viewModelScope.launch {
            repository.transferMoney(
                fromAccount = fromAccount,
                toAccount = toAccount,
                amount = amount
            )
        }
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