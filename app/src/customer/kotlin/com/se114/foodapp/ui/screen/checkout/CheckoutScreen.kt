package com.se114.foodapp.ui.screen.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.Payment
import com.example.foodapp.ui.BasicDialog
import com.example.foodapp.ui.FoodItemCounter
import com.example.foodapp.ui.HeaderDefaultView
import com.example.foodapp.ui.Loading
import com.example.foodapp.ui.Retry
import com.example.foodapp.ui.navigation.AddressList
import com.example.foodapp.ui.navigation.OrderSuccess

import com.se114.foodapp.ui.screen.cart.CartViewModel
import com.se114.foodapp.ui.screen.cart.CheckoutDetailsView
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

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

                CheckoutViewModel.CheckoutEvents.OnCheckOut -> {

                }

                CheckoutViewModel.CheckoutEvents.ShowErrorDialog -> {
                    showErrorDialog.value = true
                }

                is CheckoutViewModel.CheckoutEvents.OrderSuccess -> {
                    navController.navigate(OrderSuccess("Demo order completed"))
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally

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
        Spacer(modifier = Modifier.size(8.dp))

        when (uiState.value) {
            is CheckoutViewModel.CheckoutState.Loading -> {
                Spacer(modifier = Modifier.size(16.dp))
                Loading()
            }

            is CheckoutViewModel.CheckoutState.Success -> {
                val cartItems = (uiState.value as CheckoutViewModel.CheckoutState.Success).cartItems
                val checkoutDetails =
                    (uiState.value as CheckoutViewModel.CheckoutState.Success).checkoutDetails
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),

                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(cartItems, key = { it.id }) { item ->
                            ItemView(
                                cartItem = item
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                PaymentMethod()
                Spacer(modifier = Modifier.size(8.dp))
                CheckoutDetailsView(checkoutDetails = checkoutDetails)
                Button(
                    onClick = {
                        viewModel.onConfirmClicked()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Đặt món")
                }


            }


            is CheckoutViewModel.CheckoutState.Error -> {
                val message = (uiState.value as CartViewModel.CartState.Error).message
                Retry(
                    message,
                    onClicked = {}
                )
            }

            is CheckoutViewModel.CheckoutState.Nothing -> {}

        }
    }
    if (showErrorDialog.value) {
        ModalBottomSheet(onDismissRequest = { showErrorDialog.value = false }) {
            BasicDialog(title = viewModel.errorTitle, description = viewModel.errorMessage) {
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
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))
            if (address != null) {
                Column {
                    Text(
                        text = address.addressLine1,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "${address.city}, ${address.state}, ${address.country}",
                        style = MaterialTheme.typography.bodySmall,
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
fun ItemView(
    cartItem: CartItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AsyncImage(
            model = cartItem.menuItemId.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(82.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = cartItem.menuItemId.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = cartItem.menuItemId.description,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )


            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End

            ) {
                Text(
                    text = "SL: ${cartItem.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,

                    )
                Text(
                    text = StringUtils.formatCurrency(cartItem.menuItemId.price),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }


        }
    }


}

@Composable
fun PaymentMethod(payment: Payment = Payment()) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Phương thức thanh toán",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { /*TODO*/ },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = payment.paymentImage),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = payment.paymentMethod,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.primary
                )
            }


        }
    }
}
