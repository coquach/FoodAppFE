package com.example.foodapp.ui.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.foodapp.R
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.data.model.enums.FoodTableStatus

@Composable
fun FoodTableCard(
    foodTable: FoodTable,
    onClick: (FoodTable) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .width(162.dp)
            .height(147.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
            )
            .background(color = if(foodTable.status == FoodTableStatus.OCCUPIED.name) MaterialTheme.colorScheme.outline.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface)
            .clickable(
                onClick = { onClick.invoke(foodTable) },
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_table),
                contentDescription = "Food Table",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp)
                ,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Bàn ${foodTable.tableNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.outlineVariant)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomStart)
            )

            Text(
                text = "Số chỗ: ${foodTable.seatCapacity}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.outlineVariant)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopEnd)
            )


        }
    }
}