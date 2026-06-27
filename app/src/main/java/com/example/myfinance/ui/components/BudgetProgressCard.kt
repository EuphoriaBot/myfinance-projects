package com.example.myfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.myfinance.utils.formatRupiah

data class BudgetUiModel(
    val categoryName: String,
    val spent: Double,
    val limit: Double
)

@Composable
fun BudgetProgressCard(
    budgets: List<BudgetUiModel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        budgets.forEach { budget ->
            BudgetProgressItem(budget = budget)
        }
    }
}

@Composable
private fun BudgetProgressItem(
    budget: BudgetUiModel
) {
    val progress = (budget.spent / budget.limit).coerceIn(0.0, 1.0).toFloat()

    val barColor = when {
        progress >= 1f -> ExpenseRed
        progress >= 0.8f -> WarningAmber
        else -> IncomeGreen
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = budget.categoryName,
                fontSize = 12.sp,
                color = TextPrimary
            )
            Text(
                text = "${formatRupiah(budget.spent)} / ${formatRupiah(budget.limit)}",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = Color.White.copy(alpha = 0.07f)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF0F1117
)
@Composable
fun BudgetProgressCardPreview() {
    MyFinanceTheme {
        BudgetProgressCard(
            budgets = listOf(
                BudgetUiModel("Makan & Minum", 680000.0, 1000000.0),
                BudgetUiModel("Transportasi", 420000.0, 500000.0),
                BudgetUiModel("Hiburan", 310000.0, 300000.0)
            )
        )
    }
}