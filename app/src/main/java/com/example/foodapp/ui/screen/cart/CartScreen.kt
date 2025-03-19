package com.example.foodapp.ui.screen.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.ui.FoodItemCounter
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()




    Column(modifier = Modifier.fillMaxSize()) {
        CartHeaderView( onBack = { navController.popBackStack() })
        Spacer(modifier = Modifier.size(16.dp))

        when (uiState.value) {
            is CartViewModel.CartState.Loading -> {
                Spacer(modifier = Modifier.size(16.dp))
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(16.dp))
                    CircularProgressIndicator()
                    Text(
                        text = "Đang tải...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            is CartViewModel.CartState.Success -> {
                val cartItems = (uiState.value as CartViewModel.CartState.Success).cartItems
                val checkoutDetails = (uiState.value as CartViewModel.CartState.Success).checkoutDetails
                LazyColumn {
                    items(cartItems, key = { it.id }) {
                        CartItemView(
                            cartItem = it,
                            onIncrement = {
                                item, count -> viewModel.incrementQuantity(item, count)
                            },
                            onDecrement = {
                                item, count -> viewModel.decrementQuantity(item, count)
                            },
                            onRemove = { item -> viewModel.removeItem(item) }
                        )
                    }
                    item {
                        CheckoutDetailsView(
                            checkoutDetails = checkoutDetails
                        )
                    }
                }
            }

            is CartViewModel.CartState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val message = (uiState.value as CartViewModel.CartState.Error).message
                    Text(text = message, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Retry")
                    }

                }
            }
            CartViewModel.CartState.Nothing -> {}

        }
        Spacer(modifier = Modifier.weight(1f))
        if (uiState.value is CartViewModel.CartState.Success) {
            Button(
                onClick = {
                    viewModel.checkout()
                },
                modifier = Modifier.fillMaxWidth()

            ) {
                Text(text = "Thanh toán")
            }
        }


    }
}

@Composable
fun CheckoutDetailsView(
    checkoutDetails: CheckoutDetails
) {
    Column() {
        CheckoutRowItem(
            title = "Tổng giá",
            value = checkoutDetails.subTotal
        )
        CheckoutRowItem(
            title = "Thuế GTGT",
            value = checkoutDetails.tax
        )
        CheckoutRowItem(
            title = "Phí ship",
            value = checkoutDetails.deliveryFee
            )
        CheckoutRowItem(
            title = "Tổng cộng",
            value = checkoutDetails.totalAmount
        )
    }
}

@Composable
fun CheckoutRowItem(title: String, value: Float) {
    Row(
        modifier = Modifier
           .fillMaxWidth()
           .padding(vertical = 4.dp),

    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = StringUtils.formatCurrency(value),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
    VerticalDivider()
}

@Composable
fun CartItemView(
    cartItem: CartItem,
    onIncrement: (CartItem, Int) -> Unit,
    onDecrement: (CartItem, Int) -> Unit,
    onRemove: (CartItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = cartItem.menuItemId.imageUrl,
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
            Row {
                Text(
                    text = cartItem.menuItemId.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = cartItem.menuItemId.description,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodySmall
                )
                Row {
                    Text(
                        text = "$${cartItem.menuItemId.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    FoodItemCounter(
                        count = cartItem.quantity,
                        onCounterIncrement = { onIncrement.invoke(cartItem, cartItem.quantity) },
                        onCounterDecrement = { onDecrement.invoke(cartItem, cartItem.quantity) },
                    )
                }
            }
        }
    }
}

@Composable
fun CartHeaderView(
    onBack : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            onClick = onBack
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = null
            )
            Text(
                text = "Giỏ hàng",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(
                modifier = Modifier.size(8.dp)
            )
        }
    }
}