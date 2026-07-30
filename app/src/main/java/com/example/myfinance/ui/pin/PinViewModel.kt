package com.example.myfinance.ui.pin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.utils.PinManager
import com.example.myfinance.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


sealed class PinLockState {
    object Locked : PinLockState()
    object Unlocked : PinLockState()
}

@HiltViewModel
class PinViewModel @Inject constructor(
    private val pinManager: PinManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _lockState = MutableStateFlow<PinLockState>(PinLockState.Locked)
    val lockState: StateFlow<PinLockState> = _lockState.asStateFlow()

    private val _pinError = MutableStateFlow<String?>(null)
    val pinError: StateFlow<String?> = _pinError.asStateFlow()

    private val _failedAttempts = MutableStateFlow(0)

    private val _isPinLocked = MutableStateFlow(false)
    val isPinLocked = _isPinLocked.asStateFlow()

    private val _lockUntil = MutableStateFlow<Long?>(null)

    fun checkInitialState(context: Context) {
        val isEnabled = pinManager.isAppLockEnabled(context)
        val isPinSet = pinManager.isPinSet(context)

        _lockState.value = when {
            isEnabled && isPinSet -> PinLockState.Locked
            else -> PinLockState.Unlocked
        }
    }

    fun verifyPin(
        context: Context,
        pin: String
    ): Boolean {
        val correct = pinManager.verifyPin(
            context,
            pin
        )
        if (correct) {
            _failedAttempts.value = 0
            _pinError.value = null
            _lockState.value = PinLockState.Unlocked
        } else {
            _failedAttempts.value++
            if (_failedAttempts.value >= 3) {
                _isPinLocked.value = true
                _lockUntil.value =
                    System.currentTimeMillis() + 30_000L
                viewModelScope.launch {
                    delay(30.seconds)
                    _isPinLocked.value = false
                    _failedAttempts.value = 0
                    _lockUntil.value = null
                    _pinError.value = null
                }
            }
            _pinError.value = "PIN salah"
        }
        return correct
    }

    fun setPin(context: Context, pin: String) {
        pinManager.setPin(context, pin)
    }

    fun clearPin(context: Context) {
        pinManager.clearPin(context)
    }

    fun isAppLockEnabled(context: Context): Boolean {
        return pinManager.isAppLockEnabled(context)
    }

    fun isPinSet(context: Context): Boolean {
        return pinManager.isPinSet(context)
    }

    fun setAppLockEnabled(context: Context, enabled: Boolean) {
        pinManager.setAppLockEnabled(context, enabled)
    }

    fun getAutoLockMinutes(context: Context): Int {
        return pinManager.getAutoLockMinutes(context)
    }

    fun setAutoLockMinutes(context: Context, minutes: Int) {
        pinManager.setAutoLockMinutes(context, minutes)
    }

    fun clearPinError() {
        _pinError.value = null
    }

    fun lockApp() {
        _lockState.value = PinLockState.Locked
        _pinError.value = null
    }

    fun resetAllAndClearPin(context: Context) {
        viewModelScope.launch {
            pinManager.clearPin(context)
            preferencesManager.clearAll()
            _lockState.value = PinLockState.Unlocked
        }
    }
}