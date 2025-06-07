package com.se114.foodapp.ui.screen.order


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.navigation.OrderDetailsCustomer
import com.example.foodapp.ui.screen.common.OrderListSection
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.TabWithPager

@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val orders = viewModel.getOrdersByTab(uiState.tabIndex).collectAsLazyPagingItems()

    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        val condition = handle?.get<Boolean>("shouldRefresh") == true
        if (condition) {
            handle["shouldRefresh"] = false
            viewModel.refreshAllTabs()
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
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(OrderList.Action.OnOrderClicked(it))
                        }
                    )
                },
                {
                    OrderListSection(
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(OrderList.Action.OnOrderClicked(it))
                        }
                    )
                }
            ),
            onTabSelected = {
                viewModel.onAction(OrderList.Action.OnTabChanged(it))
            }
        )
    }
}


