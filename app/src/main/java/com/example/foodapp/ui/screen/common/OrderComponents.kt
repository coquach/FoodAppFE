package com.example.foodapp.ui.screen.common

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.paging.compose.LazyPagingItems
import com.example.foodapp.R
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.data.model.enums.ServingType
import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.NoteInput
import com.example.foodapp.ui.theme.confirm
import com.example.foodapp.utils.StringUtils

@Composable
fun OrderDetailsText(order: Order) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,

            ) {

            Image(
                painter = painterResource(id = R.drawable.receipt),
                contentDescription = null,
                modifier = Modifier.size(50.dp).align(Alignment.Bottom)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)

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
               DetailsTextRow(
                   text = "Khởi tạo: ${StringUtils.formatDateTime(order.startedAt)}",
                   icon = Icons.Default.Timer,
                   color = MaterialTheme.colorScheme.outline
               )

               DetailsTextRow(
                   text ="Phương thức: ${PaymentMethod.fromName(order.method)?.getDisplayName()?: "Không xác định"}",
                   icon = Icons.Default.Payments,
                   color = MaterialTheme.colorScheme.confirm
               )
            }

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Tổng giá: ${StringUtils.formatCurrency(order.totalPrice)}",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

        }
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

    }
}

@Composable
fun OrderItemView(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListSection(
    modifier: Modifier = Modifier,
    orders: LazyPagingItems<Order>,
    onItemClick: (Order) -> Unit,
    onRefresh: () -> Unit = {},

) {

           LazyPagingSample(
               modifier = modifier,
               items = orders,
               textNothing = "Không có đơn hàng nào",
               iconNothing = Icons.Default.Receipt,
               columns = 1,
               key = {
                       order -> order.id
               },
               itemContent = {
                   OrderItemView(
                       order = it,
                       onClick = {
                           onItemClick(it)
                       }
                   )
               },
                onRefresh = onRefresh
               )



}

@Composable
fun OrderDetails(order: Order, isStaff: Boolean = false) {
    val orderStatus = OrderStatus.valueOf(order.status)
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        DetailsTextRow(
            icon = Icons.Default.Numbers,
            color = MaterialTheme.colorScheme.primary,
            text = "Mã đơn hàng: ${order.id}"
        )

        DetailsTextRow(
            icon = orderStatus.icon,
            color = orderStatus.color,
            text = orderStatus.display
        )
        DetailsTextRow(
            icon = Icons.Default.LocationOn,
            color = MaterialTheme.colorScheme.onBackground,
            text = "Địa chỉ: ${order.address?.formatAddress?: "Không có địa chỉ"}"
        )




        DetailsTextRow(
            icon = Icons.Default.Timer,
            color = MaterialTheme.colorScheme.onBackground,
            text = "Khởi tạo: ${StringUtils.formatDateTime(order.startedAt)}"
        )
        DetailsTextRow(
            icon = Icons.Default.AccountBalanceWallet,
            color = MaterialTheme.colorScheme.onBackground,
            text = "Thanh toán lúc: ${StringUtils.formatDateTime(order.paymentAt)?: "Chưa thanh toán"}"
        )
        DetailsTextRow(
            icon = Icons.Default.Payments,
            color = MaterialTheme.colorScheme.onBackground,
            text = "Phuơng thức: ${
                PaymentMethod.fromName(order.method)!!.getDisplayName()
            }"
        )
        if (isStaff) {
            val orderType = ServingType.valueOf(order.type).display
            DetailsTextRow(
                icon = Icons.Default.Category,
                color = MaterialTheme.colorScheme.secondary,
                text = "Loại phục vụ: $orderType"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Notes,
                contentDescription = "Note",
                tint = MaterialTheme.colorScheme.inversePrimary
            )
            Text(
                text = "Ghi chú",
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
        NoteInput(
            modifier = Modifier,
            note = order.note ?: "",
            onNoteChange = {

            },
            maxLines = 3,
            textHolder = "Không có ghi chú",
            readOnly = true
        )
    }


}
