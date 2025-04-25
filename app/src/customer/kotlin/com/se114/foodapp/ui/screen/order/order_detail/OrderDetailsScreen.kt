package com.example.foodapp.ui.screen.order.order_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
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
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Retry
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.utils.StringUtils


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
        if (order.status == OrderStatus.PENDING.toString()) {
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
    val orderStatus = OrderStatus.valueOf(order.status)
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
                text = "Số lượng món: ${order.orderItems.size}",
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
                text = "Thời gian tạo: ${order.createAt}",
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
                text = "Địa chỉ: ${order.address}",
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = orderStatus.icon,
            contentDescription = orderStatus.toString(),
            tint = orderStatus.color
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = orderStatus.display,
            color = orderStatus.color,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.size(16.dp))
}

