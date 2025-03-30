package com.se114.foodapp.ui.screen.address

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.ui.Loading
import com.example.foodapp.ui.navigation.AddAddress
import com.se114.foodapp.ui.screen.cart.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddressListScreen(
    navController: NavController,
    viewModel: AddressListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is AddressListViewModel.AddressEvent.NavigateToEditAddress -> {

                }

                is AddressListViewModel.AddressEvent.NavigateToAddAddress -> {
                    navController.navigate(AddAddress)
                }

                else -> {

                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = null,
                    modifier = Modifier
                       .size(24.dp)

                )
            }


            Text(
                text = "Danh sách địa chỉ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        viewModel.onAddAddressClicked()
                    })


        }

        when (state.value) {
            is AddressListViewModel.AddressState.Loading -> {
                Spacer(modifier = Modifier.size(16.dp))
                Loading()
            }

            is AddressListViewModel.AddressState.Success -> {
                val data = (state.value as AddressListViewModel.AddressState.Success).addresses
                if (data.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(data) { 

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
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Tải lại")
                    }

                }
            }
        }
    }

}