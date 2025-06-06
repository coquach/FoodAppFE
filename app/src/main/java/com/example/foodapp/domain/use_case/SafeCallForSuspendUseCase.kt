package com.example.foodapp.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
    return try {
        val result = withContext(Dispatchers.IO) {
            block()
        }
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}