package com.example.myfinance.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatRupiah
import kotlinx.coroutines.launch

fun formatInputRupiah(input: String): String {
    if (input.isEmpty()) return ""
    return input.reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}
@Composable
fun OnboardingScreen(
    repository: FinanceRepository,
    onFinished: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var cashBalance by remember { mutableStateOf("") }
    var bankBalance by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Selamat datang!",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Setup akun kamu untuk mulai mencatat keuangan.",
                fontSize = 14.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Saldo Cash",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = if (cashBalance.isEmpty()) "" else formatInputRupiah(cashBalance),
                onValueChange = { newValue ->
                    cashBalance = newValue
                        .replace(".", "")
                        .filter { it.isDigit() }
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nama Bank",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                modifier = Modifier.fillMaxWidth(),
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
                text = "Saldo $bankName",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = if (bankBalance.isEmpty()) "" else formatInputRupiah(bankBalance),
                onValueChange = { newValue ->
                    bankBalance = newValue
                        .replace(".", "")
                        .filter { it.isDigit() }
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

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        repository.insertAccount(
                            AccountEntity(
                                name = "Cash",
                                balance = cashBalance.toDoubleOrNull() ?: 0.0,
                                type = "CASH",
                                colorHex = "#00C896"
                            )
                        )
                        repository.insertAccount(
                            AccountEntity(
                                name = bankName,
                                balance = bankBalance.toDoubleOrNull() ?: 0.0,
                                type = "BANK",
                                colorHex = "#6C63FF"
                            )
                        )
                        isLoading = false
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Mulai Gunakan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}