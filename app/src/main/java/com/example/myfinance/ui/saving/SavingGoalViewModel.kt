package com.example.myfinance.ui.saving

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinance.data.local.entity.SavingGoalEntity
import com.example.myfinance.data.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingGoalViewModel @Inject constructor(
    private val repository: FinanceRepository
) : ViewModel() {

    val goals: StateFlow<List<SavingGoalEntity>> = repository.getAllSavingGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertGoal(goal: SavingGoalEntity) {
        viewModelScope.launch {
            repository.insertSavingGoal(goal)
        }
    }

    fun updateGoal(goal: SavingGoalEntity) {
        viewModelScope.launch {
            repository.updateSavingGoal(goal)
        }
    }

    fun deleteGoal(goal: SavingGoalEntity) {
        viewModelScope.launch {
            repository.deleteSavingGoal(goal)
        }
    }
}