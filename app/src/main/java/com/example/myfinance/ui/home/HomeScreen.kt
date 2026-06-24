package com.example.myfinance.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.domain.model.TransactionType
import com.example.myfinance.ui.components.*
import com.example.myfinance.ui.theme.*

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val dummyAccounts = listOf(
        "Cash" to 450000.0,
        "Bank BCA" to 8000000.0
    )

    val dummyBudgets = listOf(
        BudgetUiModel("Makan & Minum", 680000.0, 1000000.0),
        BudgetUiModel("Transportasi", 420000.0, 500000.0),
        BudgetUiModel("Hiburan", 310000.0, 300000.0)
    )

    val dummyTransactions = listOf(
        TransactionUiModel(
            id = 1,
            title = "Belanja Alfamart",
            categoryName = "Makan & Minum",
            accountName = "Cash",
            amount = 87000.0,
            type = TransactionType.EXPENSE,
            dateLabel = "Hari ini"
        ),
        TransactionUiModel(
            id = 2,
            title = "Isi Gopay",
            categoryName = "Transfer",
            accountName = "Bank BCA",
            amount = 200000.0,
            type = TransactionType.TRANSFER,
            dateLabel = "Kemarin"
        ),
        TransactionUiModel(
            id = 3,
            title = "Gaji Juni",
            categoryName = "Pemasukan",
            accountName = "Bank BCA",
            amount = 5200000.0,
            type = TransactionType.INCOME,
            dateLabel = "25 Jun"
        )
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Selamat datang,",
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Text(
                    text = "MyFinance",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }

        item {
            BalanceCard(
                totalBalance = dummyAccounts.sumOf { it.second },
                accounts = dummyAccounts
            )
        }

        item {
            IncomeExpenseRow(
                totalIncome = 5200000.0,
                totalExpense = 2750000.0
            )
        }

        item {
            SectionHeader(title = "Budget bulan ini", onSeeAll = {})
        }

        item {
            BudgetProgressCard(budgets = dummyBudgets)
        }

        item {
            SectionHeader(title = "Transaksi terakhir", onSeeAll = {})
        }
        
        items(dummyTransactions) { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Text(
            text = "Lihat semua",
            fontSize = 11.sp,
            color = AccentPurple
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF0F1117
)
@Composable
fun HomeScreenPreview() {
    MyFinanceTheme {
        HomeScreen()
    }
}