package com.example.foodapp.ui.screen.order.order_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.ui.HeaderDefaultView
import com.example.foodapp.ui.Loading
import com.example.foodapp.ui.Retry
import com.example.foodapp.ui.screen.order.OrderDetailsText
import com.example.foodapp.utils.StringUtils

@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String,
    viewModel: OrderDetailsViewModel = hiltViewModel()

) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    Column(Modifier.padding(horizontal = 16.dp)) {
        HeaderDefaultView(
            onBack = {
                navController.popBackStack()
            },
            text = "Chi tiết đơn hàng"
        )

        when (uiState.value) {
            is OrderDetailsViewModel.OrderDetailsState.Loading -> {
                Loading()
            }

            is OrderDetailsViewModel.OrderDetailsState.OrderDetails -> {
                val order =
                    (uiState.value as OrderDetailsViewModel.OrderDetailsState.OrderDetails).order
                OrderDetailsText(order)
                Row {
                    Text(text = "Price:")
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(text = StringUtils.formatCurrency(order.totalAmount))
                }
                Row {
                    Text(text = "Date:")
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(text = order.createdAt)
                }
                Row {
                    Image(
                        painter = painterResource(id = viewModel.getImage(order)),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(text = "${order.status}")
                }

            }

            is OrderDetailsViewModel.OrderDetailsState.Error -> {
                val message = (uiState.value as OrderDetailsViewModel.OrderDetailsState.Error).message
               Retry(
                   message,
                   onClicked = {

                   }
               )
            }
        }


    }
}