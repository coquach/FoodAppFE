package com.se114.foodapp.ui.screen.warehouse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.R
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.model.Unit
import com.example.foodapp.navigation.Import
import com.example.foodapp.navigation.Material

import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.utils.StringUtils

@Composable
fun WarehouseScreen(
    navController: NavController,
    viewModel: WarehouseViewModel = hiltViewModel()
) {
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val inventories = viewModel.getInventoriesByTab(uiState.tabIndex).collectAsLazyPagingItems()





    Column(
        modifier = Modifier
            .fillMaxSize()

            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {


        HeaderDefaultView(
            onBackIcon = Icons.Filled.Inventory,
            onBack = {
                navController.navigate(Material)
            },
            text = "Kho hàng",
            icon = Icons.Filled.Description,
            iconClick = {
                navController.navigate(Import)
            }
        )
//        SearchField(
//            searchInput = search,
//            searchChange = { search = it }
//        )
        TabWithPager(
            tabs = listOf("Tồn kho", "Hết hạn", "Đã dùng"),
            pages = listOf(
                {
                    InventoryListSection(
                        list = inventories
                    )
                },
                {
                    InventoryListSection(
                        list = inventories
                    )
                },
                {
                    InventoryListSection(
                        list = inventories
                    )
                }
            ),
            onTabSelected = { index ->
                viewModel.onAction(WarehouseState.Action.OnTabSelected(index))
            }
        )

    }

}

@Composable
fun InventoryListSection(
    list: LazyPagingItems<Inventory>,
) {
    if (list.itemCount == 0 && list.loadState.refresh !is LoadState.Loading) {
        Nothing(
            icon = Icons.Default.Spa,
            text = "Không có nguyên liệu nào",
            modifier = Modifier.fillMaxSize()
        )
    } else {

        LazyColumn(
            modifier = Modifier.heightIn(max = 10000.dp)

        ) {
            gridItems(
                list, 2, key = { inventory -> inventory.id },
                itemContent = { inventory ->
                    inventory?.let {
                        InventoryItemView(
                            inventory = inventory,
                        )
                    }
                })}

    }
}


@Composable
fun InventoryItemView(
    inventory: Inventory,
    onClick: ((Inventory) -> Unit) ? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(162.dp)
                .height(216.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                    spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                )
                .background(color = MaterialTheme.colorScheme.surface)
                .clickable(
                    onClick = { onClick?.invoke(inventory) },
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(147.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.image_error),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),

                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = StringUtils.formatCurrency(inventory.cost),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.outlineVariant)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.TopStart)
                )

            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = inventory.ingredientName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${inventory.quantityRemaining}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}




