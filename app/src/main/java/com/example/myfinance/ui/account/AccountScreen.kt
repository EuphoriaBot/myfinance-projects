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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.ui.theme.*
import com.example.myfinance.utils.formatRupiah
import com.example.myfinance.ui.components.BackgroundPattern
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MenuAnchorType
import com.example.myfinance.utils.formatInputNumber
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var accountToEdit by remember {
        mutableStateOf<AccountEntity?>(null)
    }
    var accountToDelete by remember {
        mutableStateOf<AccountEntity?>(null)
    }

    if (showAddDialog) {
        AddAccountDialog(
            viewModel = viewModel,
            onSave = { account ->
                viewModel.insertAccount(account)
                showAddDialog = false
            },
            onDismiss = {
                showAddDialog = false
            }
        )
    }

    if (accountToEdit != null) {

        EditAccountDialog(
            viewModel = viewModel,
            account = accountToEdit!!,
            onDismiss = {
                accountToEdit = null
            },
            onSave = { updatedAccount ->
                viewModel.updateAccount(updatedAccount)
                accountToEdit = null
            }
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
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Akun",
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
                        contentDescription = "Tambah Akun",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (accountToDelete != null) {

                AlertDialog(
                    onDismissRequest = {
                        accountToDelete = null
                    },
                    title = {
                        Text("Hapus akun?")
                    },
                    text = {
                        Text(
                            "Akun ${accountToDelete!!.name} akan disembunyikan."
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.softDeleteAccount(
                                    accountToDelete!!
                                )
                                accountToDelete = null
                            }
                        ) {
                            Text("Hapus")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                accountToDelete = null
                            }
                        ) {
                            Text("Batal")
                        }
                    }
                )
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
                        AccountItem(
                            account = account,
                            onEdit = {
                                accountToEdit = account
                            },
                            onDelete = {
                                accountToDelete = account
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountItem(
    account: AccountEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatRupiah(account.balance),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = AccentPurple
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = ExpenseRed
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditAccountDialog(
    viewModel: AccountViewModel,
    account: AccountEntity,
    onDismiss: () -> Unit,
    onSave: (AccountEntity) -> Unit
) {

    var accountName by remember {
        mutableStateOf(account.name)
    }

    var accountBalance by remember {
        mutableStateOf(account.balance.toLong().toString())
    }

    var accountType by remember {
        mutableStateOf(account.type)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Akun")
        },

        text = {
            Column {
                OutlinedTextField(
                    value = accountName,
                    onValueChange = {
                        accountName = it
                        errorMessage = null
                    },
                    isError = errorMessage != null,
                    supportingText = {
                        errorMessage?.let {
                            Text(it)
                        }
                    },
                    label = {
                        Text("Nama Akun")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = if (accountBalance.isEmpty()) "" else formatInputNumber(accountBalance),
                    onValueChange = {
                        accountBalance = it
                            .replace(".", "")
                            .filter(Char::isDigit)
                    },
                    label = {
                        Text("Saldo")
                    },
                    prefix = {
                        Text("Rp ")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                var expanded by remember {
                    mutableStateOf(false)
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {

                    OutlinedTextField(
                        value = accountType,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Tipe")
                        },
                        modifier = Modifier.menuAnchor(
                            MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        listOf(
                            "CASH",
                            "BANK",
                            "E_WALLET"
                        ).forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(type)
                                },
                                onClick = {
                                    accountType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {
                    val cleanName = accountName.trim()
                    when {
                        cleanName.isBlank() -> {
                            errorMessage = "Nama akun tidak boleh kosong"
                            return@Button
                        }

                        cleanName.length > 30 -> {
                            errorMessage = "Maksimal 30 karakter"
                            return@Button
                        }

                        viewModel.accountExists(
                            cleanName,
                            excludeId = account.id
                        ) -> {
                            errorMessage = "Nama akun sudah digunakan"
                            return@Button
                        }
                    }

                    onSave(
                        account.copy(
                            name = cleanName,
                            balance = accountBalance.toDoubleOrNull() ?: 0.0,
                            type = accountType
                        )
                    )
                }
            ) {
                Text("Simpan")
            }
        },

        dismissButton = {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Batal")
            }
        }
    )
}