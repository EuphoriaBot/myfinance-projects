package com.example.myfinance.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.domain.model.TransactionType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: FinanceRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val allTransactions = repository.getAllTransactions().first()
            val recurringTransactions = allTransactions.filter { it.isRecurring }

            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L
            val oneWeekMs = 7 * oneDayMs
            val oneMonthMs = 30 * oneDayMs

            recurringTransactions.forEach { transaction ->
                val interval = when (transaction.recurringInterval) {
                    "DAILY" -> oneDayMs
                    "WEEKLY" -> oneWeekMs
                    "MONTHLY" -> oneMonthMs
                    "YEARLY" -> 365 * oneDayMs
                    else -> return@forEach
                }

                val timeSinceLast = now - transaction.date
                if (timeSinceLast >= interval) {
                    repository.insertTransaction(
                        TransactionEntity(
                            amount = transaction.amount,
                            note = transaction.note,
                            type = transaction.type,
                            categoryId = transaction.categoryId,
                            accountId = transaction.accountId,
                            date = now,
                            isRecurring = true,
                            recurringInterval = transaction.recurringInterval
                        )
                    )

                    val accounts = repository.getAllAccounts().first()
                    val account = accounts.find { it.id == transaction.accountId }
                    if (account != null) {
                        val newBalance = if (transaction.type == TransactionType.INCOME.name) {
                            account.balance + transaction.amount
                        } else {
                            account.balance - transaction.amount
                        }
                        repository.updateAccount(account.copy(balance = newBalance))
                    }
                }
            }

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "recurring_transaction_work"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(
                1, TimeUnit.DAYS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}