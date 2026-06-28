package com.example.myfinance.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun TransactionFormScreen(
    repository: FinanceRepository,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("EXPENSE") }
    var selectedAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var accounts by remember { mutableStateOf<List<AccountEntity>>(emptyList()) }
    var categories by remember { mutableStateOf<List<CategoryEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        accounts = repository.getAllAccounts().first()
        if (accounts.isNotEmpty()) selectedAccount = accounts[0]
    }

    LaunchedEffect(selectedType) {
        categories = repository.getCategoriesByType(selectedType).first()
        selectedCategory = categories.firstOrNull()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tambah Transaksi",
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
                listOf("EXPENSE" to "Pengeluaran", "INCOME" to "Pemasukan").forEach { (type, label) ->
                    Button(
                        onClick = { selectedType = type },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == type) AccentPurple else Color.Transparent,
                            contentColor = if (selectedType == type) Color.White else TextMuted
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(text = label, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Jumlah", fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() } },
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

            Text("Akun", fontSize = 13.sp, color = TextSecondary)
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

            Spacer(modifier = Modifier.height(16.dp))

            Text("Kategori", fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.take(4).forEach { category ->
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: return@Button
                    val account = selectedAccount ?: return@Button
                    val category = selectedCategory ?: return@Button

                    scope.launch {
                        isLoading = true
                        repository.insertTransaction(
                            TransactionEntity(
                                amount = amountValue,
                                note = note,
                                type = selectedType,
                                categoryId = category.id,
                                accountId = account.id,
                                date = System.currentTimeMillis()
                            )
                        )
                        // Update account balance
                        val newBalance = if (selectedType == "INCOME") {
                            account.balance + amountValue
                        } else {
                            account.balance - amountValue
                        }
                        repository.updateAccount(account.copy(balance = newBalance))
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
                Text(
                    text = "Simpan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}