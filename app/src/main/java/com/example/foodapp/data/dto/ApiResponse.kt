package com.example.foodapp.data.dto

import android.util.Log
import retrofit2.Response

sealed class ApiResponse<out T> {
    data class Success<out T>(val body: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>() {
        fun formatMsg(): String {
            return "Error: $code  $message"
        }
    }

    data class Exception(val exception: kotlin.Exception) : ApiResponse<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResponse<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResponse.Success(body)
            } else {
                ApiResponse.Error(response.code(), "Successful response with unexpectedly empty body")
            }
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown Error (Code: ${response.code()})"
            ApiResponse.Error(response.code(), errorMessage)
        }
    } catch (e: Exception) {
        Log.d("Api Exception: ", e.toString())
        ApiResponse.Exception(e)

    }
}