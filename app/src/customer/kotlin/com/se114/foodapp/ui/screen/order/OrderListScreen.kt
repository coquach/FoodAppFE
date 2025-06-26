package com.se114.foodapp.ui.screen.order


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.navigation.OrderDetailsCustomer
import com.example.foodapp.ui.screen.common.OrderListSection
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.TabWithPager
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val orders = viewModel.orders.collectAsLazyPagingItems()


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
        DateRangePickerSample(
            modifier = Modifier.width(170.dp),
            startDateText = "Bắt đầu",
            endDateText = "Kết thúc",
            onDateRangeSelected = { startDate, endDate ->
                viewModel.onAction(OrderList.Action.OnChangeDateFilter(startDate, endDate))
            },
            startDate = uiState.orderFilter.startDate,
            endDate = uiState.orderFilter.endDate,

        )
        TabWithPager(
            tabs = listOf("Sắp tới", "Lịch sử"),
            pages = listOf(
                {
                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(OrderList.Action.OnOrderClicked(it))
                        }
                    )
                },
                {
                    OrderListSection(
                        modifier = Modifier.fillMaxSize(),
                        orders = orders,
                        onItemClick = {
                            viewModel.onAction(OrderList.Action.OnOrderClicked(it))
                        }
                    )
                }
            ),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            onTabSelected = {
                when (it) {
                    0 -> viewModel.onAction(OrderList.Action.OnTabChanged(OrderStatus.PENDING.name))
                    1 -> viewModel.onAction(OrderList.Action.OnTabChanged(null))
                }
            }
        )
    }
}


