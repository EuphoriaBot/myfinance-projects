package com.example.myfinance.utils

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DatabasePassphraseManager @Inject constructor(
    private val keystoreManager: KeystoreManager
) {

    companion object {
        private const val PREFS_NAME = "myfinance_secure_prefs"
        private const val PREF_ENCRYPTED_PASSPHRASE = "encrypted_passphrase"
        private const val PREF_IV = "passphrase_iv"
    }

    fun getOrCreateDatabasePassphrase(context: Context): ByteArray {

        val prefs = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val encrypted = prefs.getString(PREF_ENCRYPTED_PASSPHRASE, null)
        val iv = prefs.getString(PREF_IV, null)

        if (encrypted != null && iv != null) {
            return keystoreManager.decrypt(
                Base64.decode(encrypted, Base64.DEFAULT),
                Base64.decode(iv, Base64.DEFAULT)
            )
        }

        val randomPassphrase = Random.nextBytes(32)

        val result = keystoreManager.encrypt(randomPassphrase)

        prefs.edit {
            putString(
                PREF_ENCRYPTED_PASSPHRASE,
                Base64.encodeToString(result.first, Base64.DEFAULT)
            )

            putString(
                PREF_IV,
                Base64.encodeToString(result.second, Base64.DEFAULT)
            )
        }

        return randomPassphrase
    }
}