package com.example.myfinance.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.data.local.entity.BudgetEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatRupiah
import com.example.myfinance.utils.formatInputNumber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BudgetManagementScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var budgetToEdit by remember { mutableStateOf<BudgetEntity?>(null) }
    var budgetToDelete by remember { mutableStateOf<BudgetEntity?>(null) }

    if (showAddDialog) {
        AddBudgetDialog(
            categories = categories,
            onDismiss = { showAddDialog = false },
            onSave = { budget ->
                viewModel.insertBudget(budget)
                showAddDialog = false
            }
        )
    }

    if (budgetToEdit != null) {
        EditBudgetDialog(
            budget = budgetToEdit!!,
            categories = categories,
            onDismiss = { budgetToEdit = null },
            onSave = { budget ->
                viewModel.updateBudget(budget)
                budgetToEdit = null
            }
        )
    }

    if (budgetToDelete != null) {
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            title = { Text("Hapus Budget?", color = TextPrimary) },
            text = {
                val categoryName = categories
                    .find { it.id == budgetToDelete!!.categoryId }?.name ?: "ini"
                Text(
                    "Budget kategori \"$categoryName\" akan dihapus.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBudget(budgetToDelete!!)
                        budgetToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { budgetToDelete = null }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = DarkCard
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextMuted
                    )
                }
                Text(
                    text = "Kelola Budget",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentPurple,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Budget",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (budgets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Belum ada budget",
                        fontSize = 15.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Tekan + untuk menambah budget",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(budgets) { budget ->
                    val categoryName = categories
                        .find { it.id == budget.categoryId }?.name ?: "Kategori"
                    BudgetItem(
                        budget = budget,
                        categoryName = categoryName,
                        onEdit = { budgetToEdit = budget },
                        onDelete = { budgetToDelete = budget }
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetItem(
    budget: BudgetEntity,
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = categoryName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Limit: ${formatRupiah(budget.limitAmount)} / bulan",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = AccentPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = ExpenseRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditBudgetDialog(
    budget: BudgetEntity,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (BudgetEntity) -> Unit
) {
    var limitAmount by remember {
        mutableStateOf(budget.limitAmount.toLong().toString())
    }
    val categoryName = categories.find { it.id == budget.categoryId }?.name ?: "Kategori"

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCard, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Edit Budget",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Kategori: $categoryName",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Limit Bulanan Baru", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = if (limitAmount.isEmpty()) "" else formatInputNumber(limitAmount),
                    onValueChange = {
                        limitAmount = it.replace(".", "")
                            .filter(Char::isDigit)
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
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal", color = TextMuted)
                    }
                    Button(
                        onClick = {
                            val limit = limitAmount.toDoubleOrNull() ?: return@Button
                            onSave(budget.copy(limitAmount = limit))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        enabled = limitAmount.isNotEmpty()
                    ) {
                        Text("Simpan", color = Color.White)
                    }
                }
            }
        }
    }
}