package com.se114.foodapp.ui.screen.order

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.OrderDetails
import com.example.foodapp.ui.screen.common.OrderListSection
import com.example.foodapp.ui.screen.components.HeaderDefaultView


import com.example.foodapp.ui.screen.components.TabWithPager
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel()

) {
    BackHandler {
        navController.popBackStack(route = Home, inclusive = false)
    }

    val currentTab by viewModel.tabIndex.collectAsStateWithLifecycle()

    val orders = viewModel.getOrdersByTab(currentTab).collectAsLazyPagingItems()

    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        val condition = handle?.get<Boolean>("updated") == true

        if (condition) {
            handle?.set("updated", false)
            viewModel.refreshAllTabs()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is OrderListViewModel.OrderListEvent.NavigateToOrderDetailScreen -> {
                    navController.navigate(OrderDetails(it.order))
                }

                is OrderListViewModel.OrderListEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                else -> {}
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
       HeaderDefaultView(
           text = "Danh sách đơn hàng"
       )
        TabWithPager(
            tabs = listOf("Đang chờ", "Đã xác nhận", "Đã giao hàng", "Đã hoàn thành", "Đã hủy"),
            pages = listOf(
                {

                    OrderListSection(
                        orders = orders,
                        onItemClick = {
                            navController.navigate(OrderDetails(it))
                        }
                    )

                },
                {
                    OrderListSection(
                        orders = orders,
                        onItemClick = {
                            navController.navigate(OrderDetails(it))
                        }
                    )
                },
                {
                    OrderListSection(
                        orders = orders,
                        onItemClick = {
                            navController.navigate(OrderDetails(it))
                        }
                    )
                },
                {
                    OrderListSection(
                        orders = orders,
                        onItemClick = {
                            navController.navigate(OrderDetails(it))
                        }
                    )
                },
                {
                    OrderListSection(
                        orders = orders,
                        onItemClick = {
                            navController.navigate(OrderDetails(it))
                        }
                    )
                }
            ),
            scrollable = true,
            onTabSelected = {
                viewModel.setTab(it)
            }
        )


    }
}

@Composable
fun <T : Any> ObserveLoadState(
    lazyPagingItems: LazyPagingItems<T>,
    statusName: String
) {

    // Lắng nghe và xử lý trạng thái loadState
    LaunchedEffect(lazyPagingItems.loadState.refresh) {
        when (val state = lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                Log.d("OrderListScreen", "Loading $statusName Orders")
            }

            is LoadState.Error -> {
                Log.d("OrderListScreen", "Error loading $statusName Orders: ${state.error.message}")
            }

            else -> {
                Log.d("OrderListScreen", "$statusName Orders loaded")
            }
        }
    }
}