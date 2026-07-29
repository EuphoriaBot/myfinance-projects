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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.clickable

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

    var isPinSet by remember {
        mutableStateOf(
            viewModel.isPinSet(context)
        )
    }

    var showSetPinScreen by remember {
        mutableStateOf(false)
    }

    var selectedAutoLock by remember {
        mutableStateOf("Immediately")
    }

    var showAutoLockDialog by remember {
        mutableStateOf(false)
    }

    var showVerifyDisablePin by remember {
        mutableStateOf(false)
    }

    var showChangePinScreen by remember {
        mutableStateOf(false)
    }

    var showDisableDialog by remember {
        mutableStateOf(false)
    }

    if (showSetPinScreen) {
        SetPinScreen(
            onPinSet = {
                isPinSet = true
                viewModel.setAppLockEnabled(
                    context,
                    true
                )
                isAppLockEnabled = true
                showSetPinScreen = false
            }
        )
        return
    }

    if (showVerifyDisablePin) {
        PinScreen(
            title = "Verifikasi PIN",
            subtitle = "Masukkan PIN untuk menonaktifkan App Lock",
            onPinComplete = { pin ->
                if (viewModel.verifyPin(context, pin)) {
                    viewModel.clearPin(context)
                    isPinSet = false
                    viewModel.setAppLockEnabled(
                        context,
                        false
                    )
                    isAppLockEnabled = false
                    showVerifyDisablePin = false
                }
            }
        )
        return
    }

    if (showAutoLockDialog) {
        val options = listOf(
            "Immediately",
            "30 seconds",
            "1 minute",
            "5 minutes",
            "Never"
        )

        AlertDialog(
            onDismissRequest = {
                showAutoLockDialog = false
            },
            title = {
                Text("Auto Lock After")
            },
            text = {
                Column {
                    options.forEach { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedAutoLock = option
                                    showAutoLockDialog = false
                                }
                                .padding(vertical = 12.dp),
                            color = TextPrimary
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showDisableDialog) {
        AlertDialog(
            onDismissRequest = {
                showDisableDialog = false
            },
            title = {
                Text("Nonaktifkan App Lock?")
            },
            text = {
                Text(
                    "Masukkan PIN untuk menonaktifkan App Lock."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDisableDialog = false
                        showVerifyDisablePin = true
                    }
                ) {
                    Text("Lanjut")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDisableDialog = false
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    if (showChangePinScreen) {
        SetPinScreen(
            onPinSet = {
                showChangePinScreen = false
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
                        if (enabled) {
                            if (viewModel.isPinSet(context)) {
                                viewModel.setAppLockEnabled(
                                    context,
                                    true
                                )
                                isAppLockEnabled = true
                            } else {
                                showSetPinScreen = true

                            }
                        } else {
                            showDisableDialog = true
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentPurple
                    )
                )
            }

            if (isPinSet) {

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.05f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showAutoLockDialog = true
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Auto Lock After",
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = selectedAutoLock,
                            color = TextMuted,
                            fontSize = 12.sp
                        )

                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextMuted
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Ubah PIN",
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Ganti PIN aplikasi",
                            color = TextMuted,
                            fontSize = 12.sp
                        )

                    }

                    IconButton(
                        onClick = {
                            showChangePinScreen = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                }
            }
        }
    }
}