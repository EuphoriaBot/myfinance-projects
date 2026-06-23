package com.example.myfinance.utils

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return "Rp ${format.format(amount)}"
}