package com.example.myfinance

import android.app.Application
import com.example.myfinance.data.local.database.AppDatabase

class AppApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
}