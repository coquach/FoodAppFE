package com.example.foodapp.ui.screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.foodapp.ui.theme.FoodAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBoxSample(
    modifier: Modifier = Modifier,
    title:  String?= null,
    textPlaceholder: String,
    selected: String?,
    onPositionSelected: (String?) -> Unit,
    options: List<String>,
    fieldHeight : Dp = 56.dp,
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(selected, options) {
        if (selected == null && options.isNotEmpty()) {
            onPositionSelected(options.first())
        }
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        FoodAppTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            labelText = title,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size
                },

            fieldHeight = fieldHeight

        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })

        ) {
            // Thêm mục rỗng
            DropdownMenuItem(
                text = { Text(textPlaceholder) },
                onClick = {
                    onPositionSelected(null)
                    expanded = false
                }
            )

            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onPositionSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


