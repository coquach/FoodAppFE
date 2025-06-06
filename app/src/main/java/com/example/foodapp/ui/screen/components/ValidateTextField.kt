package com.example.foodapp.ui.screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun ValidateTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String?=null,
    errorMessage: String?,
    validate: () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
){
    val focusManager = LocalFocusManager.current
    var isTouched by remember { mutableStateOf(false) }
    FoodAppTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            if (!isTouched) isTouched = true
        },
        labelText = labelText,
        isError = errorMessage != null,
        errorText = errorMessage,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (isTouched && !focusState.isFocused) {
                    validate()
                }
            },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                validate()
            }
        ),
        singleLine = false,
        maxLines = maxLines
    )
}

fun validateField(
    value: String,
    errorMessage: String,
    condition: (String) -> Boolean
): String? {
    return if (condition(value)) null else errorMessage
}
