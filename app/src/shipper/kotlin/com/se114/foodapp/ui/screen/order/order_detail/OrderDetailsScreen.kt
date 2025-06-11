package com.se114.foodapp.ui.screen.order.order_detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
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
import com.example.foodapp.navigation.Tracking
import com.example.foodapp.ui.screen.common.CheckoutRowItem
import com.example.foodapp.ui.screen.common.OrderDetails
import com.example.foodapp.ui.screen.common.OrderItemView
import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.NoteInput
import com.example.foodapp.utils.StringUtils
import java.math.BigDecimal


@Composable
fun OrderDetailScreen(
    navController: NavController,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }

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

                is OrderDetailsState.Event.GoToTracking -> {
                    navController.navigate(Tracking(long =  it.lon, lat = it.lat))
                }

                OrderDetailsState.Event.OnBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val distance =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Double?>(
            "distance",
            null
        )
            ?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = distance?.value) {
        distance?.value?.let {
            val canUpdateStatus = it <= 50
            viewModel.onAction(OrderDetailsState.Action.OnChangeCanUpdateStatus(canUpdateStatus))
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
            icon = if(uiState.order.status == OrderStatus.SHIPPING.name) Icons.Default.Map else null,
            iconClick = {
                viewModel.onAction(OrderDetailsState.Action.GoToTracking(uiState.order.address!!.longtitude, uiState.order.address!!.latitude))
            }
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
        CheckoutRowItem("Tổng cộng", BigDecimal.ZERO, FontWeight.ExtraBold)



        if (uiState.order.status == OrderStatus.READY.name) {
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
                        viewModel.onAction(OrderDetailsState.Action.UpdateStatusOrder(OrderStatus.SHIPPING.name))
                    },
                    text = "Giao hàng",
                    loading = uiState.isLoading,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                )

            }
        }
        else if(uiState.order.status == OrderStatus.SHIPPING.name){
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
                        viewModel.onAction(OrderDetailsState.Action.UpdateStatusOrder(OrderStatus.COMPLETED.name))
                    },
                    text = "Hoàn thành",
                    loading = uiState.isLoading,
                    containerColor = OrderStatus.COMPLETED.color,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                    enabled = uiState.canUpdateStatus
                )

            }
        }


    }


}






