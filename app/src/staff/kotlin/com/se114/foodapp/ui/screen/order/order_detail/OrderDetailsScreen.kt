package com.se114.foodapp.ui.screen.order.order_detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.ui.screen.common.CheckoutRowItem
import com.example.foodapp.ui.screen.common.OrderDetails
import com.example.foodapp.ui.screen.common.OrderItemView
import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.NoteInput
import com.example.foodapp.ui.theme.onConfirm
import com.example.foodapp.utils.StringUtils
import java.math.BigDecimal


@Composable
fun OrderDetailScreen(
    navController: NavController,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showErrorSheet by rememberSaveable {mutableStateOf(false)}

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {

                is OrderDetailsState.Event.UpdateOrder -> {
                    Toast.makeText(
                        context,
                        "Cập nhật tình trạng đơn hàng thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("updated", true)
                    navController.popBackStack()
                }

                OrderDetailsState.Event.ShowError -> {
                    showErrorSheet = true
                }
                OrderDetailsState.Event.OnBack -> {
                    navController.popBackStack()
                }
            }
        }
    }


    Column(
        Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderDefaultView(
            onBack = {
                viewModel.onAction(OrderDetailsState.Action.OnBack)
            },
            text = "Chi tiết đơn hàng",

        )

        OrderDetails(uiState.order, isStaff = true)

        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(uiState.order.orderItems, key = { it.id }) { item ->
                OrderItemView(
                    orderItem = item,
                    isCustomer = false
                )
            }

        }
        CheckoutRowItem("Tổng cộng", uiState.order.totalPrice, FontWeight.ExtraBold)


            when (uiState.order.status) {
                OrderStatus.PENDING.name-> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LoadingButton(
                            onClick = {
                                viewModel.onAction(OrderDetailsState.Action.UpdateStatusOrder(OrderStatus.CANCELLED.name))
                            },
                            text = "Hủy",
                            loading = uiState.isLoading,
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.weight(1f)

                        )
                        LoadingButton(
                            onClick = {
                                viewModel.onAction(OrderDetailsState.Action.UpdateStatusOrder(OrderStatus.CONFIRMED.name))
                            },
                            text = "Xác nhận",
                            loading = uiState.isLoading,
                            containerColor = OrderStatus.CONFIRMED.color,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f)
                        )

                    }

                }

                OrderStatus.CONFIRMED.name -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LoadingButton(
                            onClick = {
                                viewModel.onAction(OrderDetailsState.Action.UpdateStatusOrder(OrderStatus.CANCELLED.name))
                            },
                            text = "Hủy",
                            loading = uiState.isLoading,
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.weight(1f)
                        )
                        LoadingButton(
                            onClick = {
                                viewModel.onAction(OrderDetailsState.Action.UpdateStatusOrder(OrderStatus.READY.name))
                            },
                            text = "Chuẩn bị",
                            loading = uiState.isLoading,
                            containerColor = OrderStatus.READY.color,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f)
                        )

                    }
                }


                OrderStatus.READY.name, OrderStatus.SHIPPING.name, OrderStatus.COMPLETED.name, OrderStatus.CANCELLED.name -> {
                    Column(  modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                }

                else -> {}
            }
        }



}

//@Composable
//fun OrderDetails(order: Order) {
//    val orderStatus = OrderStatus.valueOf(order.status)
//    Column(
//        modifier = Modifier
//            .fillMaxWidth(),
//        horizontalAlignment = Alignment.Start,
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//
//        DetailsTextRow(
//            icon = Icons.Default.Numbers,
//            color = MaterialTheme.colorScheme.primary,
//            text = "Mã đơn hàng: ${order.id}"
//        )
//
////            order.createdBy?.let {
////                OrderDetailsRow(
////                    icon = Icons.Default.SupervisorAccount,
////                    color = MaterialTheme.colorScheme.secondary,
////                    text = "Tên nhân viên: ${order.createdBy}"
////                )
////            }
////            OrderDetailsRow(
////                icon = Icons.Default.Person,
////                color = MaterialTheme.colorScheme.tertiary,
////                text = "Tên khách hàng: ${order.customerName}"
////            )
//
//        DetailsTextRow(
//            icon = orderStatus.icon,
//            color = orderStatus.color,
//            text = orderStatus.display
//        )
//        DetailsTextRow(
//            icon = Icons.Default.LocationOn,
//            color = MaterialTheme.colorScheme.onBackground,
//            text = "Địa chỉ: ${order.address?.formatAddress ?: "Không có địa chỉ"}"
//        )
//
//
//
//        DetailsTextRow(
//            icon = Icons.Default.Timer,
//            color = MaterialTheme.colorScheme.onBackground,
//            text = "Khởi tạo: ${StringUtils.formatDateTime(order.startedAt)}"
//        )
//
//        DetailsTextRow(
//            icon = Icons.Default.Payments,
//            color = MaterialTheme.colorScheme.onBackground,
//            text = "Phuơng thức: ${PaymentMethod.fromName(order.method)!!.getDisplayName()}"
//        )
//
//            DetailsTextRow(
//                icon = Icons.Default.Category,
//                color = MaterialTheme.colorScheme.secondary,
//                text = "Loại phục vụ: ${order.type}"
//            )
//
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(12.dp)
//
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.Notes,
//                contentDescription = "Note",
//                tint = MaterialTheme.colorScheme.inversePrimary
//            )
//            Text(
//                text = "Ghi chú",
//                color = MaterialTheme.colorScheme.inversePrimary
//            )
//        }
//        NoteInput(
//            note = order.note?: "",
//            onNoteChange = {
//
//            },
//            modifier = Modifier,
//            maxLines = 3,
//            textHolder = "Không có ghi chú",
//            readOnly = true
//        )
//    }
//
//
//}





