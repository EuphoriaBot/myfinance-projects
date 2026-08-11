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
import java.time.LocalDate

@HiltWorker
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: FinanceRepository
) : CoroutineWorker(appContext, params) {

    private fun shouldRunRecurring(
        transaction: TransactionEntity,
        now: Long
    ): Boolean {

        val lastDate = Instant.ofEpochMilli(transaction.date)
            .atZone(ZoneId.systemDefault())

        val currentDate = Instant.ofEpochMilli(now)
            .atZone(ZoneId.systemDefault())

        return when (transaction.recurringInterval) {

            "DAILY" -> {
                now - transaction.date >= 24 * 60 * 60 * 1000L
            }

            "WEEKLY" -> {
                now - transaction.date >= 7 * 24 * 60 * 60 * 1000L
            }

            "MONTHLY" -> {

                val monthsDifference =
                    (currentDate.year - lastDate.year) * 12 +
                            (currentDate.monthValue - lastDate.monthValue)

                if (monthsDifference < 1) {
                    false
                } else {

                    val lastDayOfCurrentMonth =
                        currentDate.toLocalDate().lengthOfMonth()

                    val targetDay =
                        minOf(
                            lastDate.dayOfMonth,
                            lastDayOfCurrentMonth
                        )

                    currentDate.dayOfMonth >= targetDay
                }
            }

            "YEARLY" -> {
                val yearsDifference =
                    currentDate.year - lastDate.year

                if (yearsDifference < 1) {
                    false
                } else {
                    val lastDayOfCurrentMonth =
                        currentDate.toLocalDate().lengthOfMonth()
                    val targetDay =
                        minOf(
                            lastDate.dayOfMonth,
                            lastDayOfCurrentMonth
                        )
                    currentDate.monthValue > lastDate.monthValue || (currentDate.monthValue == lastDate.monthValue && currentDate.dayOfMonth >= targetDay)
                }
            }
            else -> false
        }
    }

    private fun getPeriodRange(
        transaction: TransactionEntity,
        now: Long
    ): Pair<Long, Long> {

        val zone = ZoneId.systemDefault()

        val currentDate = Instant.ofEpochMilli(now)
            .atZone(zone)
            .toLocalDate()

        return when (transaction.recurringInterval) {

            "DAILY" -> {
                val start = currentDate
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli()

                val end = currentDate
                    .plusDays(1)
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli() - 1

                start to end
            }

            "WEEKLY" -> {
                val startDate = currentDate
                    .minusDays(
                        currentDate.dayOfWeek.value.toLong() - 1
                    )

                val endDate = startDate.plusDays(7)

                val start = startDate
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli()

                val end = endDate
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli() - 1

                start to end
            }

            "MONTHLY" -> {
                val startDate = currentDate.withDayOfMonth(1)
                val endDate = startDate.plusMonths(1)

                val start = startDate
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli()

                val end = endDate
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli() - 1

                start to end
            }

            "YEARLY" -> {
                val startDate = LocalDate.of(
                    currentDate.year,
                    1,
                    1
                )

                val endDate = startDate.plusYears(1)

                val start = startDate
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli()

                val end = endDate
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli() - 1

                start to end
            }

            else -> {
                now to now
            }
        }
    }

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

                val shouldRun = shouldRunRecurring(
                    transaction = transaction,
                    now = now
                )

                if (shouldRun) {

                    val (periodStart, periodEnd) = getPeriodRange(
                        transaction = transaction,
                        now = now
                    )

                    val alreadyGenerated =
                        repository.hasRecurringTransactionInPeriod(
                            sourceId = transaction.id,
                            startDate = periodStart,
                            endDate = periodEnd
                        )

                    if (alreadyGenerated) {
                        return@forEach
                    }

                    val recurringTransaction = TransactionEntity(
                        amount = transaction.amount,
                        note = transaction.note,
                        type = transaction.type,
                        categoryId = transaction.categoryId,
                        accountId = transaction.accountId,
                        toAccountId = transaction.toAccountId,
                        date = now,
                        isRecurring = false,
                        recurringSourceId = transaction.id,
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