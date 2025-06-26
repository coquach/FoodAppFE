package com.example.foodapp.ui.screen.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBoxSample(
    modifier: Modifier = Modifier,
    title:  String?= null,
    textPlaceholder: String,
    selected: String?,
    onPositionSelected: (String?) -> Unit,
    options: List<String>,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(selected, options) {
        if (selected == null && options.isNotEmpty()) {
            onPositionSelected(options.first())
        }
    }


    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        FoodAppTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            labelText = title,

            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable, true)


        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp)

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

@Composable
fun  CustomPagingDropdown(
    modifier: Modifier = Modifier,
    title: String,
    textPlaceholder: String,
    selected: String?,

    enabled: Boolean = true,
    dropdownContent: @Composable (onDismissDropdown: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                if(enabled) expanded = !expanded
            }
        }
    }

    Box(modifier) {
        FoodAppTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            labelText = title,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            },
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if(enabled) expanded = !expanded }
        )
Log.d("CustomPagingDropdown", "expanded: $expanded")
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .width(320.dp)
        ) {
            DropdownMenuItem(
                text = { Text(textPlaceholder) },
                onClick = {

                }
            )
            Column(
                modifier = Modifier.heightIn(300.dp)
            ){
                dropdownContent{
                    expanded = false

                }
            }
        }
    }
}

