package com.se114.foodapp.ui.screen.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.data.model.CheckoutUiModel
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.navigation.OrderSuccess
import com.example.foodapp.navigation.VoucherCheck
import com.example.foodapp.ui.screen.common.CartItemView
import com.example.foodapp.ui.screen.common.CheckoutDetailsView
import com.example.foodapp.ui.screen.common.CheckoutRowItem
import com.example.foodapp.ui.screen.common.calculateVoucherValue
import com.example.foodapp.ui.screen.components.CustomPagingDropdown
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.NoteInput
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.example.foodapp.ui.screen.components.TabWithPager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foodTables = viewModel.foodTables.collectAsLazyPagingItems()

    var showErrorSheet by rememberSaveable {
        mutableStateOf(
            false
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {

                Checkout.Event.ChooseVoucher -> {
                    navController.navigate(VoucherCheck)
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
            text = "Xác nhận đơn hàng"
        )
        TabWithPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            tabs = listOf("Tại quán", "Mang về"),
            pages = listOf(
                {
                    CheckoutSection(
                        modifier = Modifier
                            .fillMaxSize(),
                        isInStore = true,
                        checkoutDetails = uiState.checkoutDetails,
                        cartItems = uiState.cartItems,
                        onPlaceOrder = {
                            viewModel.onAction(Checkout.Action.PlaceOrder)
                        },
                        onNoteChange = {
                            viewModel.onAction(Checkout.Action.OnNoteChanged(it))
                        },
                        onPaymentMethodChange = {
                            viewModel.onAction(Checkout.Action.OnPaymentMethodChanged(it))
                        },
                        onFoodTableIdChange = {
                            viewModel.onAction(Checkout.Action.OnFoodTableIdChanged(it))
                        },
                        onChooseVoucher = {
                            viewModel.onAction(Checkout.Action.OnChooseVoucher)
                        },
                        checkOut = uiState.checkout,
                        isLoading = uiState.isLoading,
                        foodTables = foodTables
                    )
                },
                {
                    CheckoutSection(
                        modifier = Modifier
                            .fillMaxSize(),
                        isInStore = false,
                        checkoutDetails = uiState.checkoutDetails,
                        cartItems = uiState.cartItems,
                        onPlaceOrder = {
                            viewModel.onAction(Checkout.Action.PlaceOrder)
                        },
                        onNoteChange = {
                            viewModel.onAction(Checkout.Action.OnNoteChanged(it))
                        },
                        onPaymentMethodChange = {
                            viewModel.onAction(Checkout.Action.OnPaymentMethodChanged(it))
                        },
                        onChooseVoucher = {
                            viewModel.onAction(Checkout.Action.OnChooseVoucher)
                        },
                        checkOut = uiState.checkout,
                        isLoading = uiState.isLoading,
                        foodTables = foodTables
                    )
                }
            ),

            onTabSelected = {
                viewModel.onAction(Checkout.Action.OnServingTypeChanged(it))
            },
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
fun CheckoutSection(
    modifier: Modifier = Modifier,
    isInStore: Boolean,
    checkoutDetails: CheckoutDetails,
    cartItems: List<CartItem>,
    onPlaceOrder: () -> Unit,
    onNoteChange: (String) -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onFoodTableIdChange: ((Int) -> Unit)? = null,
    foodTables: LazyPagingItems<FoodTable>,
    onChooseVoucher: () -> Unit,
    checkOut: CheckoutUiModel,
    isLoading: Boolean,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isInStore) {
                CustomPagingDropdown(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Chọn bàn",
                    textPlaceholder = "Chọn bàn ăn cho hóa đơn",
                    selected = checkOut.foodTableId.toString(),
                    enabled = true,
                    dropdownContent = {onDismissDropdown ->
                        FoodTableComboBox(
                            items = foodTables,
                            onFoodTableClicked = { tableId->
                                onFoodTableIdChange?.invoke(tableId)
                                onDismissDropdown()
                            }
                        )
                    }
                )
            }
            NoteInput(
                note = checkOut.note,
                onNoteChange = onNoteChange,
                maxLines = 2,
                textHolder = "Nhập ghi chú",
                modifier = Modifier.height(100.dp)
            )
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 6.dp,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(cartItems, key = { it.id }) { item ->
                            CartItemView(
                                cartItem = item
                            )
                        }

                        item {
                            CheckoutDetailsView(
                                checkoutDetails = checkoutDetails,
                                voucher = checkOut.voucher
                            )
                        }
                    }


                }

            }
            VoucherCard(
                voucher = checkOut.voucher,
                onVoucherClicked = onChooseVoucher
            )
            Payment(
                method = checkOut.method,
                onSelected = {
                    val method = PaymentMethod.fromDisplay(it)!!.name
                    onPaymentMethodChange(method)
                }
            )
        }

        CheckoutRowItem(
            title = "Tổng cộng",
            value = checkoutDetails.totalAmount + calculateVoucherValue(
                checkOut.voucher,
                checkoutDetails
            ),
            fontWeight = FontWeight.Bold
        )
        LoadingButton(
            onClick = onPlaceOrder,
            text = "Hoàn thành",
            loading = isLoading,
            modifier = Modifier.fillMaxWidth()

        )
    }
}

@Composable
fun FoodTableComboBox(
    items: LazyPagingItems<FoodTable>,
    onFoodTableClicked: (Int) -> Unit
){
    LazyPagingSample(
        modifier = Modifier.fillMaxSize(),
        items = items,
        textNothing = "Không có bàn nào cả",
        iconNothing = Icons.Default.TableRestaurant,
        columns = 1,
        key = {
            it.id!!
        }
    ) {
        DropdownMenuItem(
                text = { Text(text = "${it.tableNumber}") },
        onClick = {
            onFoodTableClicked(it.id!!)
        }
        )
    }
}


@Composable
fun VoucherCard(voucher: Voucher?, onVoucherClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
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
