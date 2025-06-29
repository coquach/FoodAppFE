package com.example.foodapp.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    searchInput: String,
    searchChange: (String) -> Unit,
    searchFilter: () ->Unit,
    switchState: Boolean,
    switchChange: (Boolean) -> Unit,
    filterChange: (String) -> Unit,
    filters: List<String>,
    filterSelected: String,
    placeHolder: String,
    isFilterBox: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    var wasFocused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        FoodAppTextField(
            value = searchInput,
            onValueChange = { searchChange(it) },
            placeholder = {
                Text(text = placeHolder, color = MaterialTheme.colorScheme.outline)
            },
            modifier = Modifier
                .weight(1f).onFocusChanged { focusState ->
                    if (wasFocused && !focusState.isFocused) {
                        searchFilter()
                    }
                    wasFocused = focusState.isFocused
                },
            singleLine = true,
            maxLines = 1,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors().copy(
                unfocusedContainerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        )
        if(isFilterBox){
            var expanded by remember { mutableStateOf(false) }
            var isSwitchOn by remember { mutableStateOf(switchState) }
            Box() {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(16.dp)
                        ),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(220.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Thứ tự",
                                    color = MaterialTheme.colorScheme.outline,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold,

                                    )
                                Switch(
                                    checked = isSwitchOn,
                                    onCheckedChange = {
                                        isSwitchOn = it
                                        switchChange(it)
                                    },
                                    thumbContent = {
                                        if (isSwitchOn) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDownward,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.ArrowUpward,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        },
                        onClick = {

                        }
                    )
                    DropdownMenuItem(
                        text = {
                            ChipsGroupWrap(
                                modifier = Modifier.padding(2.dp),
                                text = "Sắp xếp",
                                options = filters,
                                selectedOption = filterSelected,
                                onOptionSelected = {
                                    filterChange(it)
                                },
                                containerColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f),
                            )
                        },
                        onClick = {

                        }
                    )

                }
            }
        }



    }


}




