package com.example.myfinance.utils

import android.util.Log
import com.example.myfinance.BuildConfig

object AppLogger {
    private const val TAG = "MyFinance"

    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) Log.e(TAG, message, throwable)
    }

    fun w(message: String) {
        if (BuildConfig.DEBUG) Log.w(TAG, message)
    }
}