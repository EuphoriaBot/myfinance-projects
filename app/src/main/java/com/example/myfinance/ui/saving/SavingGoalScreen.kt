package com.example.myfinance.ui.saving

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.myfinance.data.local.entity.SavingGoalEntity
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatRupiah
import kotlinx.coroutines.launch
import com.example.myfinance.ui.components.BackgroundPattern
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myfinance.utils.formatInputNumber
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun SavingGoalScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: SavingGoalViewModel = hiltViewModel()
) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember {
        mutableStateOf<SavingGoalEntity?>(null)
    }
    var goalToDelete by remember {
        mutableStateOf<SavingGoalEntity?>(null)
    }

    if (showAddDialog) {
        AddSavingGoalDialog(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }

    if (goalToEdit != null) {
        EditSavingGoalDialog(
            goal = goalToEdit!!,
            onDismiss = {
                goalToEdit = null
            },
            onSave = { updatedGoal ->
                viewModel.updateGoal(updatedGoal)
                goalToEdit = null
            }
        )
    }

    if (goalToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                goalToDelete = null
            },
            title = {
                Text(
                    "Hapus Target?",
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "Target \"${goalToDelete!!.name}\" akan dihapus.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGoal(goalToDelete!!)
                        goalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpenseRed
                    )
                ) {
                    Text(
                        "Hapus",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        goalToDelete = null
                    }
                ) {
                    Text(
                        "Batal",
                        color = TextMuted
                    )
                }
            },
            containerColor = DarkCard
        )
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
                    .padding(8.dp, 8.dp, 16.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextMuted
                    )
                }

                Text(
                    text = "Target Nabung",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = AccentPurple,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Goal",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (goals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Belum ada target nabung",
                            fontSize = 15.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Tekan + untuk menambah target",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(goals) { goal ->
                        SavingGoalItem(
                            goal = goal,
                            viewModel = viewModel,
                            onEdit = {
                                goalToEdit = goal
                            },
                            onDelete = {
                                goalToDelete = goal
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavingGoalItem(
    goal: SavingGoalEntity,
    viewModel: SavingGoalViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showAddFundsDialog by remember { mutableStateOf(false) }

    val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
    val isCompleted = goal.currentAmount >= goal.targetAmount

    if (showAddFundsDialog) {
        AddFundsDialog(
            goal = goal,
            viewModel = viewModel,
            onDismiss = { showAddFundsDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (isCompleted) {
                    Text(
                        text = "✓ Tercapai!",
                        fontSize = 12.sp,
                        color = IncomeGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.07f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isCompleted) IncomeGreen else AccentPurple)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatRupiah(goal.currentAmount),
                    fontSize = 12.sp,
                    color = if (isCompleted) IncomeGreen else AccentPurple,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatRupiah(goal.targetAmount),
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!isCompleted) {
                val remaining = goal.targetAmount - goal.currentAmount
                Text(
                    text = "Sisa ${formatRupiah(remaining)} lagi",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isCompleted) {
                    OutlinedButton(
                        onClick = { showAddFundsDialog = true },
                        modifier = Modifier.weight(1f),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = androidx.compose.ui.graphics.SolidColor(AccentPurple)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Tambah Dana",
                            color = AccentPurple
                        )
                    }
                }

                IconButton(
                    onClick = onEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = AccentPurple
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = ExpenseRed
                    )
                }
            }
        }
    }
}

@Composable
private fun AddSavingGoalDialog(
    viewModel: SavingGoalViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCard, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Tambah Target Nabung",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Nama Goal", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Contoh: Beli Laptop", color = TextMuted) },
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

                Spacer(modifier = Modifier.height(16.dp))

                Text("Target Nominal", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = if (targetAmount.isEmpty()) "" else formatInputNumber(targetAmount),
                    onValueChange = { newValue ->
                        targetAmount = newValue.replace(".", "").filter { it.isDigit() }
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
                            val target = targetAmount.toDoubleOrNull() ?: return@Button
                            if (name.isEmpty()) return@Button
                            scope.launch {
                                isLoading = true
                                viewModel.insertGoal(
                                    SavingGoalEntity(
                                        name = name,
                                        targetAmount = target,
                                        currentAmount = 0.0
                                    )
                                )
                                isLoading = false
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        enabled = !isLoading && name.isNotEmpty() && targetAmount.isNotEmpty()
                    ) {
                        Text("Simpan", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditSavingGoalDialog(
    goal: SavingGoalEntity,
    onDismiss: () -> Unit,
    onSave: (SavingGoalEntity) -> Unit
) {
    var name by remember {
        mutableStateOf(goal.name)
    }

    var targetAmount by remember {
        mutableStateOf(goal.targetAmount.toLong().toString())
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCard, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Edit Target Nabung",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Nama Goal",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Target Nominal",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (targetAmount.isEmpty()) "" else formatInputNumber(targetAmount),
                    onValueChange = {
                        targetAmount = it.replace(".", "")
                            .filter(Char::isDigit)
                    },
                    modifier = Modifier.fillMaxWidth(),
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
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val target = targetAmount.toDoubleOrNull() ?: return@Button
                            onSave(
                                goal.copy(
                                    name = name,
                                    targetAmount = target
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPurple
                        )
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddFundsDialog(
    goal: SavingGoalEntity,
    viewModel: SavingGoalViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCard, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Tambah Dana ke ${goal.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Saat ini: ${formatRupiah(goal.currentAmount)} / ${formatRupiah(goal.targetAmount)}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                            val addAmount = amount.toDoubleOrNull() ?: return@Button
                            scope.launch {
                                isLoading = true
                                val newAmount = (goal.currentAmount + addAmount)
                                    .coerceAtMost(goal.targetAmount)
                                viewModel.updateGoal(
                                    goal.copy(
                                        currentAmount = newAmount,
                                        isCompleted = newAmount >= goal.targetAmount
                                    )
                                )
                                isLoading = false
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        enabled = !isLoading && amount.isNotEmpty()
                    ) {
                        Text("Tambah", color = Color.White)
                    }
                }
            }
        }
    }
}