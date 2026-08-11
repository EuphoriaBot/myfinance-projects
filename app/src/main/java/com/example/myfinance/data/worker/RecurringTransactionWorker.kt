package com.example.myfinance.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@HiltWorker
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: FinanceRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val allTransactions = repository
                .getAllTransactions()
                .first()

            val recurringTransactions = allTransactions
                .filter { it.isRecurring }

            val now = System.currentTimeMillis()

            val oneDayMs = 24 * 60 * 60 * 1000L
            val oneWeekMs = 7 * oneDayMs

            recurringTransactions.forEach { transaction ->

                val shouldRun = when (transaction.recurringInterval) {

                    "DAILY" -> {
                        now - transaction.date >= oneDayMs
                    }

                    "WEEKLY" -> {
                        now - transaction.date >= oneWeekMs
                    }

                    "MONTHLY" -> {
                        val lastDate = Instant.ofEpochMilli(transaction.date)
                            .atZone(ZoneId.systemDefault())

                        val currentDate = Instant.ofEpochMilli(now)
                            .atZone(ZoneId.systemDefault())

                        currentDate.year > lastDate.year ||
                                currentDate.monthValue > lastDate.monthValue
                    }

                    "YEARLY" -> {
                        val lastDate = Instant.ofEpochMilli(transaction.date)
                            .atZone(ZoneId.systemDefault())

                        val currentDate = Instant.ofEpochMilli(now)
                            .atZone(ZoneId.systemDefault())

                        currentDate.year > lastDate.year
                    }

                    else -> false
                }

                if (shouldRun) {

                    val recurringTransaction = TransactionEntity(
                        amount = transaction.amount,
                        note = transaction.note,
                        type = transaction.type,
                        categoryId = transaction.categoryId,
                        accountId = transaction.accountId,
                        toAccountId = transaction.toAccountId,
                        date = now,
                        isRecurring = false,
                        recurringInterval = null
                    )

                    repository.addRecurringTransaction(
                        recurringTransaction
                    )
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

            val request =
                PeriodicWorkRequestBuilder<RecurringTransactionWorker>(
                    1,
                    TimeUnit.DAYS
                )
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(
                                NetworkType.NOT_REQUIRED
                            )
                            .build()
                    )
                    .build()

            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}