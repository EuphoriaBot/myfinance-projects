package com.example.myfinance.ui.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.ui.theme.DarkBackground
import com.example.myfinance.ui.theme.TextMuted
import com.example.myfinance.ui.theme.TextPrimary

@Composable
fun AppLockSettingsScreen(
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 4.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = TextMuted
                )
            }

            Text(
                text = "App Lock",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

        }

    }

}