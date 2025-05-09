package com.example.foodapp.ui.screen.order

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.data.model.Order
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.Retry
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.OrderDetails
import com.example.foodapp.ui.screen.components.GenericListContent
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.utils.OrdersUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel()
) {
    BackHandler {
        navController.popBackStack(route = Home, inclusive = false)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)

    ) {
        val uiState = viewModel.state.collectAsStateWithLifecycle()

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
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(48.dp))
            Text(
                text = "Đơn hàng", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }
        when (uiState.value) {
            is OrderListViewModel.OrderListState.Loading -> {
                Loading()
            }

            is OrderListViewModel.OrderListState.Success -> {
                val list = (uiState.value as OrderListViewModel.OrderListState.Success).orderList

                TabWithPager(
                    tabs = listOf("Sắp tới", "Lịch sử"),
                    pages = listOf(
                        {
                            GenericListContent(
                                list = list.filter { it.status == "PENDING_ACCEPTANCE" },
                                iconEmpty = Icons.Default.Inventory2,
                                textEmpty = "Không có đơn hàng nào",
                                itemContent = {order ->
                                    OrderListItem(order = order, onClick = { viewModel.navigateToDetails(order) })
                                },
                            )
                        },
                        {
                            GenericListContent(
                                list = list.filter { it.status != "PENDING_ACCEPTANCE" },
                                iconEmpty = Icons.Default.Inventory2,
                                textEmpty = "Không có đơn hàng nào",
                                itemContent = {order ->
                                    OrderListItem(order = order, onClick = { viewModel.navigateToDetails(order) })
                                },
                            )
                        }
                    )
                )

            }

            is OrderListViewModel.OrderListState.Error -> {
                val message = (uiState.value as OrderListViewModel.OrderListState.Error).message
                Retry(
                    message,
                    onClicked = {
                        viewModel.getOrders()
                    }
                )
            }


            else -> {}
        }

    }
}


@Composable
fun OrderDetailsText(order: Order) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,

            ) {

            Image(
                painter = painterResource(id = R.drawable.receipt),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start

            ) {
                Text(
                    text = "Mã đơn hàng: ${order.id}",
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Số lượng món: ${order.items.size.toString()}",
                    color = MaterialTheme.colorScheme.outline
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clock),
                        contentDescription = "Clock Icon",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = StringUtils.formatDateTime(order.createdAt),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        val orderStatus = OrdersUtils.getOrderStatusFromString(order.status)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Trạng thái:", color = Color.Gray)
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = OrdersUtils.getOrderStatusInVietnamese(
                    orderStatus ?: OrdersUtils.OrderStatus.DEFAULT
                ),
                color = OrdersUtils.getStatusColor(orderStatus ?: OrdersUtils.OrderStatus.DEFAULT),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun OrderListItem(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)

        ) {
            OrderDetailsText(order = order)
            Button(onClick = onClick) {
                Text(
                    text = "Chi tiết",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

        }
    }

}