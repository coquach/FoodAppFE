package com.example.foodapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

abstract class BaseViewModel() : ViewModel() {
    var error: String = ""
    var errorDescription = ""
    sealed class ResultState{
        data object Nothing : ResultState()
        data object Loading : ResultState()
        data object Success : ResultState()
        data class Error(val message: String?) : ResultState()
    }
}