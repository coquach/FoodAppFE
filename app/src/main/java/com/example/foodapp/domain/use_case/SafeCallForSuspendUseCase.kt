package com.example.foodapp.domain.use_case

suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(e)
    }
}