package com.example.myfinance.utils

import java.text.NumberFormat
import java.util.Locale

private val indonesiaFormatter = NumberFormat.getNumberInstance(
    Locale("id", "ID")
).apply {
    maximumFractionDigits = 0
}

fun formatRupiah(amount: Double): String {
    return "Rp ${indonesiaFormatter.format(amount)}"
}

fun formatInputNumber(input: String): String {
    if (input.isBlank()) return ""

    return indonesiaFormatter.format(input.toLong())
}