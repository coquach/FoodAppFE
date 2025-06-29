package com.se114.foodapp.ui.screen.checkout.get_foods

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.ui.screen.common.FoodList
import com.example.foodapp.ui.screen.components.AppButton
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.utils.StringUtils
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import okhttp3.internal.http2.Header

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.GetFoodsScreen(
    navController: NavController,
    viewModel: GetFoodsViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foods = remember(uiState.filter) {
        viewModel.getFoodsByMenuId(uiState.filter)
    }.collectAsLazyPagingItems()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect { event ->
            when (event) {
                is GetFoodsState.Event.OnBack -> navController.popBackStack()
                is GetFoodsState.Event.GetFoodToCheckout -> {
                    val orderItems = event.orderItems
                    navController.previousBackStackEntry?.savedStateHandle?.set("orderItems", orderItems)
                    navController.popBackStack()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getMenus()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),

        ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderDefaultView(
                text = "Danh sách món ăn",
                onBack = { viewModel.onAction(GetFoodsState.Action.OnBack) }
            )
            SearchField(
                searchInput = uiState.nameSearch,
                searchChange = {
                    viewModel.onAction(GetFoodsState.Action.OnNameSearchChange(it))
                },
                searchFilter = {
                    viewModel.onAction(GetFoodsState.Action.OnSearchFilter)
                },
                switchState = uiState.filter.order == "desc",
                switchChange = {
                    when (it) {
                        true -> viewModel.onAction(GetFoodsState.Action.OnOrderChange("desc"))
                        false -> viewModel.onAction(GetFoodsState.Action.OnOrderChange("asc"))
                    }
                },
                filterChange = {
                    when (it) {
                        "Giá" -> viewModel.onAction(GetFoodsState.Action.OnSortByChange("price"))
                        "Số lượng" -> viewModel.onAction(GetFoodsState.Action.OnSortByChange("remainingQuantity"))
                    }
                },
                filters = listOf("Giá", "Số lượng"),
                filterSelected = when (uiState.filter.sortBy) {
                    "price" -> "Giá"
                    "remainingQuantity" -> "Số lượng"
                    else -> "Giá"
                },
                placeHolder = "Tìm kiếm món ăn theo tên"
            )
            FoodList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                foods = foods,
                animatedVisibilityScope = animatedVisibilityScope,
                onItemClick = {
                    viewModel.onAction(GetFoodsState.Action.AddFood(it))
                },
                isSwipeAction = false,
                isAnimated = false,
            )

        }

        AnimatedVisibility(
            visible = uiState.foodStaffsUi.isNotEmpty(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(items = uiState.foodStaffsUi, key = { it.id }) {
                        SwipeableActionsBox(
                            modifier = Modifier
                                .padding(
                                    8.dp,
                                )
                                .clip(RoundedCornerShape(12.dp)),
                            startActions = listOf(
                                SwipeAction(
                                    icon = rememberVectorPainter(Icons.Default.Delete),
                                    background = MaterialTheme.colorScheme.error,
                                    onSwipe = {
                                        viewModel.onAction(GetFoodsState.Action.RemoveFood(it.id))
                                    }
                                )
                            )
                        ) {
                                FoodItemView(
                                    food = it,
                                    onIncrement = {
                                        viewModel.onAction(GetFoodsState.Action.OnQuantityChange(it, it.quantity + 1))
                                    },
                                    onDecrement = {
                                        viewModel.onAction(GetFoodsState.Action.OnQuantityChange(it, it.quantity - 1))
                                    }
                                )
                        }
                    }
                }
                AppButton(
                    text = "Lưu thay đổi",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.onAction(GetFoodsState.Action.GetFoodToCheckout)
                    },

                )
            }
        }
    }

}

@Composable
fun FoodItemView(
    food: FoodUiStaffModel,
    onIncrement: (FoodUiStaffModel) -> Unit,
    onDecrement: (FoodUiStaffModel) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = food.image,
            contentDescription = null,
            modifier = Modifier
                .size(82.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = food.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )


            Spacer(modifier = Modifier.size(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StringUtils.formatCurrency(food.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                FoodItemCounter(
                    count = food.quantity,
                    onCounterIncrement = { onIncrement.invoke(food) },
                    onCounterDecrement = { onDecrement.invoke(food) },
                )
            }
        }
    }
}