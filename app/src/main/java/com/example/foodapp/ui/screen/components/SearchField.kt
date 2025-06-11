package com.example.foodapp.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp


@Composable
fun SearchField(
    searchInput: String,
    searchChange: (String) -> Unit,
    searchPlaceholder: String= "Tìm kiếm ở đây",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            FoodAppTextField(
                value = searchInput,
                onValueChange = { searchChange(it)},
                placeholder = {
                    Text(text = searchPlaceholder , color = MaterialTheme.colorScheme.outline)
                },
                modifier = Modifier
                    .fillMaxWidth(),
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
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    unfocusedContainerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            )
        }
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(50.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }


    }
}





