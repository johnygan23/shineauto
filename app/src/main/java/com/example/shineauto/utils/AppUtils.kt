package com.example.shineauto.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppUtils {
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Helper to check if a date string is in the past
    fun isDateInPast(dateString: String): Boolean {
        val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        return try {
            val date = sdf.parse(dateString)
            val today = Date()
            date != null && date.before(today)
        } catch (e: Exception) {
            false
        }
    }
}