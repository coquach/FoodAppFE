package com.se114.foodapp.ui.screen.order


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.OrderDetailsStaff
import com.example.foodapp.ui.screen.common.OrderListSection
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.TabWithPager
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel(),

    ) {
    BackHandler {
        navController.popBackStack(route = Home, inclusive = false)
    }

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val orders = remember(uiState.orderFilter) {
        viewModel.getOrders(uiState.orderFilter)
    }.collectAsLazyPagingItems()


    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is OrderListState.Event.GoToDetail -> {
                    navController.navigate(OrderDetailsStaff(it.order))
                }

            }
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        HeaderDefaultView(
            text = "Danh sách đơn hàng"
        )
        TabWithPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            tabs = listOf(
                "Đang chờ",
                "Đã xác nhận",
                "Đã chuẩn bị",
                "Đang giao hàng",
                "Đã hoàn thành",
                "Đã hủy"
            ),
            pages = listOf(
                {

                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },


                        )

                },
                {
                    OrderListSection(
                        onRefresh = {
                            viewModel.onAction(OrderListState.Action.OnRefresh)
                        },
                        modifier = Modifier.fillMaxSize(),
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },


                        )
                },
                {
                    OrderListSection(
                        onRefresh = {
                            viewModel.onAction(OrderListState.Action.OnRefresh)
                        },
                        modifier = Modifier.fillMaxSize(),
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },


                        )
                },
                {
                    OrderListSection(
                        onRefresh = {
                            viewModel.onAction(OrderListState.Action.OnRefresh)
                        },
                        modifier = Modifier.fillMaxSize(),
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },


                        )
                },
                {
                    OrderListSection(
                        onRefresh = {
                            viewModel.onAction(OrderListState.Action.OnRefresh)
                        },
                        modifier = Modifier.fillMaxSize(),
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },

                        )
                },
                {
                    OrderListSection(
                        onRefresh = {
                            viewModel.onAction(OrderListState.Action.OnRefresh)
                        },
                        modifier = Modifier.fillMaxSize(),
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },

                        )
                }
            ),
            scrollable = true,
            onTabSelected = {
                when (it) {
                    0 -> viewModel.onAction(OrderListState.Action.OnStatusFilterChange(OrderStatus.PENDING.name))
                    1 -> viewModel.onAction(OrderListState.Action.OnStatusFilterChange(OrderStatus.CONFIRMED.name))
                    2 -> viewModel.onAction(OrderListState.Action.OnStatusFilterChange(OrderStatus.READY.name))
                    3 -> viewModel.onAction(OrderListState.Action.OnStatusFilterChange(OrderStatus.SHIPPING.name))
                    4 -> viewModel.onAction(OrderListState.Action.OnStatusFilterChange(OrderStatus.COMPLETED.name))
                    5 -> viewModel.onAction(OrderListState.Action.OnStatusFilterChange(OrderStatus.CANCELLED.name))
                }
            }
        )


    }
}

@Composable
fun <T : Any> ObserveLoadState(
    lazyPagingItems: LazyPagingItems<T>,
    statusName: String,
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