package com.example.foodapp.ui.screen.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.foodapp.R
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.utils.StringUtils

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
                        text = StringUtils.formatLocalDate(order.orderDate)!!,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        val orderStatus = OrderStatus.valueOf(order.status)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = orderStatus.icon,
                contentDescription = null,
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
}

@Composable
fun OrderItemView(order: Order, onClick: () -> Unit) {
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

@Composable
fun OrderListSection(
    orders: LazyPagingItems<Order>,
    onItemClick: (Order) -> Unit,
) {
    if (orders.itemSnapshotList.items.isEmpty() && orders.loadState.refresh !is LoadState.Loading) {
        Nothing(
            text = "Không có đơn hàng nào",
            icon = Icons.Default.Receipt
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            gridItems(
                orders, 2, key = { order -> order.id },

                itemContent = { order ->
                    order?.let {
                        OrderItemView(
                            order = order,
                            onClick = { onItemClick(order) }
                        )

                    } },

            )  
            
        }
    }
}