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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.myfinance.ui.theme.AccentPurple
import com.example.myfinance.ui.theme.DarkCard
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppLockSettingsScreen(
    onBack: () -> Unit,
    viewModel: PinViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    var isAppLockEnabled by remember {
        mutableStateOf(
            viewModel.isAppLockEnabled(context)
        )
    }

    var showSetPinScreen by remember {
        mutableStateOf(false)
    }

    if (showSetPinScreen) {
        SetPinScreen(
            onPinSet = {
                showSetPinScreen = false
                isAppLockEnabled = true
            }
        )
        return
    }

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

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkCard)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "App Lock",
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Kunci aplikasi menggunakan PIN",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                }

                Switch(
                    checked = isAppLockEnabled,
                    onCheckedChange = { enabled ->

                        viewModel.setAppLockEnabled(
                            context,
                            enabled
                        )

                        isAppLockEnabled = enabled

                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentPurple
                    )
                )

            }

        }

    }

}