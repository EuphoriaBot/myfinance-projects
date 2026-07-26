package com.example.myfinance.ui.pin

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SetPinScreen(
    onPinSet: () -> Unit,
    onSkip: (() -> Unit)? = null,
    viewModel: PinViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(SetPinStep.ENTER_PIN) }
    var firstPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    when (step) {
        SetPinStep.ENTER_PIN -> {
            PinScreen(
                title = "Buat PIN Baru",
                subtitle = "PIN digunakan untuk mengunci aplikasi",
                error = error,
                onPinComplete = { pin ->
                    firstPin = pin
                    error = null
                    step = SetPinStep.CONFIRM_PIN
                },
                onForgotPin = onSkip
            )
        }

        SetPinStep.CONFIRM_PIN -> {
            PinScreen(
                title = "Konfirmasi PIN",
                subtitle = "Masukkan PIN yang sama",
                error = error,
                onPinComplete = { pin ->
                    if (pin == firstPin) {
                        viewModel.setPin(context, pin)
                        onPinSet()
                    } else {
                        error = "PIN tidak cocok, coba lagi"
                        step = SetPinStep.ENTER_PIN
                        firstPin = ""
                    }
                }
            )
        }
    }
}

enum class SetPinStep {
    ENTER_PIN,
    CONFIRM_PIN
}