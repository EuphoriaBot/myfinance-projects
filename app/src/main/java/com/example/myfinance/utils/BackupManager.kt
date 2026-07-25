package com.example.myfinance.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val DATABASE_NAME = "myfinance_database"
        private const val BACKUP_FOLDER = "MyFinance"
    }

    fun createBackup(): Boolean {
        val backupName = buildBackupFileName()
        val uri = createBackupUri(backupName) ?: return false

        val databaseFiles = listOf(
            context.getDatabasePath(DATABASE_NAME),
            context.getDatabasePath("$DATABASE_NAME-wal"),
            context.getDatabasePath("$DATABASE_NAME-shm")
        )

        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                ZipOutputStream(BufferedOutputStream(outputStream)).use { zipStream ->
                    databaseFiles.forEach { file ->
                        if (file.exists()) {
                            zipStream.putNextEntry(ZipEntry(file.name))
                            file.inputStream().use { input ->
                                input.copyTo(zipStream)
                            }
                            zipStream.closeEntry()
                        }
                    }
                }
            } ?: return false

            true
        } catch (_: Exception) {
            context.contentResolver.delete(uri, null, null)
            false
        }
    }

    private fun buildBackupFileName(): String {
        val timestamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date())

        return "MyFinance_Backup_$timestamp.myfinance"
    }

    private fun createBackupUri(fileName: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createBackupUriApi29(fileName)
        } else {
            createLegacyBackupFile(fileName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createBackupUriApi29(fileName: String): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS + "/$BACKUP_FOLDER"
            )
        }

        return context.contentResolver.insert(
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            values
        )
    }

    @Suppress("DEPRECATION")
    private fun createLegacyBackupFile(fileName: String): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )
        val folder = File(downloadsDir, BACKUP_FOLDER)
        if (!folder.exists()) folder.mkdirs()

        return Uri.fromFile(File(folder, fileName))
    }
}