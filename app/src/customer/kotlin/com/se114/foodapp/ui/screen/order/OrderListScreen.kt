package com.se114.foodapp.ui.screen.order


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
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Order
import com.example.foodapp.navigation.OrderDetailsCustomer
import com.example.foodapp.ui.screen.common.OrderListSection
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.TabWithPager
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val orders by viewModel.ordersTabManager.tabDataMap.collectAsStateWithLifecycle()
    val emptyOrders = MutableStateFlow<PagingData<Order>>(PagingData.empty()).collectAsLazyPagingItems()

    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        val condition = handle?.get<Boolean>("shouldRefresh") == true
        if (condition) {
            handle["shouldRefresh"] = false
            viewModel.onAction(OrderList.Action.OnRefresh)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is OrderList.Event.GoToDetails -> {
                    navController.navigate(OrderDetailsCustomer(it.order))
                }
            }
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {

        HeaderDefaultView(
            text = "Đơn hàng"
        )

        TabWithPager(
            tabs = listOf("Sắp tới", "Lịch sử"),
            pages = listOf(
                {
                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders[0]?.flow?.collectAsLazyPagingItems()?: emptyOrders,
                        onItemClick = {
                            viewModel.onAction(OrderList.Action.OnOrderClicked(it))
                        }
                    )
                },
                {
                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders[1]?.flow?.collectAsLazyPagingItems()?: emptyOrders,
                        onItemClick = {
                            viewModel.onAction(OrderList.Action.OnOrderClicked(it))
                        }
                    )
                }
            ),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            onTabSelected = {
                viewModel.onAction(OrderList.Action.OnTabChanged(it))
                viewModel.getOrdersFlow(it)
            }
        )
    }
}


