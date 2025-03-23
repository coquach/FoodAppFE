package com.example.foodapp.ui.screen.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails

import com.example.foodapp.ui.BasicDialog
import com.example.foodapp.ui.FoodItemCounter
import com.example.foodapp.ui.Loading
import com.example.foodapp.ui.navigation.AddressList
import com.example.foodapp.ui.navigation.Checkout
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val showErrorDialog = remember {
        mutableStateOf(
            false
        )
    }

    var isEditing by rememberSaveable { mutableStateOf(false) }
    var isSelectAll by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is CartViewModel.CartEvents.OnItemRemoveError,
                is CartViewModel.CartEvents.OnQuantityUpdateError,
                is CartViewModel.CartEvents.ShowErrorDialog -> {
                    showErrorDialog.value = true
                }
                is CartViewModel.CartEvents.OnAddress -> {
                    navController.navigate(AddressList)
                }
                is CartViewModel.CartEvents.NavigateToCheckOut -> {
                    navController.navigate(Checkout)
                }

                else -> {

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
        CartHeaderView(
            isEditing = isEditing,
            onBack = { navController.popBackStack() },
            onEditToggle = { isEditing = !isEditing }
        )
        Spacer(modifier = Modifier.size(16.dp))

        when (uiState.value) {
            is CartViewModel.CartState.Loading -> {
                Spacer(modifier = Modifier.size(16.dp))
                Loading()
            }

            is CartViewModel.CartState.Success -> {
                val cartItems = (uiState.value as CartViewModel.CartState.Success).cartItems
                val checkoutDetails =
                    (uiState.value as CartViewModel.CartState.Success).checkoutDetails
                if (cartItems.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(cartItems, key = { it.id }) { item ->
                            CartItemView(
                                cartItem = item,
                                isEditMode = isEditing,
                                isChecked = viewModel.selectedItems.contains(item),
                                onCheckedChange = { cartItem ->
                                    viewModel.toggleSelection(cartItem)
                                },
                                onIncrement = { item, _ ->
                                    viewModel.incrementQuantity(item)
                                },
                                onDecrement = { item, _ ->
                                    viewModel.decrementQuantity(item)
                                }
                            )
                        }

                    }
                    AnimatedVisibility(
                        visible = isEditing,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                    ) {
                        CartBottomBar(
                            onSelectAll = {
                                isSelectAll = !isSelectAll
                                viewModel.selectAllItems(cartItems, isSelectAll)
                            },
                            onDeleteSelected = { viewModel.removeItem() }
                        )
                    }
                    AnimatedVisibility(
                        visible = !isEditing,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                    ) {
                        Column {
                            CheckoutDetailsView(checkoutDetails = checkoutDetails)
                            Button(
                                onClick = { viewModel.checkout() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Thanh toán")
                            }
                        }
                    }

                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cart),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Text(
                            text = "Không có món nào trong giỏ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
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
                        Text(text = "Tải lại")
                    }

                }
            }

            CartViewModel.CartState.Nothing -> {}

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
fun CheckoutDetailsView(
    checkoutDetails: CheckoutDetails
) {
    Column {
        CheckoutRowItem(
            title = "Tổng giá", value = checkoutDetails.subTotal
        )
        CheckoutRowItem(
            title = "Thuế GTGT", value = checkoutDetails.tax
        )
        CheckoutRowItem(
            title = "Phí ship", value = checkoutDetails.deliveryFee
        )
        CheckoutRowItem(
            title = "Tổng cộng", value = checkoutDetails.totalAmount, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CheckoutRowItem(title: String, value: Float, fontWeight: FontWeight = FontWeight.Normal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),

        ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = fontWeight
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = StringUtils.formatCurrency(value),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = fontWeight
        )
    }

}

@Composable
fun CartItemView(
    cartItem: CartItem,
    isEditMode: Boolean = false,
    isChecked: Boolean,
    onCheckedChange: (CartItem) -> Unit,
    onIncrement: (CartItem, Int) -> Unit,
    onDecrement: (CartItem, Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = isEditMode,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onCheckedChange(cartItem) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
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

            Text(
                text = cartItem.menuItemId.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = cartItem.menuItemId.description,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(200.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StringUtils.formatCurrency(cartItem.menuItemId.price),
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




@Composable
fun CartBottomBar(
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onSelectAll) {
                Text(text = "Chọn tất cả", color = MaterialTheme.colorScheme.primary)
            }
            Button(
                onClick = onDeleteSelected,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "XÓA", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

@Composable
fun CartHeaderView(
    isEditing: Boolean,
    onBack: () -> Unit,
    onEditToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(CircleShape)
                .clickable { onBack.invoke() },
            tint = MaterialTheme.colorScheme.primary
        )


        Text(
            text = "Giỏ hàng",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        TextButton(onClick = { onEditToggle(!isEditing) }) {
            Text(
                text = if (isEditing) "Xong" else "Sửa",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(48.dp)
            )

        }
    }
}

