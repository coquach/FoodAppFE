package com.se114.foodapp.ui.screen.order


import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.foodapp.navigation.OrderDetailsStaff
import com.example.foodapp.ui.screen.common.OrderListSection
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.TabWithPager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel(),

    ) {


    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val orders by viewModel.ordersTabManager.tabDataMap.collectAsStateWithLifecycle()

    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        val condition = handle?.get<Boolean>("updated") == true

        if (condition) {
            handle["updated"] = false
            viewModel.onAction(OrderListState.Action.OnRefresh)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getOrdersFlow(uiState.tabIndex)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.ordersTabManager.refreshAllTabs()
        }
    }

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

    val notificationPermissionState =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        ) else null

    if (notificationPermissionState != null) {
        LaunchedEffect(Unit) {

            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }

        }
    }
    val emptyOrders = MutableStateFlow<PagingData<Order>>(PagingData.empty()).collectAsLazyPagingItems()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        HeaderDefaultView(
            text = "Danh sách đơn hàng"
        )
        TabWithPager(
            modifier = Modifier.fillMaxWidth().weight(1f),
            tabs = listOf("Đang chuẩn bị","Đang giao hàng", "Đã hoàn thành", "Đã hủy"),
            pages = listOf(
                {

                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders[0]?.flow?.collectAsLazyPagingItems() ?: emptyOrders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },

                    )

                },
                {
                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders[1]?.flow?.collectAsLazyPagingItems() ?: emptyOrders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },


                        )
                },
                {
                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders[2]?.flow?.collectAsLazyPagingItems() ?: emptyOrders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },


                        )
                },
                {
                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders[3]?.flow?.collectAsLazyPagingItems() ?: emptyOrders,
                        onItemClick = {
                            viewModel.onAction(
                                OrderListState.Action.OnOrderClicked(it)
                            )
                        },
                      

                        )
                },

            ),
            scrollable = true,
            onTabSelected = {
                viewModel.onAction(OrderListState.Action.OnTabSelected(it))
                viewModel.getOrdersFlow(it)
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