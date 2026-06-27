package com.example.myfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatRupiah

@Composable
fun IncomeExpenseRow(
    totalIncome: Double,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IncomeExpenseCard(
            label = "Pemasukan",
            amount = totalIncome,
            icon = Icons.Default.KeyboardArrowUp,
            iconBackgroundColor = IncomeGreen.copy(alpha = 0.15f),
            iconColor = IncomeGreen,
            amountColor = IncomeGreen,
            modifier = Modifier.weight(1f)
        )
        IncomeExpenseCard(
            label = "Pengeluaran",
            amount = totalExpense,
            icon = Icons.Default.KeyboardArrowDown,
            iconBackgroundColor = ExpenseRed.copy(alpha = 0.15f),
            iconColor = ExpenseRed,
            amountColor = ExpenseRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun IncomeExpenseCard(
    label: String,
    amount: Double,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconColor: Color,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatRupiah(amount),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = amountColor
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF0F1117
)
@Composable
fun IncomeExpenseRowPreview() {
    MyFinanceTheme {
        IncomeExpenseRow(
            totalIncome = 5200000.0,
            totalExpense = 2750000.0
        )
    }
}