package com.example.myfinance.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.local.entity.CategoryEntity
import com.example.myfinance.data.local.entity.TransactionEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun exportTransactionsToCsv(
    context: Context,
    transactions: List<TransactionEntity>,
    accounts: List<AccountEntity>,
    categories: List<CategoryEntity>
): Uri? {
    return try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID"))
        val fileName = "myfinance_export_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        FileWriter(file).use { writer ->
            // Header
            writer.append("Tanggal,Jenis,Kategori,Akun,Jumlah,Catatan\n")

            transactions.forEach { transaction ->
                val date = dateFormat.format(Date(transaction.date))
                val type = when (transaction.type) {
                    "INCOME" -> "Pemasukan"
                    "EXPENSE" -> "Pengeluaran"
                    "TRANSFER" -> "Transfer"
                    else -> transaction.type
                }
                val category = categories.find { it.id == transaction.categoryId }?.name ?: "-"
                val account = accounts.find { it.id == transaction.accountId }?.name ?: "-"
                val amount = transaction.amount.toLong()
                val note = transaction.note.replace(",", " ")

                writer.append("$date,$type,$category,$account,$amount,$note\n")
            }
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun shareFile(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Export Data MyFinance"))
}