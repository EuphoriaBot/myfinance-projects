package com.example.myfinance.utils

import com.example.myfinance.domain.model.TransactionType

fun String.toTransactionType(): TransactionType =
    TransactionType.valueOf(this)

fun TransactionType.toDbValue(): String =
    name