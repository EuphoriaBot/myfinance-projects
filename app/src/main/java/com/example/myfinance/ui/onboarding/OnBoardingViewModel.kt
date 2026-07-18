package com.example.myfinance.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.AccountEntity
import com.example.myfinance.data.repository.FinanceRepository
import com.example.myfinance.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun finishOnboarding(
        cashBalance: Double,
        bankName: String,
        bankBalance: Double
    ) {
        viewModelScope.launch {

            repository.insertAccount(
                AccountEntity(
                    name = "Cash",
                    balance = cashBalance,
                    type = "CASH",
                    colorHex = "#00C896"
                )
            )

            repository.insertAccount(
                AccountEntity(
                    name = bankName,
                    balance = bankBalance,
                    type = "BANK",
                    colorHex = "#6C63FF"
                )
            )

            preferencesManager.setOnboardingCompleted(true)
        }
    }
}