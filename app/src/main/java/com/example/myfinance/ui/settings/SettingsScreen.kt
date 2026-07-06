package com.example.myfinance.ui.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.exportTransactionsToCsv
import com.example.myfinance.utils.shareFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    repository: FinanceRepository,
    onNavigateToGoals: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {},
    onNavigateToReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            repository = repository,
            onDismiss = { showAddBudgetDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Setelan",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            SettingsSection(title = "Keuangan") {
                SettingsItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Kelola Akun",
                    subtitle = "Lihat dan tambah akun",
                    onClick = onNavigateToAccount
                )
                SettingsItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Tambah Budget",
                    subtitle = "Atur limit pengeluaran per kategori",
                    onClick = { showAddBudgetDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Savings,
                    title = "Target Nabung",
                    subtitle = "Kelola goal tabungan kamu",
                    onClick = onNavigateToGoals
                )
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    title = "Laporan",
                    subtitle = "Lihat ringkasan keuangan bulanan",
                    onClick = onNavigateToReport
                )
            }
        }

        item {
            SettingsSection(title = "Data") {
                SettingsItem(
                    icon = Icons.Default.FileDownload,
                    title = "Export ke CSV",
                    subtitle = "Simpan semua transaksi ke file CSV",
                    onClick = {
                        scope.launch {
                            isExporting = true
                            val transactions = repository.getAllTransactions().first()
                            val accounts = repository.getAllAccounts().first()
                            val categories = repository.getAllCategories().first()
                            val uri = exportTransactionsToCsv(
                                context, transactions, accounts, categories
                            )
                            if (uri != null) shareFile(context, uri)
                            isExporting = false
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Privasi",
                    subtitle = "Data tersimpan lokal di perangkat kamu"
                )
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Versi Aplikasi",
                    subtitle = "1.0.0"
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkCard)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AccentPurple.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AccentPurple,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}