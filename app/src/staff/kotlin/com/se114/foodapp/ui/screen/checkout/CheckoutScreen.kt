package com.se114.foodapp.ui.screen.checkout

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.data.model.OrderItem
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.navigation.GetFoodForStaff
import com.example.foodapp.navigation.OrderSuccess
import com.example.foodapp.navigation.VoucherCheck
import com.example.foodapp.navigation.VoucherCheckStaff
import com.example.foodapp.ui.screen.common.CheckoutDetailsView
import com.example.foodapp.ui.screen.common.CheckoutRowItem
import com.example.foodapp.ui.screen.common.calculateVoucherValue
import com.example.foodapp.ui.screen.components.AppButton
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.example.foodapp.ui.screen.components.Retry
import com.example.foodapp.utils.StringUtils
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showErrorSheet by rememberSaveable {
        mutableStateOf(
            false
        )
    }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isCheckOut by rememberSaveable { mutableStateOf(false) }
    var isSaveOrderItems by rememberSaveable { mutableStateOf(false) }
    val totalPriceOrderItem by remember {
        derivedStateOf {
            uiState.orderItems.sumOf { it.quantity.toBigDecimal() * it.price }
        }
    }


    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {

                Checkout.Event.ChooseVoucher -> {
                    navController.navigate(VoucherCheckStaff(totalPriceOrderItem.toPlainString()))
                }

                Checkout.Event.OnBack -> {
                    navController.popBackStack()
                }

                is Checkout.Event.OrderSuccess -> {
                    navController.navigate(OrderSuccess(it.orderId))
                }

                Checkout.Event.ShowError -> {
                    showErrorSheet = true
                }

                is Checkout.Event.ShowToastSuccess -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Checkout.Event.NavigateToGetFood -> {
                    navController.navigate(GetFoodForStaff)
                }

                Checkout.Event.isSaveOrderItems -> {
                    isSaveOrderItems = true
                }
            }
        }
    }

    val voucher =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Voucher?>(
            "voucher",
            null
        )
            ?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = voucher?.value) {
        voucher?.value?.let {
            viewModel.onAction(Checkout.Action.OnVoucherChanged(it))
        }
    }

    val orderItems =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<List<OrderItem>?>(
            "orderItems",
            null
        )?.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = orderItems?.value) {
        orderItems?.value?.let {
            viewModel.onAction(Checkout.Action.OnOrderItemsChanged(it))
        }
    }


