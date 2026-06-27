package com.example.myfinance.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.domain.model.TransactionType
import com.example.myfinance.ui.components.*
import com.example.myfinance.ui.theme.*

@Composable
fun HomeScreen(
    repository: FinanceRepository,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(repository)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf(BottomNavDestination.HOME) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            BottomNavBar(
                currentDestination = currentDestination,
                onDestinationChanged = { currentDestination = it },
                onAddClick = {}
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 16.dp)
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
                    totalBalance = uiState.totalBalance,
                    accounts = uiState.accounts.map { it.name to it.balance }
                )
            }

            item {
                IncomeExpenseRow(
                    totalIncome = uiState.totalIncome,
                    totalExpense = uiState.totalExpense
                )
            }

            item {
                SectionHeader(title = "Budget bulan ini", onSeeAll = {})
            }
            item {
                BudgetProgressCard(
                    budgets = listOf(
                        BudgetUiModel("Makan & Minum", 680000.0, 1000000.0),
                        BudgetUiModel("Transportasi", 420000.0, 500000.0),
                        BudgetUiModel("Hiburan", 310000.0, 300000.0)
                    )
                )
            }
            
            item {
                SectionHeader(title = "Transaksi terakhir", onSeeAll = {})
            }
            items(uiState.recentTransactions) { transaction ->
                TransactionItem(
                    transaction = mapToUiModel(transaction)
                )
            }
        }
    }
}

private fun mapToUiModel(entity: TransactionEntity): TransactionUiModel {
    return TransactionUiModel(
        id = entity.id,
        title = entity.note.ifEmpty { entity.type },
        categoryName = entity.categoryId.toString(),
        accountName = entity.accountId.toString(),
        amount = entity.amount,
        type = when (entity.type) {
            "INCOME" -> TransactionType.INCOME
            "EXPENSE" -> TransactionType.EXPENSE
            else -> TransactionType.TRANSFER
        },
        dateLabel = android.text.format.DateFormat.format(
            "dd MMM", entity.date
        ).toString()
    )
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