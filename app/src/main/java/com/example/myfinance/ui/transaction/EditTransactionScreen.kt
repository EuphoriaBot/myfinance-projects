package com.example.myfinance.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.FlowRow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private fun formatInputNumber(input: String): String {
    if (input.isEmpty()) return ""
    return input.reversed().chunked(3).joinToString(".").reversed()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTransactionScreen(
    transaction: TransactionEntity,
    onDismiss: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var amount by remember { mutableStateOf(transaction.amount.toLong().toString()) }
    var note by remember { mutableStateOf(transaction.note) }
    var selectedType by remember { mutableStateOf(transaction.type) }
    var selectedAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var selectedToAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(accounts) {
        selectedAccount = accounts.find { it.id == transaction.accountId }
        selectedToAccount = accounts.find { it.id == transaction.toAccountId }
    }

    LaunchedEffect(selectedAccount) {
        if (selectedToAccount?.id == selectedAccount?.id) {
            selectedToAccount = accounts.firstOrNull { it.id != selectedAccount?.id }
        }
    }

    LaunchedEffect(categories, selectedType) {
        if (selectedType == "TRANSFER") {
            selectedCategory = null
            return@LaunchedEffect
        }

        val currentCategoryStillExists =
            categories.any { it.id == selectedCategory?.id }

        if (!currentCategoryStillExists || selectedCategory?.type != selectedType) {
            selectedCategory = categories.firstOrNull { it.type == selectedType }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Transaksi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "EXPENSE" to "Pengeluaran",
                    "INCOME" to "Pemasukan",
                    "TRANSFER" to "Transfer"
                ).forEach { (type, label) ->
                    Button(
                        onClick = { selectedType = type },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == type) AccentPurple else Color.Transparent,
                            contentColor = if (selectedType == type) Color.White else TextMuted
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(text = label, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Jumlah", fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = if (amount.isEmpty()) "" else formatInputNumber(amount),
                onValueChange = { newValue ->
                    amount = newValue.replace(".", "").filter { it.isDigit() }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0", color = TextMuted) },
                prefix = { Text("Rp ", color = TextMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentPurple,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Catatan", fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Opsional", color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentPurple,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (selectedType == "TRANSFER")
                    "Dari Akun"
                else
                    "Akun",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                accounts.forEach { account ->
                    FilterChip(
                        selected = selectedAccount?.id == account.id,
                        onClick = { selectedAccount = account },
                        label = { Text(account.name, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentPurple,
                            selectedLabelColor = Color.White,
                            containerColor = DarkCard,
                            labelColor = TextMuted
                        )
                    )
                }
            }

            if (selectedType == "TRANSFER") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Ke Akun",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts
                        .filter { it.id != selectedAccount?.id }
                        .forEach { account ->
                            FilterChip(
                                selected = selectedToAccount?.id == account.id,
                                onClick = {
                                    selectedToAccount = account
                                },
                                label = {
                                    Text(account.name)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IncomeGreen,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkCard,
                                    labelColor = TextMuted
                                )
                            )
                        }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedType != "TRANSFER") {
                Text("Kategori", fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories
                        .filter { it.type == selectedType }
                        .forEach { category ->
                            FilterChip(
                                selected = selectedCategory?.id == category.id,
                                onClick = { selectedCategory = category },
                                label = { Text(category.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AccentPurple,
                                    selectedLabelColor = Color.White,
                                    containerColor = DarkCard,
                                    labelColor = TextMuted
                                )
                            )
                        }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    errorMessage = viewModel.validateTransaction(
                        amount = amount,
                        account = selectedAccount,
                        category = if (selectedType == "TRANSFER") null else selectedCategory,
                        type = selectedType,
                        toAccount = selectedToAccount
                    )

                    if (errorMessage != null) {
                        return@Button
                    }

                    val amountValue = amount.toDouble()
                    val account = selectedAccount!!

                    scope.launch {
                        isLoading = true
                        when (transaction.type) {
                            "INCOME" -> {
                                viewModel.getAccountById(transaction.accountId)?.let {
                                    viewModel.updateAccount(
                                        it.copy(balance = it.balance - transaction.amount)
                                    )
                                }
                            }

                            "EXPENSE" -> {
                                viewModel.getAccountById(transaction.accountId)?.let {
                                    viewModel.updateAccount(
                                        it.copy(balance = it.balance + transaction.amount)
                                    )
                                }
                            }

                            "TRANSFER" -> {
                                viewModel.getAccountById(transaction.accountId)?.let {
                                    viewModel.updateAccount(
                                        it.copy(balance = it.balance + transaction.amount)
                                    )
                                }

                                transaction.toAccountId?.let { toId ->
                                    viewModel.getAccountById(toId)?.let {
                                        viewModel.updateAccount(
                                            it.copy(balance = it.balance - transaction.amount)
                                        )
                                    }
                                }
                            }
                        }

                        if (selectedType == "TRANSFER") {
                            val toAccount = selectedToAccount!!
                            viewModel.updateTransaction(
                                transaction.copy(
                                    amount = amountValue,
                                    note = note,
                                    type = "TRANSFER",
                                    categoryId = 0,
                                    accountId = account.id,
                                    toAccountId = toAccount.id
                                )
                            )

                            viewModel.updateAccount(
                                account.copy(
                                    balance = account.balance - amountValue
                                )
                            )

                            viewModel.updateAccount(
                                toAccount.copy(
                                    balance = toAccount.balance + amountValue
                                )
                            )

                        } else {
                            val category = selectedCategory!!
                            viewModel.updateTransaction(
                                transaction.copy(
                                    amount = amountValue,
                                    note = note,
                                    type = selectedType,
                                    categoryId = category.id,
                                    accountId = account.id,
                                    toAccountId = null
                                )
                            )

                            val newBalance =
                                if (selectedType == "INCOME")
                                    account.balance + amountValue
                                else
                                    account.balance - amountValue
                            viewModel.updateAccount(
                                account.copy(balance = newBalance)
                            )
                        }
                        isLoading = false
                        onDismiss()
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading && amount.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Simpan Perubahan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}