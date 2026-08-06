package com.example.myfinance.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myfinance.domain.model.TransactionType

@Composable
fun CategoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember {
        mutableStateOf(TransactionType.EXPENSE.name)
    }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }
    var categoryToEdit by remember { mutableStateOf<CategoryEntity?>(null) }

    if (showAddDialog) {
        AddCategoryDialog(
            viewModel = viewModel,
            defaultType = selectedTab,
            onDismiss = { showAddDialog = false }
        )
    }

    if (categoryToEdit != null) {
        EditCategoryDialog(
            category = categoryToEdit!!,
            viewModel = viewModel,
            onDismiss = {
                categoryToEdit = null
            }
        )
    }

    if (categoryToDelete != null) {
        DeleteCategoryDialog(

            category = categoryToDelete!!,
            viewModel = viewModel,
            onDismiss = { categoryToDelete = null }
        )
    }

    val filteredCategories = categories.filter { it.type == selectedTab }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextMuted
                    )
                }
                Text(
                    text = "Kelola Kategori",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentPurple,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(DarkCard, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("EXPENSE" to "Pengeluaran", "INCOME" to "Pemasukan").forEach { (type, label) ->
                Button(
                    onClick = { selectedTab = type },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == type) AccentPurple else Color.Transparent,
                        contentColor = if (selectedTab == type) Color.White else TextMuted
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(label, fontSize = 13.sp)
                }
            }
        }

        if (filteredCategories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada kategori",
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCategories) { category ->
                    CategoryItem(
                        category = category,
                        onEdit = {
                            categoryToEdit = category
                        },
                        onDelete = {
                            categoryToDelete = category
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (category.type == TransactionType.INCOME.name) IncomeGreen.copy(alpha = 0.15f)
                        else ExpenseRed.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (category.type == TransactionType.INCOME.name) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = null,
                    tint = if (category.type == TransactionType.INCOME.name) IncomeGreen else ExpenseRed,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = AccentPurple,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = ExpenseRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(
    viewModel: CategoryViewModel,
    defaultType: String,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(defaultType) }
    var isLoading by remember { mutableStateOf(false) }
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
                    text = "Tambah Kategori",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Jenis", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBackground, RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("EXPENSE" to "Pengeluaran", "INCOME" to "Pemasukan").forEach { (type, label) ->
                        Button(
                            onClick = { selectedType = type },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == type) AccentPurple else Color.Transparent,
                                contentColor = if (selectedType == type) Color.White else TextMuted
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(label, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Nama Kategori", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Contoh: Olahraga", color = TextMuted) },
                    isError = errorMessage != null,
                    supportingText = {
                        errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentPurple,
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
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
                            val cleanName = name.trim()
                            when {
                                cleanName.isBlank() -> {
                                    errorMessage = "Nama kategori tidak boleh kosong"
                                    return@Button
                                }

                                cleanName.length > 30 -> {
                                    errorMessage = "Maksimal 30 karakter"
                                    return@Button
                                }

                                viewModel.categoryExists(cleanName, selectedType) -> {
                                    errorMessage = "Kategori sudah ada"
                                    return@Button
                                }
                            }

                            scope.launch {
                                isLoading = true
                                viewModel.insertCategory(
                                    CategoryEntity(
                                        name = cleanName,
                                        icon = "category",
                                        type = selectedType,
                                        colorHex =
                                            if (selectedType == TransactionType.INCOME.name)
                                                "#00C896"
                                            else
                                                "#FF5C5C"
                                    )
                                )
                                isLoading = false
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                        enabled = !isLoading && name.isNotBlank()
                    ) {
                        Text("Simpan", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditCategoryDialog(
    category: CategoryEntity,
    viewModel: CategoryViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(category.name) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    DarkCard,
                    RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Edit Kategori",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    supportingText = {
                        errorMessage?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
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
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    ) {
                        Text("Batal")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPurple
                        ),
                        onClick = {
                            val cleanName = name.trim()
                            when {
                                cleanName.isBlank() -> {
                                    errorMessage = "Nama kategori tidak boleh kosong"
                                    return@Button
                                }

                                cleanName.length > 30 -> {
                                    errorMessage = "Maksimal 30 karakter"
                                    return@Button
                                }

                                cleanName.equals(category.name, true).not() && viewModel.categoryExists(
                                    cleanName,
                                    category.type
                                ) -> {
                                    errorMessage = "Kategori sudah ada"
                                    return@Button
                                }
                            }

                            scope.launch {
                                isLoading = true
                                viewModel.updateCategory(
                                    category.copy(
                                        name = cleanName
                                    )
                                )
                                isLoading = false
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteCategoryDialog(
    category: CategoryEntity,
    viewModel: CategoryViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Kategori?", color = TextPrimary) },
        text = {
            Text(
                "Kategori \"${category.name}\" akan dihapus. Transaksi yang menggunakan kategori ini tidak akan terhapus.",
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        viewModel.deleteCategory(category)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
            ) {
                Text("Hapus", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal", color = TextMuted)
            }
        },
        containerColor = DarkCard
    )
}