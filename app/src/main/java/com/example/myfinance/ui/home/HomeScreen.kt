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
import com.example.myfinance.ui.account.AccountScreen
import com.example.myfinance.ui.components.*
import com.example.myfinance.ui.theme.*
import com.example.myfinance.ui.transaction.TransactionFormScreen
import com.example.myfinance.ui.report.ReportScreen
import com.example.myfinance.ui.settings.SettingsScreen
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.AccountEntity
import androidx.compose.foundation.background
import com.example.myfinance.ui.transaction.TransactionScreen
import com.example.myfinance.ui.saving.SavingGoalScreen
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.clickable
import com.example.myfinance.ui.components.BackgroundPattern

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
    var showTransactionForm by remember { mutableStateOf(false) }
    var showGoalScreen by remember { mutableStateOf(false) }
    var showAllTransactions by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }

    if (showGoalScreen) {
        SavingGoalScreen(
            repository = repository,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    if (showAllTransactions) {
        TransactionScreen(
            repository = repository,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    if (showAddBudgetDialog) {
        com.example.myfinance.ui.settings.AddBudgetDialog(
            repository = repository,
            onDismiss = { showAddBudgetDialog = false }
        )
        return
    }

    if (showTransactionForm) {
        TransactionFormScreen(
            repository = repository,
            onDismiss = { showTransactionForm = false }
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = DarkBackground,
            bottomBar = {
                BottomNavBar(
                    currentDestination = currentDestination,
                    onDestinationChanged = { currentDestination = it },
                    onAddClick = { showTransactionForm = true }
                )
            }
        ) { innerPadding ->
            when (currentDestination) {
                BottomNavDestination.TRANSACTIONS -> {
                    TransactionScreen(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                BottomNavDestination.ACCOUNT -> {
                    AccountScreen(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                BottomNavDestination.REPORT -> {
                    ReportScreen(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                BottomNavDestination.SETTINGS -> {
                    SettingsScreen(
                        repository = repository,
                        onNavigateToGoals = { showGoalScreen = true },
                        onNavigateToAccount = { currentDestination = BottomNavDestination.ACCOUNT },
                        onNavigateToReport = { currentDestination = BottomNavDestination.REPORT },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        BackgroundPattern()

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(
                                top = 24.dp,
                                bottom = 16.dp
                            )
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
                                SectionHeader(
                                    title = "Budget bulan ini",
                                    onSeeAll = { showAddBudgetDialog = true }
                                )
                            }

                            item {
                                if (uiState.budgets.isNotEmpty()) {
                                    BudgetProgressCard(
                                        budgets = uiState.budgets.map {
                                            BudgetUiModel(it.categoryName, it.spent, it.limit)
                                        }
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .background(
                                                DarkCard,
                                                androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
                                            )
                                            .padding(20.dp)
                                    ) {
                                        Text(
                                            text = "Belum ada budget. Tambahkan budget di menu Setelan.",
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }

                            item {
                                SectionHeader(
                                    title = "Transaksi terakhir",
                                    onSeeAll = { showAllTransactions = true }
                                )
                            }

                            items(uiState.recentTransactions) { transaction ->
                                TransactionItem(
                                    transaction = mapToUiModel(
                                        entity = transaction,
                                        accounts = uiState.accounts,
                                        categories = uiState.categories
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun mapToUiModel(
    entity: TransactionEntity,
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>
): TransactionUiModel {
    val accountName = accounts.find { it.id == entity.accountId }?.name ?: "Akun"
    val categoryName = categories.find { it.id == entity.categoryId }?.name ?: "Kategori"

    return TransactionUiModel(
        id = entity.id,
        title = entity.note.ifEmpty { categoryName },
        categoryName = categoryName,
        accountName = accountName,
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
            color = AccentPurple,
            modifier = Modifier.clickable {
                onSeeAll()
            }
        )
    }
}