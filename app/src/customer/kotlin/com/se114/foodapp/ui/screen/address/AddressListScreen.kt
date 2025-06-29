package com.se114.foodapp.ui.screen.address

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.data.model.AddressUI
import com.example.foodapp.navigation.AddAddress
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.Nothing
import com.se114.foodapp.ui.screen.address.AddressList.Action.OnSelectAddress
import com.se114.foodapp.ui.screen.address.AddressList.Action.OnSelectToBackCheckOut
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun AddressListScreen(
    navController: NavController,
    viewModel: AddressListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }

    val address =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<AddressUI?>(
            "address",
            null
        )
            ?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = address?.value) {
        address?.value?.let {
            viewModel.onAction(AddressList.Action.AddAddress(it))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getAddress()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {

                is AddressList.Event.NavigateToAddAddress -> {
                    navController.navigate(AddAddress)
                }

                AddressList.Event.OnBack -> {
                    navController.popBackStack()
                }

                is AddressList.Event.BackToCheckout -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "address",
                        it.address
                    )
                    navController.popBackStack()
                }

                AddressList.Event.ShowError -> {
                    showErrorSheet = true

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
                viewModel.onAction(AddressList.Action.OnBack)
            },
            icon = Icons.Default.AddLocationAlt,
            iconClick = {
                viewModel.onAction(AddressList.Action.OnAddAddress)
            },
            tintIcon = MaterialTheme.colorScheme.primary
        )
        when (uiState.addressesState) {
            is AddressList.AddressesState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val message =
                        (uiState.addressesState as AddressList.AddressesState.Error).message
                    Text(text = message, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = {  }) {
                        Text(text = "Tải lại")
                    }

                }
            }

            AddressList.AddressesState.Loading -> {
                Loading()
            }

            AddressList.AddressesState.Success -> {
                if (uiState.addresses.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)

                    ) {
                        itemsIndexed(uiState.addresses) { index, it ->
                            SwipeableActionsBox(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(28.dp)),
                                endActions = listOf(
                                    SwipeAction(
                                        icon = rememberVectorPainter(Icons.Default.Close),
                                        background = MaterialTheme.colorScheme.error,
                                        onSwipe = {
                                            viewModel.onAction(OnSelectAddress(it.id))
                                            showDeleteDialog = true
                                        }
                                    )
                                )
                            ) {
                                AddressSection(
                                    index = index + 1,
                                    address = it.formatAddress,
                                    onClick = {
                                        Log.d("AddressListScreen", "AddressSection: $it")
                                        if (uiState.isCheckout) viewModel.onAction(
                                            OnSelectToBackCheckOut(
                                                it
                                            )
                                        )
                                    },
                                )
                            }

                        }
                    }
                }else{
                    Nothing(
                        icon = Icons.Default.LocationOn,
                        text = "Không có địa chỉ nào",
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }

            null -> {}
        }

        if (showDeleteDialog) {
            FoodAppDialog(
                title = "Xóa địa chỉ",
                message = "Bạn có chắc chắn muốn xóa địa chỉ này không?",
                onDismiss = {
                    showDeleteDialog = false
                    viewModel.onAction(OnSelectAddress(null))
                },
                onConfirm = {
                    viewModel.onAction(AddressList.Action.OnDeleteAddress(uiState.selectedAddress!!))
                    showDeleteDialog = false
                    viewModel.onAction(OnSelectAddress(null))

                },
                confirmText = "Xóa",
                dismissText = "Đóng",
                showConfirmButton = true
            )
        }

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
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(28.dp)
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
