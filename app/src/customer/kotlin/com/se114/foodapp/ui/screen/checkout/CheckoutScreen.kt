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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money

import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController

import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.navigation.MyAddressList
import com.example.foodapp.navigation.OrderSuccess
import com.example.foodapp.navigation.VoucherCheck
import com.example.foodapp.ui.screen.components.BasicDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.common.CartItemView
import com.example.foodapp.ui.screen.common.CheckoutDetailsView
import com.example.foodapp.ui.screen.common.CheckoutRowItem
import com.example.foodapp.ui.screen.common.calculateVoucherValue
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.NoteInput
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import java.math.BigDecimal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showErrorSheet by remember {
        mutableStateOf(
            false
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                Checkout.Event.ChooseAddress -> {
                    navController.navigate(MyAddressList(isCheckout = true))
                }

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

    val address =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<String?>(
            "address",
            null
        )
            ?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = address?.value) {
        address?.value?.let {
            viewModel.onAction(Checkout.Action.OnAddressChanged(it))
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
        AddressCard(
            uiState.checkout.address,
            onAddressClicked = {
                viewModel.onAction(Checkout.Action.OnChooseAddress)
            }
        )
        NoteInput(
            note = uiState.checkout.note?: "",
            onNoteChange = {
                viewModel.onAction(Checkout.Action.OnNoteChanged(it))
            },
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
                    .padding(10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.cartItems, key = { it.id }) { item ->
                        CartItemView(
                            cartItem = item
                        )
                    }

                    item {
                        CheckoutDetailsView(
                            checkoutDetails = uiState.checkoutDetails,
                            voucher = uiState.checkout.voucher
                        )
                    }
                }


            }

        }
        VoucherCard(
            voucher = uiState.checkout.voucher,
            onVoucherClicked = {
                viewModel.onAction(Checkout.Action.OnChooseVoucher)
            }
        )
        Payment(
            method = uiState.checkout.method,
            onSelected = {
                val method = PaymentMethod.fromDisplay(it)!!.name
                viewModel.onAction(Checkout.Action.OnPaymentMethodChanged(method))
            }
        )
        CheckoutRowItem(
            title = "Tổng cộng",
            value =  uiState.checkoutDetails.totalAmount + calculateVoucherValue(uiState.checkout.voucher,uiState.checkoutDetails),
            fontWeight = FontWeight.Bold
        )
        LoadingButton(
            onClick = {
                viewModel.onAction(Checkout.Action.PlaceOrder)
            },
            text = "Hoàn thành",
            loading = uiState.isLoading,
            modifier = Modifier.fillMaxWidth()

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
fun AddressCard(address: String?, onAddressClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onAddressClicked.invoke()
            }
            .padding(16.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Vị trí của bạn",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )


            if (address != null) {

                Text(
                    text = address,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)

                )

            } else {
                Text(
                    text = "Chọn địa chỉ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }

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
                contentDescription = "Voucher của bạn",
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
