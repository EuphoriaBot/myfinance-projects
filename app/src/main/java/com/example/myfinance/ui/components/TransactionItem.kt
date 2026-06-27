package com.example.myfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.domain.model.TransactionType
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatRupiah

data class TransactionUiModel(
    val id: Long,
    val title: String,
    val categoryName: String,
    val accountName: String,
    val amount: Double,
    val type: TransactionType,
    val dateLabel: String
)

@Composable
fun TransactionItem(
    transaction: TransactionUiModel,
    modifier: Modifier = Modifier
) {
    val (amountColor, amountPrefix, icon) = when (transaction.type) {
        TransactionType.INCOME -> Triple(IncomeGreen, "+", Icons.Default.ArrowUpward)
        TransactionType.EXPENSE -> Triple(ExpenseRed, "-", Icons.Default.ArrowDownward)
        TransactionType.TRANSFER -> Triple(TextSecondary, "", Icons.Default.SwapHoriz)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = transaction.type.name,
                tint = amountColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${transaction.categoryName} · ${transaction.accountName} · ${transaction.dateLabel}",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Text(
            text = "$amountPrefix${formatRupiah(transaction.amount)}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = amountColor
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF0F1117
)
@Composable
fun TransactionItemPreview() {
    MyFinanceTheme {
        Column {
            TransactionItem(
                transaction = TransactionUiModel(
                    id = 1,
                    title = "Belanja Alfamart",
                    categoryName = "Makan & Minum",
                    accountName = "Cash",
                    amount = 87000.0,
                    type = TransactionType.EXPENSE,
                    dateLabel = "Hari ini"
                )
            )
            TransactionItem(
                transaction = TransactionUiModel(
                    id = 2,
                    title = "Gaji Juni",
                    categoryName = "Pemasukan",
                    accountName = "Bank BCA",
                    amount = 5200000.0,
                    type = TransactionType.INCOME,
                    dateLabel = "25 Jun"
                )
            )
            TransactionItem(
                transaction = TransactionUiModel(
                    id = 3,
                    title = "Isi Gopay",
                    categoryName = "Transfer",
                    accountName = "Bank BCA",
                    amount = 200000.0,
                    type = TransactionType.TRANSFER,
                    dateLabel = "Kemarin"
                )
            )
        }
    }
}