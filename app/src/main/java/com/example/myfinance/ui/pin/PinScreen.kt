package com.example.myfinance.ui.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.ui.theme.*

@Composable
fun PinScreen(
    title: String = "Masukkan PIN",
    subtitle: String = "",
    onPinComplete: (String) -> Unit,
    onForgotPin: (() -> Unit)? = null,
    error: String? = null,
    isLocked: Boolean = false,
    onPinChanged: (() -> Unit)? = null
) {
    var pin by remember { mutableStateOf("") }
    val maxPin = 6

    LaunchedEffect(pin) {
        if (pin.length == maxPin) {
            onPinComplete(pin)
            pin = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "MyFinance",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentPurple
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
                if (error != null) {
                    Text(
                        text = error,
                        fontSize = 13.sp,
                        color = ExpenseRed
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(maxPin) { index ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < pin.length) AccentPurple
                                else Color.White.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "⌫")
                )

                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { key ->
                            PinKey(
                                key = key,
                                enabled = !isLocked,
                                onClick = {
                                    when (key) {
                                        "⌫" -> if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                        "" -> Unit
                                        else -> {
                                            if (pin.length < maxPin) {
                                                pin += key
                                                onPinChanged?.invoke()
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (onForgotPin != null) {
                Text(
                    text = "Lupa PIN? Reset Aplikasi",
                    fontSize = 13.sp,
                    color = AccentPurple,
                    modifier = Modifier.clickable { onForgotPin() }
                )
            }
        }
    }
}

@Composable
private fun PinKey(
    key: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                if (key.isEmpty()) Color.Transparent
                else DarkCard
            )
            .then(
                if (key.isNotEmpty() && enabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (key == "⌫") {
            Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Hapus",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        } else if (key.isNotEmpty()) {
            Text(
                text = key,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}