//    LaunchedEffect(Unit) {
//        viewModel.getOrderByFoodTableId()
//
//    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {
        HeaderDefaultView(
            onBack = {
                viewModel.onAction(Checkout.Action.OnBack)
            },
            text = "Xác nhận đơn hàng",
            icon = Icons.Default.Fastfood,
            iconClick = {
                viewModel.onAction(Checkout.Action.NavigateToGetFood)
            }
        )

        when (uiState.getOrdersState) {


            Checkout.GetOrderState.Loading -> {
                Loading(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            else -> {
                if (uiState.orderItems.isEmpty()) {
                    Nothing(
                        text = "Chưa có món ăn nào trong đơn hàng",
                        icon = Icons.Default.Fastfood,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        items(items = uiState.orderItems, key = { it.id }) { item ->
                            SwipeableActionsBox(
                                modifier = Modifier
                                    .padding(
                                        8.dp,
                                    )
                                    .clip(RoundedCornerShape(12.dp)),
                                startActions = listOf(
                                    SwipeAction(
                                        icon = rememberVectorPainter(Icons.Default.Delete),
                                        background = MaterialTheme.colorScheme.error,
                                        onSwipe = {
                                            viewModel.onAction(Checkout.Action.RemoveOrderItem(item.id))
                                            isSaveOrderItems = true
                                        }
                                    )
                                )
                            ) {
                                OrderItemCheckoutView(
                                    orderItem = item,
                                    onIncrement = {
                                        viewModel.onAction(
                                            Checkout.Action.OnQuantityChange(
                                                it.id,
                                                it.quantity + 1
                                            )
                                        )
                                        isSaveOrderItems = false
                                    },
                                    onDecrement = {
                                        viewModel.onAction(
                                            Checkout.Action.OnQuantityChange(
                                                it.id,
                                                it.quantity - 1
                                            )
                                        )
                                        isSaveOrderItems = false
                                    }
                                )
                            }

                        }

                        item {
                            CheckoutDetailsView(
                                checkoutDetails = CheckoutDetails(
                                    totalAmount = totalPriceOrderItem
                                ),
                                voucher = uiState.checkout.voucher
                            )
                        }
                    }
                    VoucherCard(
                        voucher = uiState.checkout.voucher,
                        onVoucherClicked = {
                            viewModel.onAction(Checkout.Action.OnChooseVoucher)
                        }
                    )

                    AnimatedContent(
                        targetState = isSaveOrderItems,
                        modifier = Modifier.fillMaxWidth(),
                        transitionSpec = {
                            (slideInVertically { it } + fadeIn()) togetherWith
                                    (slideOutVertically { -it } + fadeOut())
                        },
                        label = "Edit button",
                    ) {
                        if (!it) {
                            LoadingButton(
                                onClick = {
                                    viewModel.onAction(Checkout.Action.SaveOrderItems)
                                    isSaveOrderItems = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                text = "Lưu",
                                loading = uiState.isLoading,
                            )
                        } else {
                            CheckoutRowItem(
                                title = "Tổng cộng",
                                value = totalPriceOrderItem + calculateVoucherValue(
                                    uiState.checkout.voucher,
                                    CheckoutDetails(totalPriceOrderItem)
                                ),
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                LoadingButton(
                                    text = "Xác nhận",
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        isCheckOut = true
                                        showDialog = true
                                    },
                                    loading = uiState.isLoading,

                                    )
                                AppButton(
                                    text = "Hủy",
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        isCheckOut = false
                                        showDialog = true
                                    },
                                    backgroundColor = MaterialTheme.colorScheme.error,
                                    enable = !uiState.isLoading

                                )
                            }
                        }
                    }
                }

            }
        }


    }
    if (showDialog) {
        FoodAppDialog(
            title = if (isCheckOut) "Thanh toán đơn hàng" else "Hủy đơn hàng",
            titleColor = if (isCheckOut) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            message = if (isCheckOut) "Bạn có muốn thanh toán đơn hàng này không?" else "Bạn có muốn hủy đơn hàng này không?",
            onDismiss = {
                showDialog = false
            },
            containerConfirmButtonColor = if (isCheckOut) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            onConfirm = {
                if (isCheckOut) {
                    viewModel.onAction(Checkout.Action.CheckoutOrder)
                } else {
                    viewModel.onAction(Checkout.Action.CancelOrder)
                }
            },
            confirmText = if (isCheckOut) "Thanh toán" else "Hủy",
            dismissText = "Đóng",
        )
    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error ?: "Đã xảy ra lỗi",
            onDismiss = {
                showErrorSheet = false
            },

            )
    }
}

@Composable
fun OrderItemCheckoutView(
    orderItem: OrderItem,
    onIncrement: (OrderItem) -> Unit,
    onDecrement: (OrderItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = orderItem.foodImage,
            contentDescription = null,
            modifier = Modifier
                .size(82.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = orderItem.foodName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )


            Spacer(modifier = Modifier.size(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StringUtils.formatCurrency(orderItem.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                FoodItemCounter(
                    count = orderItem.quantity,
                    onCounterIncrement = { onIncrement.invoke(orderItem) },
                    onCounterDecrement = { onDecrement.invoke(orderItem) },
                )
            }
        }
    }
}


@Composable
fun VoucherCard(voucher: Voucher?, onVoucherClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onVoucherClicked.invoke()
            }
            .padding(16.dp)

    ) {
        Row {
            Icon(
                imageVector = Icons.Default.LocalOffer,
                contentDescription = "Voucher áp dụng",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))
            if (voucher != null) {
                Column {
                    Text(
                        text = voucher.code,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "${voucher.value}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                Text(
                    text = "Chọn voucher",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.primary
            )
        }

    }

}


@Composable
fun Payment(method: String, onSelected: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp),
        contentAlignment = Alignment.Center

    ) {

        RadioGroupWrap(
            modifier = Modifier,
            text = "Phương thức thanh toán",
            options = PaymentMethod.entries.map { it.display },
            selectedOption = method,
            onOptionSelected = onSelected,
            optionIcons = listOf(Icons.Default.Money, Icons.Default.Payment),
        )


    }
}
