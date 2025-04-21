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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RadioGroupWrap(
    text: String,
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    optionIcons: List<ImageVector>? = null, // <-- giờ là nullable
    isFlowLayout: Boolean = true
) {
    LaunchedEffect(options, selectedOption) {
        if (options.isNotEmpty() && selectedOption.isBlank()) {
            onOptionSelected(options.first())
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val layoutModifier = modifier
            .fillMaxWidth()
            .selectableGroup()
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
                    Column(
                        modifier = layoutModifier,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        content = { content() }
                    )
                }
            }

        optionLayout {
            options.forEachIndexed { index, optionText ->
                val icon = optionIcons?.getOrNull(index)

                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (optionText == selectedOption),
                            onClick = { onOptionSelected(optionText) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (optionText == selectedOption),
                        onClick = null
                    )
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}