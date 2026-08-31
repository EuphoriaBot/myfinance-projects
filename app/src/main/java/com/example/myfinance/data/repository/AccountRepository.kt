package com.example.myfinance.data.repository

import com.example.myfinance.data.local.entity.AccountEntity
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val financeRepository: FinanceRepository
) {

    fun getAllAccounts() =
        financeRepository.getAllAccounts()

    fun getTotalBalance() =
        financeRepository.getTotalBalance()

    suspend fun insertAccount(
        account: AccountEntity
    ) =
        financeRepository.insertAccount(account)

    suspend fun updateAccount(
        account: AccountEntity
    ) =
        financeRepository.updateAccount(account)

    suspend fun softDeleteAccount(
        account: AccountEntity
    ) =
        financeRepository.softDeleteAccount(account)
}