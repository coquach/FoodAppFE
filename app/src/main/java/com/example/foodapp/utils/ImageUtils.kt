package com.example.foodapp.utils

import android.content.Context
import android.net.Uri
import java.io.File

object ImageUtils {

    /**
     * Chuyển Uri thành File
     */
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "selected_image.jpg")
            file.outputStream().use { output -> inputStream.copyTo(output) }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Chuyển Uri thành ByteArray (nếu muốn upload dưới dạng dữ liệu nhị phân)
     */
    fun getBytesFromUri(context: Context, uri: Uri): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            inputStream.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
