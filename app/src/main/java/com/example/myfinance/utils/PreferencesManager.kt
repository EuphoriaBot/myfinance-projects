package com.example.myfinance.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val ONBOARDING_COMPLETED =
            booleanPreferencesKey("onboarding_completed")
    }

    val isOnboardingCompleted: Flow<Boolean> =
        dataStore.data.map {
            it[ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted(
        completed: Boolean
    ) {
        dataStore.edit {
            it[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun clearAll() {
        dataStore.edit {
            it.clear()
        }
    }
}