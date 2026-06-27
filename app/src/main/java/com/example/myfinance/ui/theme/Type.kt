package com.example.myfinance.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = TextSecondary
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = TextMuted
    )
)