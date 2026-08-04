package com.example.myfinance.ui.transaction

import androidx.compose.runtime.Composable
import com.example.myfinance.data.local.entity.TransactionEntity
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
import com.example.myfinance.ui.theme.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.utils.formatInputNumber
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTransferScreen(
    transaction: TransactionEntity,
    onDismiss: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()

    var amount by remember {
        mutableStateOf(transaction.amount.toLong().toString())
    }

    var note by remember {
        mutableStateOf(transaction.note)
    }

    var selectedFromAccount by remember {
        mutableStateOf<AccountEntity?>(null)
    }

    var selectedToAccount by remember {
        mutableStateOf<AccountEntity?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(accounts) {
        selectedFromAccount =
            accounts.find { it.id == transaction.accountId }

        selectedToAccount =
            accounts.find { it.id == transaction.toAccountId }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Edit Transfer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Jumlah",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = if (amount.isEmpty()) "" else formatInputNumber(amount),
                onValueChange = {
                    amount = it.filter { char -> char.isDigit() }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("0", color = TextMuted)
                },
                prefix = {
                    Text("Rp ", color = TextMuted)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentPurple
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Catatan",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Opsional", color = TextMuted)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentPurple
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dari Akun",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                accounts.forEach { account ->
                    FilterChip(
                        selected = selectedFromAccount?.id == account.id,
                        onClick = {
                            selectedFromAccount = account

                            if (selectedToAccount?.id == account.id) {
                                selectedToAccount =
                                    accounts.firstOrNull { it.id != account.id }
                            }
                        },
                        label = {
                            Text(account.name)
                        },
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

            Text(
                text = "Ke Akun",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                accounts
                    .filter { it.id != selectedFromAccount?.id }
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

            Spacer(modifier = Modifier.height(24.dp))

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
                    val amountValue = amount.toDouble()
                    var availableBalance = selectedFromAccount!!.balance

                    if (selectedFromAccount!!.id == transaction.toAccountId) {
                        availableBalance -= transaction.amount
                    }

                    if (selectedFromAccount!!.id == transaction.accountId) {
                        availableBalance += transaction.amount
                    }

                    if (amountValue > availableBalance) {
                        errorMessage = "Saldo akun tidak mencukupi"
                        return@Button
                    }

                    errorMessage = viewModel.validateEditTransfer(
                        amount = amount,
                        oldTransaction = transaction,
                        fromAccount = selectedFromAccount,
                        toAccount = selectedToAccount
                    )

                    if (errorMessage != null) {
                        return@Button
                    }

                    val newTransaction = transaction.copy(
                        amount = amountValue,
                        note = note,
                        accountId = selectedFromAccount!!.id,
                        toAccountId = selectedToAccount!!.id,
                        categoryId = 0,
                        type = "TRANSFER"
                    )

                    isLoading = true

                    viewModel.updateTransfer(
                        oldTransaction = transaction,
                        newTransaction = newTransaction
                    )

                    isLoading = false
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple
                )
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )

                } else {

                    Text(
                        "Simpan Perubahan",
                        color = Color.White
                    )

                }
            }
        }
    }
}