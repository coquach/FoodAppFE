package com.example.foodapp.ui.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsGroupWrap(
    text: String,
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    isFlowLayout: Boolean = true
) {
    LaunchedEffect(options, selectedOption) {
        if (options.isNotEmpty() && selectedOption==null) {
            onOptionSelected(options.first())
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
//            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val layoutModifier = modifier
            .fillMaxWidth()
            .padding(8.dp)

        val optionLayout: @Composable (content: @Composable () -> Unit) -> Unit =
            if (isFlowLayout) {
                { content ->
                    FlowRow(
                        modifier = layoutModifier,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        content = { content() }
                    )
                }
            } else {
                { content ->
                    LazyRow(
                        modifier = layoutModifier,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            item {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    content()
                                }
                            }
                        },
                    )
                }
            }

        optionLayout {
            options.forEach { optionText ->
                val isSelected = optionText == selectedOption
                FilterChip(
                    selected = isSelected,
                    onClick = { onOptionSelected(optionText) },
                    label = {
                        Text(optionText)
                    },
                    colors = FilterChipDefaults.filterChipColors().copy(
                        labelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        containerColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }
    }
}
