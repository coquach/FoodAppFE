package com.se114.foodapp.ui.screen.checkout

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.BaseViewModel
import com.example.foodapp.R
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.PaymentMethod

import com.example.foodapp.ui.screen.components.BasicDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView

import com.example.foodapp.ui.navigation.AddressList
import com.example.foodapp.ui.navigation.MyVoucher
import com.example.foodapp.ui.navigation.OrderSuccess
import com.example.foodapp.ui.navigation.VoucherCustomerCheck
import com.example.foodapp.ui.screen.common.CartItemView
import com.example.foodapp.ui.screen.common.CheckoutDetailsView
import com.example.foodapp.ui.screen.common.CheckoutRowItem

import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.NoteInput
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.example.foodapp.ui.screen.components.Retry
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel(),
    isCustomer: Boolean = true,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val checkoutDetails by viewModel.checkoutDetails.collectAsStateWithLifecycle()
    val checkoutRequest by viewModel.checkoutRequest.collectAsStateWithLifecycle()

    var isLoading by remember {
        mutableStateOf(false)
    }

    val showErrorDialog = remember {
        mutableStateOf(
            false
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is CheckoutViewModel.CheckoutEvents.OnAddress -> {
                    navController.navigate(AddressList)
                }


                CheckoutViewModel.CheckoutEvents.ShowErrorDialog -> {
                    showErrorDialog.value = true
                }

                is CheckoutViewModel.CheckoutEvents.OrderSuccess -> {
                    navController.navigate(OrderSuccess(it.orderId!!))
                }

                CheckoutViewModel.CheckoutEvents.OnBack -> {
                    navController.popBackStack()
                }

                CheckoutViewModel.CheckoutEvents.OnCustomerVoucher -> {
                    navController.navigate(VoucherCustomerCheck)
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
            viewModel.onVoucherChanged(it)
        }
    }

    when (uiState.value) {
        is BaseViewModel.ResultState.Loading -> {
            isLoading = true
        }

        is BaseViewModel.ResultState.Error -> {
            isLoading = false
            showErrorDialog.value = true
        }

        else -> {
            isLoading = false
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
                navController.popBackStack()
            },
            text = "Xác nhận đơn hàng"
        )
        AddressCard(
            null,
            onAddressClicked = {
                viewModel.onAddressClicked()
            }
        )
        NoteInput(
            note = checkoutRequest.note ?: "",
            onNoteChange = {
                viewModel.onNoteChanged(it)
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
            color = MaterialTheme.colorScheme.surface,
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
                    items(cartItems, key = { it.id }) { item ->
                        CartItemView(
                            cartItem = item
                        )
                    }

                    item {
                        CheckoutDetailsView(checkoutDetails = checkoutDetails, voucherValue = BigDecimal(checkoutRequest.voucher?.value ?: 0.0))
                    }
                }


            }

        }
        VoucherCard(
            voucher = checkoutRequest.voucher,
            onVoucherClicked = {
                viewModel.onVoucherClicked()
            }
        )
        Payment(
            method = checkoutRequest.method,
            onSelected = {
                val method = PaymentMethod.fromDisplay(it)!!.name
                viewModel.onPaymentMethodChanged(it)
            }
        )
        CheckoutRowItem(
            title = "Tổng cộng",
            value = checkoutDetails.totalAmount + BigDecimal(checkoutRequest.voucher?.value ?: 0.0),
            fontWeight = FontWeight.Bold
        )
        LoadingButton(
            onClick = {
                if (isCustomer) viewModel.onCustomerConfirmClicked()
            },
            text = "Hoàn thành",
            loading = isLoading,
            modifier = Modifier.fillMaxWidth()

        )
    }
    if (showErrorDialog.value) {
        ModalBottomSheet(onDismissRequest = { showErrorDialog.value = false }) {
            BasicDialog(title = viewModel.error, description = viewModel.errorDescription) {
                showErrorDialog.value = false
            }
        }
    }
}

@Composable
fun AddressCard(address: Address?, onAddressClicked: () -> Unit) {
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
        Row {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Vị trí của bạn",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))
            if (address != null) {
                Column {
                    Text(
                        text = address.formatAddress,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                Text(
                    text = "Chọn địa chỉ",
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
