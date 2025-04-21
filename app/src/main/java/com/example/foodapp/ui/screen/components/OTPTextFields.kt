package com.example.foodapp.ui.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OTPTextFields(
    length: Int = 4,
    onFilled: (String) -> Unit
) {
    var code by remember { mutableStateOf(List(length) { "" }) }
    val focusRequesters = remember { List(length) { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        (0 until length).forEach { index ->
            OutlinedTextField(
                value = code.getOrNull(index) ?: "",
                onValueChange = { newValue ->
                    if (focusRequesters[index].freeFocus()) {
                        val newCode = code.toMutableList()
                        if (newValue == "") {
                            if (newCode.size > index) {
                                newCode[index] = ""
                                code = newCode
                                focusRequesters.getOrNull(index - 1)?.requestFocus()
                            }
                        } else {
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                newCode[index] = newValue
                                code = newCode

                                focusRequesters.getOrNull(index + 1)?.requestFocus() ?: onFilled(
                                    newCode.joinToString("")
                                )

                            }
                        }
                    }

                },
                modifier = Modifier
                    .width(50.dp)
                    .focusRequester(focusRequesters[index])
                    .focusProperties {
                        next = focusRequesters.getOrNull(index + 1) ?: FocusRequester.Default
                        previous = focusRequesters.getOrNull(index - 1) ?: FocusRequester.Default
                    },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center, color = Color.Black
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == length - 1) ImeAction.Done else ImeAction.Next
                ),
                singleLine = true
            )
        }
    }

    // Tự động focus vào ô đầu tiên khi composable được hiển thị
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}

