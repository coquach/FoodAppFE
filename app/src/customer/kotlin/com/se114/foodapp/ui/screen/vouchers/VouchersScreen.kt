package com.se114.foodapp.ui.screen.vouchers

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.ui.screen.common.VoucherCard
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.SearchField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VouchersScreen(
    navController: NavController,
    viewModel: VouchersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val vouchers = viewModel.vouchers.collectAsLazyPagingItems()


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


        HeaderDefaultView(
            text = "Voucher",
            onBack = {
                viewModel.onAction(Vouchers.Action.OnBack)
            },

            )
        SearchField(
            searchInput = uiState.nameSearch,
            searchChange = {
                viewModel.onAction(Vouchers.Action.OnChangeNameSearch(it))
            },
            searchFilter = {
                viewModel.onAction(Vouchers.Action.OnSearchFilter)
            },
            switchState = uiState.filter.order == "desc",
            switchChange = {
                when (it) {
                    true -> viewModel.onAction(Vouchers.Action.OnChangeOrder("desc"))
                    false -> viewModel.onAction(Vouchers.Action.OnChangeOrder("asc"))
                }
            },
            filterChange = {
                when (it) {
                    "Giá trị" -> viewModel.onAction(Vouchers.Action.OnChangeSortByName("value"))
                    "Số lượng" -> viewModel.onAction(Vouchers.Action.OnChangeSortByName("quantity"))
                }
            },
            filters = listOf("Giá trị", "Số lượng"),
            filterSelected = when (uiState.filter.sortBy) {
                "value" -> "Giá trị"
                "quantity" -> "Số lượng"
                else -> "Giá trị"
            },
            placeHolder = "Tìm kiếm theo tên voucher..."
        )


        LazyPagingSample(
            modifier = Modifier.fillMaxWidth().weight(1f),
            items = vouchers,
            textNothing = "Không có voucher nào cả...",
            iconNothing = Icons.Default.LocalOffer,
            columns = 1,
            key = {
                it.id!!
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
