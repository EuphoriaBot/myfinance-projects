package com.example.myfinance.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatRupiah

@Composable
fun AccountScreen(
    repository: FinanceRepository,
    modifier: Modifier = Modifier
) {
    val accounts by repository.getAllAccounts()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val totalBalance = accounts.sumOf { it.balance }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddAccountDialog(
            repository = repository,
            onDismiss = { showAddDialog = false }
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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Akun",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentPurple,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Akun",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AccentPurple)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Total Saldo",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatRupiah(totalBalance),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Daftar Akun",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (accounts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada akun",
                    fontSize = 14.sp,
                    color = TextMuted
                )
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
                items(accounts) { account ->
                    AccountItem(account = account)
                }
            }
        }
    }
}

@Composable
private fun AccountItem(account: AccountEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkCard)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = account.name,
                    tint = AccentPurple,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = account.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = when (account.type) {
                        "BANK" -> "Bank"
                        "E_WALLET" -> "E-Wallet"
                        else -> "Cash"
                    },
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
        Text(
            text = formatRupiah(account.balance),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}