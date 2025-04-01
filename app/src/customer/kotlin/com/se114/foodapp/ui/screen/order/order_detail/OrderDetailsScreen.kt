package com.example.foodapp.ui.screen.order.order_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.OrderItem
import com.example.foodapp.ui.HeaderDefaultView
import com.example.foodapp.ui.Loading
import com.example.foodapp.ui.Retry
import com.example.foodapp.ui.screen.order.OrderDetailsText
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.utils.OrdersUtils

@Composable
fun OrderDetailScreen(
    navController: NavController,
    order: Order,
    viewModel: OrderDetailsViewModel = hiltViewModel()

) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    Column(Modifier.padding(horizontal = 16.dp)) {
        HeaderDefaultView(
            onBack = {
                navController.popBackStack()
            },
            text = "Chi tiết đơn hàng"
        )

        OrderDetails(order)

        when (uiState.value) {
            is OrderDetailsViewModel.OrderDetailsState.Loading -> {

            }

            is OrderDetailsViewModel.OrderDetailsState.OrderDetails -> {


            }

            is OrderDetailsViewModel.OrderDetailsState.Error -> {
                val message =
                    (uiState.value as OrderDetailsViewModel.OrderDetailsState.Error).message
                Retry(
                    message,
                    onClicked = {

                    }
                )
            }

            else -> {}
        }
        Spacer(modifier = Modifier.size(16.dp))
        if (order.status == "PENDING_ACCEPTANCE") {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "Hủy đơn hàng",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }


    }
}

@Composable
fun OrderDetails(order: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Numbers,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Mã đơn hàng: ${order.id}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Số lượng món: ${order.items.size.toString()}",
                color = MaterialTheme.colorScheme.outline
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_clock),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Thời gian tạo: ${StringUtils.formatDateTime(order.createdAt)}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Clock Icon",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Địa chỉ: ${order.address.addressLine1}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = "Clock Icon",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Phuơng thức thanh toán: ${order.paymentMethod}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    Spacer(modifier = Modifier.size(16.dp))
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

@Preview(showBackground = true)
@Composable
fun OrderDetailScreenPreview() {
    FoodAppTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            OrderDetails(
                Order(
                    id = "2",
                    userId = "user_456",
                    createdAt = "2025-03-23T11:00:00Z",
                    updatedAt = "2025-03-23T11:15:00Z",
                    status = "ACCEPTED",
                    totalAmount = 250.0f,
                    paymentMethod = "PayPal",
                    address = Address(
                        addressLine1 = "456 Elm St",
                        city = "Los Angeles",
                        state = "CA",
                        zipCode = "90001",
                        country = "USA"
                    ),
                    items = listOf(
                        OrderItem(
                            id = "item_2",
                            menuItemId = "menu_456",
                            orderId = "2",
                            quantity = 1,
                            menuItemName = "Cappuccino"
                        )
                    )
                )
            )
        }

    }
}



