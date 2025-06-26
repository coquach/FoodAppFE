package com.example.foodapp.data.dto

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

sealed class ApiResponse<out T> {
    data object Loading: ApiResponse<Nothing>()

    data class Success<out T>(
        val data: T
    ): ApiResponse<T>()

    data class Failure(
        val errorMessage: String,
        val code: Int,
    ): ApiResponse<Nothing>()
}

data class ErrorResponse(
    val code: Int,
    val message: String
)

fun<T> apiRequestFlow(call: suspend () -> Response<T>): Flow<ApiResponse<T>> = flow {
    emit(ApiResponse.Loading)

    withTimeoutOrNull(30000L) {
        val response = call()

        try {
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(ApiResponse.Success(body))
                } else {
                    @Suppress("UNCHECKED_CAST")
                    emit(ApiResponse.Success(Unit as T))
                }
            } else {
                response.errorBody()?.charStream()?.use { reader ->
                    val parsedError: ErrorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
                    emit(ApiResponse.Failure(parsedError.message, parsedError.code))
                }
            }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: "Unexpected error", 400))
        }
    } ?: emit(ApiResponse.Failure("Timeout! Please try again.", 408))
}.flowOn(Dispatchers.IO)