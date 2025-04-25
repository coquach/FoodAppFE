package com.se114.foodapp.ui.screen.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ShoppingCart

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails

import com.example.foodapp.ui.screen.components.BasicDialog
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.Retry
import com.example.foodapp.ui.navigation.AddressList
import com.example.foodapp.ui.navigation.Checkout
import com.example.foodapp.ui.screen.common.CheckoutRowItem
import com.example.foodapp.ui.screen.components.DeleteBar
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val quantityMap by viewModel.quantityMap.collectAsStateWithLifecycle()
    val checkoutDetails by viewModel.checkoutDetails.collectAsStateWithLifecycle()

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)


    ) {
        CartHeaderView(
            isEditing = isEditing,
            onBack = { navController.popBackStack() },
            onEditToggle = { isEditing = !isEditing }
        )

        if (cartItems.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(cartItems, key = { it.id!! }) { item ->
                    val quantity = quantityMap[item.id] ?: item.quantity
                    CartItemView(
                        cartItem = item,
                        isEditMode = isEditing,
                        quantity = quantity,
                        isChecked = viewModel.selectedItems.contains(item),
                        onCheckedChange = { cartItem ->
                            viewModel.toggleSelection(cartItem)
                        },
                        onIncrement = { item ->
                            viewModel.increment(item)
                        },
                        onDecrement = { item ->
                            viewModel.decrement(item)
                        }
                    )
                }

            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)

            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = isEditing,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    DeleteBar(
                        onSelectAll = {
                            isSelectAll = !isSelectAll
                            viewModel.selectAllItems(cartItems, isSelectAll)
                        },
                        onDeleteSelected = { viewModel.removeItem() }
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = !isEditing,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Column {
                        CheckoutRowItem(
                            title = "Tổng cộng",
                            value = checkoutDetails.subTotal,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { viewModel.checkout() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Thanh toán")
                        }
                    }
                }
            }


        } else {
            Nothing(
                icon = Icons.Default.ShoppingCart,
                text = "Không có món nào trong giỏ hàng"
            )
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
fun CartItemView(
    cartItem: CartItem,
    quantity: Int,
    isEditMode: Boolean = false,
    isChecked: Boolean,
    onCheckedChange: (CartItem) -> Unit,
    onIncrement: (CartItem) -> Unit,
    onDecrement: (CartItem) -> Unit,
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
            model = cartItem.imageUrl,
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
                text = cartItem.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = cartItem.menuName,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(200.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StringUtils.formatCurrency(cartItem.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                FoodItemCounter(
                    count = quantity,
                    onCounterIncrement = { onIncrement.invoke(cartItem) },
                    onCounterDecrement = { onDecrement.invoke(cartItem) },
                )
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
                .padding(end = 16.dp)
                .size(30.dp)
                .clip(CircleShape)
                .clickable { onBack.invoke() },
            tint = MaterialTheme.colorScheme.primary
        )


        Text(
            text = "Giỏ hàng",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
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

