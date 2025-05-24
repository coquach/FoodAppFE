package com.example.foodapp.ui.screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foodapp.R

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    validate: () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
){
    var showPassword by remember { mutableStateOf(false) }
    var isTouched by remember { mutableStateOf(false) }
    FoodAppTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            if (!isTouched) isTouched = true
        },
        labelText = label,
        isError = errorMessage != null,
        errorText = errorMessage,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (isTouched && !focusState.isFocused) {
                    validate
                }
            },

        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {

            IconButton(
                onClick = {
                    showPassword = !showPassword
                },
            ) {
                if (!showPassword) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_eye),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_slash_eye),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        keyboardOptions = keyboardOptions,
        singleLine = false,
        maxLines = maxLines
    )
}