package com.se114.foodapp.ui.screen.vouchers

import android.util.Log
import android.widget.Toast

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.LocalOffer

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle

import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems

import com.example.foodapp.ui.screen.common.VoucherCard

import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet

import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.SearchField

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VouchersScreen(
    navController: NavController,
    viewModel: VouchersViewModel = hiltViewModel(),
) {
    val vouchers = viewModel.vouchers.collectAsLazyPagingItems()
    var search by remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is Vouchers.Event.OnBack -> {
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
                    viewModel.onAction(Vouchers.Action.OnBack)
                },

                )
            SearchField(
                searchInput = search,
                searchChange = { search = it }
            )

        }
        LazyPagingSample(
            items = vouchers,
            textNothing = "Không có voucher nào cả...",
            iconNothing = Icons.Default.LocalOffer,
            columns = 1,
            key = {
                it.id
            }
        ) {

            VoucherCard(
                modifier = Modifier.fillMaxWidth(),
                voucher = it,
                onClick = {

                }
            )
        }
    }

}
