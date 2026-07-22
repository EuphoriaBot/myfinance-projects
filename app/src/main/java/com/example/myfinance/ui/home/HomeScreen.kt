package com.example.myfinance.ui.home

import androidx.compose.foundation.background
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
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.domain.model.TransactionType
import com.example.myfinance.ui.account.AccountScreen
import com.example.myfinance.ui.category.CategoryScreen
import com.example.myfinance.ui.components.*
import com.example.myfinance.ui.report.ReportScreen
import com.example.myfinance.ui.saving.SavingGoalScreen
import com.example.myfinance.ui.settings.SettingsScreen
import com.example.myfinance.ui.theme.*
import com.example.myfinance.ui.transaction.TransactionFormScreen
import com.example.myfinance.ui.transaction.TransactionScreen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf(BottomNavDestination.HOME) }
    var showTransactionForm by remember { mutableStateOf(false) }
    var showGoalScreen by remember { mutableStateOf(false) }
    var showCategoryScreen by remember { mutableStateOf(false) }

    BackHandler(
        enabled = showTransactionForm || showGoalScreen ||
                showCategoryScreen || currentDestination != BottomNavDestination.HOME
    ) {
        when {
            showTransactionForm -> showTransactionForm = false
            showGoalScreen -> showGoalScreen = false
            showCategoryScreen -> showCategoryScreen = false
            currentDestination != BottomNavDestination.HOME ->
                currentDestination = BottomNavDestination.HOME
        }
    }

    when {
        showTransactionForm -> {
            TransactionFormScreen(onDismiss = { showTransactionForm = false })
            return
        }
        showGoalScreen -> {
            SavingGoalScreen()
            return
        }
        showCategoryScreen -> {
            CategoryScreen(onBack = { showCategoryScreen = false })
            return
        }
    }

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
                TransactionScreen(modifier = Modifier.padding(innerPadding))
            }
            BottomNavDestination.ACCOUNT -> {
                AccountScreen(modifier = Modifier.padding(innerPadding))
            }
            BottomNavDestination.REPORT -> {
                ReportScreen(modifier = Modifier.padding(innerPadding))
            }
            BottomNavDestination.SETTINGS -> {
                SettingsScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToGoals = { showGoalScreen = true },
                    onNavigateToAccount = { currentDestination = BottomNavDestination.ACCOUNT },
                    onNavigateToReport = { currentDestination = BottomNavDestination.REPORT },
                    onNavigateToCategory = { showCategoryScreen = true }
                )
            }
            else -> {
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
                        Text(
                            text = "Budget bulan ini",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp)
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
                                    text = "Belum ada budget. Tambahkan di menu Setelan.",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                    item {
                        Text(
                            text = "Transaksi terakhir",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp)
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

private fun mapToUiModel(
    entity: TransactionEntity,
    accounts: List<com.example.myfinance.data.local.entity.AccountEntity>,
    categories: List<com.example.myfinance.data.local.entity.CategoryEntity>
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
        dateLabel = android.text.format.DateFormat.format("dd MMM", entity.date).toString()
    )
}