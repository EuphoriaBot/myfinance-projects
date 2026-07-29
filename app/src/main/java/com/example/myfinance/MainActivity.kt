package com.example.myfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myfinance.ui.home.HomeScreen
import com.example.myfinance.ui.theme.DarkBackground
import com.example.myfinance.ui.theme.MyFinanceTheme
import com.example.myfinance.data.worker.RecurringTransactionWorker
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfinance.ui.main.MainViewModel
import com.example.myfinance.ui.onboarding.OnboardingScreen
import com.example.myfinance.ui.pin.PinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.myfinance.ui.pin.PinScreen
import com.example.myfinance.ui.pin.PinLockState
import timber.log.Timber
import androidx.activity.viewModels

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val pinViewModel: PinViewModel by viewModels()
    private var lastPausedTime: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        RecurringTransactionWorker.schedule(this)
        setContent {
            val lockState by pinViewModel.lockState.collectAsState()
            val pinError by pinViewModel.pinError.collectAsState()
            val isPinLocked by pinViewModel.isPinLocked.collectAsState()
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                pinViewModel.checkInitialState(context)
            }
            val viewModel: MainViewModel = hiltViewModel()
            val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()
            MyFinanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    if (!onboardingCompleted) {
                        OnboardingScreen()
                    } else {
                        if (lockState is PinLockState.Locked) {
                            PinScreen(
                                title = "Masukkan PIN",
                                error = pinError,
                                isLocked = isPinLocked,
                                onPinChanged = {
                                    pinViewModel.clearPinError()
                                },
                                onPinComplete = { pin ->
                                    pinViewModel.verifyPin(
                                        context,
                                        pin
                                    )
                                }
                            )
                        } else {
                            HomeScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        lastPausedTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        val elapsed = System.currentTimeMillis() - lastPausedTime
        val autoLockMinutes =
            pinViewModel.getAutoLockMinutes(this)
        when {
            autoLockMinutes == -1 -> {
                Timber.d("Auto Lock: Never")
                return
            }

            autoLockMinutes == 0 -> {
                if (lastPausedTime != 0L) {
                    Timber.d("Auto Lock: Immediately")
                    pinViewModel.lockApp()
                }
            }

            lastPausedTime != 0L &&
                    elapsed >= autoLockMinutes * 60 * 1000L -> {

                Timber.d("Auto Lock: Time reached")
                pinViewModel.lockApp()
            }
        }
        Timber.d("Elapsed: %d ms", elapsed)
    }
}