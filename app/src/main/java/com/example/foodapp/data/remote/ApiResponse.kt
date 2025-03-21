package com.example.foodapp.data.remote

import retrofit2.Response

sealed class ApiResponse<out T> {
    abstract val status: Int
    abstract val message: String

    data class Success<out T>(
        override val status: Int,
        override val message: String,
        val data: T?
    ) : ApiResponse<T>()

    data class Error(
        override val status: Int,
        override val message: String
    ) : ApiResponse<Nothing>() {
        fun formatMsg(): String = "Error: $status $message"
    }
}


suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): ApiResponse<T> {
    return try {
        val response = apiCall.invoke()
        if (response.isSuccessful) {
            response.body() ?: ApiResponse.Error(response.code(), "Empty response")
        } else {
            ApiResponse.Error(response.code(), response.errorBody()?.string() ?: "Unknown Error")
        }
    } catch (e: Exception) {
        ApiResponse.Error(0, e.message ?: "Unexpected error")
    }
}