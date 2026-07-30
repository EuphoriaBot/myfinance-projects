package com.example.myfinance.ui.account

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
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatInputNumber

@Composable
fun AddAccountDialog(
    viewModel: AccountViewModel,
    onSave: (AccountEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("CASH") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkCard, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {

                Text(
                    text = "Tambah Akun",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tipe Akun",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            DarkBackground,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    listOf(
                        "CASH" to "Cash",
                        "BANK" to "Bank",
                        "E_WALLET" to "E-Wallet"
                    ).forEach { (type, label) ->

                        Button(
                            onClick = {
                                selectedType = type
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedType == type)
                                        AccentPurple
                                    else
                                        Color.Transparent,

                                contentColor =
                                    if (selectedType == type)
                                        Color.White
                                    else
                                        TextMuted
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(label, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nama Akun",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    isError = errorMessage != null,
                    supportingText = {
                        errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            when (selectedType) {
                                "BANK" -> "Contoh: Bank BCA"
                                "E_WALLET" -> "Contoh: GoPay"
                                else -> "Contoh: Dompet"
                            },
                            color = TextMuted
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentPurple
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Saldo Awal",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (balance.isEmpty()) "" else formatInputNumber(balance),
                    onValueChange = {
                        balance = it.replace(".", "")
                            .filter(Char::isDigit)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    prefix = {
                        Text("Rp ", color = TextMuted)
                    },
                    placeholder = {
                        Text("0", color = TextMuted)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentPurple
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = !name.isBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPurple
                        ),
                        onClick = {
                            val cleanName = name.trim()
                            when {
                                cleanName.isBlank() -> {
                                    errorMessage = "Nama akun tidak boleh kosong"
                                    return@Button
                                }

                                cleanName.length > 30 -> {
                                    errorMessage = "Maksimal 30 karakter"
                                    return@Button
                                }

                                viewModel.accountExists(cleanName) -> {
                                    errorMessage = "Nama akun sudah digunakan"
                                    return@Button
                                }
                            }

                            onSave(
                                AccountEntity(
                                    name = cleanName,
                                    balance = balance.toDoubleOrNull() ?: 0.0,
                                    type = selectedType,
                                    colorHex = when (selectedType) {
                                        "BANK" -> "#6C63FF"
                                        "E_WALLET" -> "#00C896"
                                        else -> "#F5A623"
                                    }
                                )
                            )
                            onDismiss()
                        }
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}