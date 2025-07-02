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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.R
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.model.Unit
import com.example.foodapp.navigation.Import
import com.example.foodapp.navigation.Material

import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun WarehouseScreen(
    navController: NavController,
    viewModel: WarehouseViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val inventories = remember(uiState.inventoryFilter) {
        viewModel.getInventories(uiState.inventoryFilter)
    }.collectAsLazyPagingItems()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                WarehouseState.Event.NavigateToImport -> {
                    navController.navigate(Import)
                }

                WarehouseState.Event.NavigateToMaterial -> {
                    navController.navigate(Material)
                }

            }
        }
    }




    Column(
        modifier = Modifier
            .fillMaxSize()

            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {


        HeaderDefaultView(
            onBackIcon = Icons.Filled.Inventory,
            onBack = {
                viewModel.onAction(WarehouseState.Action.OnNavigateToMaterial)
            },
            text = "Kho hàng",
            icon = Icons.Filled.Description,
            iconClick = {
                viewModel.onAction(WarehouseState.Action.OnNavigateToImport)
            }
        )
        SearchField(
            searchInput = uiState.nameSearch,
            searchChange = {
                viewModel.onAction(WarehouseState.Action.OnNameSearch(it))
            },
            searchFilter = {
                viewModel.onAction(WarehouseState.Action.OnSearchFilter)
            },
            switchState = uiState.inventoryFilter.order == "desc",
            switchChange = {
                when (it) {
                    true -> viewModel.onAction(WarehouseState.Action.OnOrderChange("desc"))
                    false -> viewModel.onAction(WarehouseState.Action.OnOrderChange("asc"))
                }
            },
            filterChange = {
                when (it) {
                    "Id" -> viewModel.onAction(WarehouseState.Action.OnSortByChange("id"))
                    "Số lượng" -> viewModel.onAction(WarehouseState.Action.OnSortByChange("quantityRemaining"))
                }
            },
            filters = listOf("Id", "Số lượng"),
            filterSelected = when (uiState.inventoryFilter.sortBy) {
                "id" -> "Id"
                "quantityRemaining" -> "Số lượng"
                else -> "Id"
            },
            placeHolder = "Tìm kiếm tồn kho theo tên..."
        )
        TabWithPager(
            tabs = listOf("Tồn kho", "Hết hạn", "Đã dùng"),
            pages = listOf(
                {
                    InventoryListSection(
                        list = inventories,
                        modifier = Modifier.fillMaxSize(),
                        onRefresh = {
                            viewModel.onAction(WarehouseState.Action.OnRefresh)
                        }
                    )
                },
                {
                    InventoryListSection(
                        list = inventories,
                        modifier = Modifier.fillMaxSize(),
                        onRefresh = {
                            viewModel.onAction(WarehouseState.Action.OnRefresh)
                        }
                    )
                },
                {
                    InventoryListSection(
                        list = inventories,
                        modifier = Modifier.fillMaxSize(),
                        onRefresh = {
                            viewModel.onAction(WarehouseState.Action.OnRefresh)
                        }
                    )
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onTabSelected = { index ->
                when (index) {
                    0 -> viewModel.onAction(
                        WarehouseState.Action.OnTabSelected(
                            uiState.inventoryFilter.copy(
                                isExpired = false,
                                isOutOfStock = false
                            )
                        )
                    )

                    1 -> viewModel.onAction(
                        WarehouseState.Action.OnTabSelected(
                            uiState.inventoryFilter.copy(
                                isExpired = true,
                                isOutOfStock = false
                            )
                        )
                    )

                    2 -> viewModel.onAction(
                        WarehouseState.Action.OnTabSelected(
                            uiState.inventoryFilter.copy(
                                isExpired = false,
                                isOutOfStock = true
                            )
                        )
                    )
                }
            }
        )

    }

}

@Composable
fun InventoryListSection(
    modifier: Modifier = Modifier,
    list: LazyPagingItems<Inventory>,
    onRefresh: () -> kotlin.Unit,
) {
    LazyPagingSample(
        onRefresh = onRefresh,
        modifier = modifier,
        items = list,
        textNothing = "Không có nguyên liệu nào",
        iconNothing = Icons.Default.Spa,
        columns = 2,
        key = {
            it.id
        }
    ) {
        InventoryItemView(
            inventory = it,
        )
    }

}


@Composable
fun InventoryItemView(
    inventory: Inventory,
    onClick: ((Inventory) -> Unit)? = null,
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
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                    spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(18.dp)
                )
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
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    tint = MaterialTheme.colorScheme.primary,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SL: ${inventory.quantityRemaining}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Text(
                        text = StringUtils.formatCurrency(inventory.cost),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,

                    )
                }

            }
        }
    }

}




