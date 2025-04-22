package com.example.foodapp.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow

fun ValidateField(
    value: String,
    errorState: MutableState<String?>,
    errorMessage: String,
    condition: (String) -> Boolean
): Boolean {
    return if (condition(value)) {
        errorState.value = null
        true
    } else {
        errorState.value = errorMessage
        false
    }
}
