package com.example.myfinance.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.myfinance.ui.theme.TextSecondary

@Composable
fun SectionTitle(
    title: String,
    color: Color = TextSecondary
) {
    Text(
        text = title,
        fontSize = 13.sp,
        color = color
    )
}