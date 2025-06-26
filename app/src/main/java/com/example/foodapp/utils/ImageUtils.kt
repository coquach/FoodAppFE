package com.example.foodapp.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID

object ImageUtils {
    fun getImagePart(context: Context, imageUrl: Uri?, partName: String = "images"): MultipartBody.Part? {
        val imageFile = if (imageUrl != null && !imageUrl.toString().startsWith("http")) {
            getFileFromUri(context, imageUrl)
        } else {
            null
        }

        return imageFile?.toMultipartBodyPartOrNull(partName = partName)
    }

    /**
     * Chuyển Uri thành File
     */
    private fun getFileFromUri(context: Context, uri: Uri): File {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Unable to open input stream from URI.")

            val file = File.createTempFile(
                "temp-${System.currentTimeMillis()}-foodapp",
                ".jpg",
                context.cacheDir
            )

            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to create file from URI", e)
        }
    }
    private fun File?.toMultipartBodyPartOrNull(partName: String = "images"): MultipartBody.Part? {
        return this?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, it.name, requestFile)
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

    fun createImageUri(context: Context): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }


}
