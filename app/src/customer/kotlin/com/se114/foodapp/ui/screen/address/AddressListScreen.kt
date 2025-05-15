package com.se114.foodapp.ui.screen.address

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.room.Index
import com.example.foodapp.R
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.navigation.AddAddress
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.confirm
import com.mapbox.maps.extension.compose.style.layers.generated.ModelLayer
import kotlinx.coroutines.flow.collectLatest
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun AddressListScreen(
    navController: NavController,
    isCheckout: Boolean = false,
    viewModel: AddressListViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    val addressList = viewModel.addressList.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf<String?>(null) }


    val address =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Address?>(
            "address",
            null
        )
            ?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = address?.value) {
        address?.value?.let {
            viewModel.addAddress(it)
        }
    }



    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {

                is AddressListViewModel.AddressEvent.NavigateToAddAddress -> {
                    navController.navigate(AddAddress)
                }

                AddressListViewModel.AddressEvent.OnBack -> {
                    navController.popBackStack()
                }

                is AddressListViewModel.AddressEvent.NavigateToCheckout -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "address",
                        it.address
                    )
                    navController.popBackStack()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderDefaultView(
            text = "Địa chỉ của tôi",
            onBack = {
                viewModel.onBackClicked()
            },
            icon = Icons.Default.AddLocationAlt,
            iconClick = {
                viewModel.onAddAddressClicked()
            },
            tintIcon = MaterialTheme.colorScheme.primary
        )

        when (state.value) {
            is AddressListViewModel.AddressState.Loading -> {
                Loading()
            }

            is AddressListViewModel.AddressState.Success -> {

                if (addressList.value.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)

                    ) {
                        itemsIndexed(addressList.value) { index, it ->
                            SwipeableActionsBox(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(28.dp)),
                                endActions = listOf(
                                    SwipeAction(
                                        icon = rememberVectorPainter(Icons.Default.Close),
                                        background = MaterialTheme.colorScheme.error,
                                        onSwipe = {
                                            selectedAddress = it.id
                                            showDeleteDialog = true
                                        }
                                    )
                                )
                            ) {
                                AddressSection(
                                    index = index + 1,
                                    address = it.formatAddress,
                                    onClick = {
                                        if (isCheckout) viewModel.navigateToCheckout(it.formatAddress)
                                    },
                                )
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
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(24.dp)
                        )
                        Text(
                            text = "Không có địa chỉ nào",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

            }

            is AddressListViewModel.AddressState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val message = (state.value as AddressListViewModel.AddressState.Error).message
                    Text(text = message, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = { viewModel.getAddress() }) {
                        Text(text = "Tải lại")
                    }

                }
            }
        }
    }

    if (showDeleteDialog) {
        FoodAppDialog(
            title = "Xóa địa chỉ",
            message = "Bạn có chắc chắn muốn xóa địa chỉ này không?",
            onDismiss = {
                showDeleteDialog = false
                selectedAddress = null
            },
            onConfirm = {
                viewModel.deleteAddress(selectedAddress!!)
                showDeleteDialog = false
                selectedAddress = null

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )
    }


}


@Composable
fun AddressSection(
    index: Int,
    address: String,
    onClick: (() -> Unit),
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .shadow(12.dp, RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.background

                        )
                    )
                )
                .clickable {
                    onClick.invoke()
                }
                .padding(vertical = 10.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()



            ) {
                Text(
                    text = "Địa chỉ $index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = address,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

        }
    }

}
