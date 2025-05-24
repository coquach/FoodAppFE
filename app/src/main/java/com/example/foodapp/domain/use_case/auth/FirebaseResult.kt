package com.example.foodapp.domain.use_case.auth

sealed interface FirebaseResult<out T>{
    data object Loading: FirebaseResult<Nothing>
    data class Success<T>(val data: T) : FirebaseResult<T>
    data class Failure(val error: String) : FirebaseResult<Nothing>
}