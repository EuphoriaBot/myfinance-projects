package com.example.myfinance.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myfinance.data.local.entity.BudgetEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.domain.model.TransactionType
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatInputNumber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddBudgetDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (BudgetEntity) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
    var limitAmount by remember { mutableStateOf("") }

    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) {
            selectedCategory = categories.first()
        }
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
                    text = "Tambah Budget",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Kategori", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.filter {
                        it.type == TransactionType.EXPENSE.name
                    }.forEach { category ->
                        FilterChip(
                            selected = selectedCategory?.id == category.id,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple,
                                selectedLabelColor = Color.White,
                                containerColor = DarkBackground,
                                labelColor = TextMuted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Limit Bulanan", fontSize = 12.sp, color = TextSecondary)
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
                            val category = selectedCategory ?: return@Button
                            onSave(
                                BudgetEntity(
                                    categoryId = category.id,
                                    limitAmount = limit,
                                    period = "MONTHLY"
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        enabled = limitAmount.isNotEmpty() && selectedCategory != null
                    ) {
                        Text("Simpan", color = Color.White)
                    }
                }
            }
        }
    }
}