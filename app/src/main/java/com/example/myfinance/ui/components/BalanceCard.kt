package com.example.myfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.myfinance.utils.formatRupiah

@Composable
fun BalanceCard(
    totalBalance: Double,
    accounts: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(AccentPurple)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Total Saldo",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.65f),
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatRupiah(totalBalance),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                accounts.forEach { (name, balance) ->
                    AccountChip(
                        name = name,
                        balance = balance,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountChip(
    name: String,
    balance: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = name,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatRupiah(balance),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1117)
@Composable
fun BalanceCardPreview() {
    MyFinanceTheme {
        BalanceCard(
            totalBalance = 8450000.0,
            accounts = listOf(
                "Cash" to 450000.0,
                "Bank BCA" to 8000000.0
            )
        )
    }
}