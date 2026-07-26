package com.example.myfinance.utils

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinManager @Inject constructor(
    private val keystoreManager: KeystoreManager
) {
    companion object {
        private const val PREFS_NAME = "myfinance_pin_prefs"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_PIN_IV = "pin_iv"
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_AUTO_LOCK_MINUTES = "auto_lock_minutes"
    }

    fun isAppLockEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_APP_LOCK_ENABLED, false)
    }

    fun isPinSet(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PIN_HASH, null) != null
    }

    fun getAutoLockMinutes(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_AUTO_LOCK_MINUTES, 1)
    }

    fun setAutoLockMinutes(context: Context, minutes: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putInt(KEY_AUTO_LOCK_MINUTES, minutes) }
    }

    fun setPin(context: Context, pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        val encrypted = keystoreManager.encrypt(hash)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_PIN_SALT, Base64.encodeToString(salt, Base64.DEFAULT))
            putString(KEY_PIN_HASH, Base64.encodeToString(encrypted.first, Base64.DEFAULT))
            putString(KEY_PIN_IV, Base64.encodeToString(encrypted.second, Base64.DEFAULT))
            putBoolean(KEY_APP_LOCK_ENABLED, true)
        }
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saltStr = prefs.getString(KEY_PIN_SALT, null) ?: return false
        val hashStr = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val ivStr = prefs.getString(KEY_PIN_IV, null) ?: return false

        val salt = Base64.decode(saltStr, Base64.DEFAULT)
        val encryptedHash = Base64.decode(hashStr, Base64.DEFAULT)
        val iv = Base64.decode(ivStr, Base64.DEFAULT)

        val decryptedHash = keystoreManager.decrypt(encryptedHash, iv)
        val inputHash = hashPin(pin, salt)

        return decryptedHash.contentEquals(inputHash)
    }

    fun setAppLockEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_APP_LOCK_ENABLED, enabled) }
    }

    fun clearPin(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            remove(KEY_PIN_HASH)
            remove(KEY_PIN_SALT)
            remove(KEY_PIN_IV)
            putBoolean(KEY_APP_LOCK_ENABLED, false)
        }
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(32)
        SecureRandom().nextBytes(salt)
        return salt
    }

    private fun hashPin(pin: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        return digest.digest(pin.toByteArray())
    }
}