package com.example.myfinance.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.domain.model.TransactionType
import com.example.myfinance.ui.components.TransactionItem
import com.example.myfinance.ui.components.TransactionUiModel
import com.example.myfinance.ui.theme.*
import com.example.myfinance.ui.components.BackgroundPattern

@Composable
fun TransactionScreen(
    repository: FinanceRepository,
    modifier: Modifier = Modifier
) {
    val transactions by repository.getAllTransactions()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val accounts by repository.getAllAccounts()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val categories by repository.getAllCategories()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var showSearch by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

    if (selectedTransaction != null) {
        TransactionDetailSheet(
            transaction = selectedTransaction!!,
            accounts = accounts,
            categories = categories,
            repository = repository,
            onDismiss = { selectedTransaction = null }
        )
    }

    if (showSearch) {
        SearchScreen(
            repository = repository,
            onBack = { showSearch = false },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        BackgroundPattern()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                IconButton(onClick = { showSearch = true }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari",
                        tint = TextMuted
                    )
                }
            }

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Belum ada transaksi",
                            fontSize = 15.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Tekan tombol + untuk menambah transaksi",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                val grouped = transactions.groupBy { transaction ->
                    android.text.format.DateFormat.format(
                        "dd MMMM yyyy", transaction.date
                    ).toString()
                }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    grouped.forEach { (date, dayTransactions) ->
                        item {
                            Text(
                                text = date,
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            )
                        }
                        items(dayTransactions) { transaction ->
                            TransactionItem(
                                transaction = mapToUiModel(
                                    entity = transaction,
                                    accounts = accounts,
                                    categories = categories
                                ),
                                onClick = { selectedTransaction = transaction }
                            )
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
            "HH:mm", entity.date
        ).toString()
    )
}