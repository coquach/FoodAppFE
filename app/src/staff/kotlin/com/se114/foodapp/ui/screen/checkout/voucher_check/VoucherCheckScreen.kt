package com.se114.foodapp.ui.screen.checkout.voucher_check

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.ui.screen.common.VoucherCard
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample

@Composable
fun VoucherCheckScreen(
    navController: NavController,
    viewModel: VoucherCheckViewModel = hiltViewModel(),
) {

    val vouchers = viewModel.vouchers.collectAsLazyPagingItems()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is VoucherCheck.Event.OnBackToCheckout -> {
                    val voucher = it.voucher
                    navController.previousBackStackEntry?.savedStateHandle?.set("voucher", voucher)
                    navController.popBackStack()

                }

                VoucherCheck.Event.OnBack -> {
                    navController.popBackStack()
                }
            }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HeaderDefaultView(
                text = "Voucher",
                onBack = {
                    navController.navigateUp()
                },

                )

        }
        LazyPagingSample(
            items = vouchers,
            textNothing = "Không có voucher nào cả...",
            iconNothing = Icons.Default.LocalOffer,
            columns = 1,
            key = {
                it.id!!
            },
            modifier = Modifier.fillMaxWidth().weight(1f),
            itemContent = {
                VoucherCard(
                    modifier = Modifier.fillMaxWidth(),
                    voucher = it,
                    onClick = {
                        viewModel.onAction(VoucherCheck.Action.OnVoucherSelected(it))
                    }
                )
            },

            )
    }

}
