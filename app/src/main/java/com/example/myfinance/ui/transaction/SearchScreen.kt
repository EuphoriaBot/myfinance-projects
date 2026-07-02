package com.example.myfinance.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun SearchScreen(
    repository: FinanceRepository,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allTransactions by repository.getAllTransactions()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val accounts by repository.getAllAccounts()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val categories by repository.getAllCategories()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("SEMUA") }

    val filteredTransactions = allTransactions.filter { transaction ->
        val categoryName = categories.find { it.id == transaction.categoryId }?.name ?: ""
        val accountName = accounts.find { it.id == transaction.accountId }?.name ?: ""

        val matchesQuery = searchQuery.isEmpty() ||
                transaction.note.contains(searchQuery, ignoreCase = true) ||
                categoryName.contains(searchQuery, ignoreCase = true) ||
                accountName.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "INCOME" -> transaction.type == "INCOME"
            "EXPENSE" -> transaction.type == "EXPENSE"
            "TRANSFER" -> transaction.type == "TRANSFER"
            else -> true
        }

        matchesQuery && matchesFilter
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = TextMuted
            )
        }
        Text(
            text = "Cari Transaksi",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Text(
            text = "Cari Transaksi",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 12.dp
            )
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Cari catatan, kategori, akun...", color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = TextMuted
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentPurple,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = AccentPurple,
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "SEMUA" to "Semua",
                "INCOME" to "Pemasukan",
                "EXPENSE" to "Pengeluaran",
                "TRANSFER" to "Transfer"
            ).forEach { (filter, label) ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentPurple,
                        selectedLabelColor = Color.White,
                        containerColor = DarkCard,
                        labelColor = TextMuted
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${filteredTransactions.size} transaksi ditemukan",
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isEmpty()) "Belum ada transaksi"
                    else "Tidak ada hasil untuk \"$searchQuery\"",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredTransactions) { transaction ->
                    TransactionItem(
                        transaction = mapToUiModel(
                            entity = transaction,
                            accounts = accounts,
                            categories = categories
                        )
                    )
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