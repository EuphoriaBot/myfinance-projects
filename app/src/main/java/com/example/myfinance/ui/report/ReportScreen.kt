package com.example.myfinance.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatRupiah
import java.util.Calendar

@Composable
fun ReportScreen(
    repository: FinanceRepository,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    val startOfMonth = calendar.timeInMillis
    val endOfMonth = System.currentTimeMillis()

    val totalIncome by repository.getTotalByTypeAndDateRange("INCOME", startOfMonth, endOfMonth)
        .collectAsStateWithLifecycle(initialValue = 0.0)

    val totalExpense by repository.getTotalByTypeAndDateRange("EXPENSE", startOfMonth, endOfMonth)
        .collectAsStateWithLifecycle(initialValue = 0.0)

    val transactions by repository.getTransactionsByDateRange(startOfMonth, endOfMonth)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val income = totalIncome ?: 0.0
    val expense = totalExpense ?: 0.0
    val net = income - expense

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Laporan Bulan Ini",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SummaryCard(
                    label = "Pemasukan",
                    amount = income,
                    color = IncomeGreen,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "Pengeluaran",
                    amount = expense,
                    color = ExpenseRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selisih bulan ini",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = formatRupiah(net),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (net >= 0) IncomeGreen else ExpenseRed
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Rasio Pengeluaran",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    val ratio = if (income > 0) (expense / income).coerceIn(0.0, 1.0).toFloat() else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.07f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        ratio >= 1f -> ExpenseRed
                                        ratio >= 0.8f -> WarningAmber
                                        else -> IncomeGreen
                                    }
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(ratio * 100).toInt()}% dari pemasukan digunakan",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkCard)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total transaksi bulan ini",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${transactions.size} transaksi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    amount: Double,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatRupiah(amount),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}