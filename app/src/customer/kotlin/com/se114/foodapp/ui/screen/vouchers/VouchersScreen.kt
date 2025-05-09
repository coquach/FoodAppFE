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
    isMyVoucher: Boolean = false
) {

    val vouchers = viewModel.vouchers.collectAsLazyPagingItems()


    var search by remember { mutableStateOf("") }
    var showErrorSheet by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }


    val scope = rememberCoroutineScope()
    val context = LocalContext.current



    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            scope.launch {
                showErrorSheet = true
            }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                VouchersViewModel.VouchersEvents.showSuccessToast -> {
                    Toast.makeText(
                        context,
                        "Lấy voucher thành công",
                        Toast.LENGTH_SHORT
                    ).show()
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
                text = if(isMyVoucher)"Voucher của tôi" else "Voucher",
                onBack = {
                    navController.navigateUp()
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
                    if (!isMyVoucher) viewModel.receivedVoucher(it.id)
                }
            )


        }

    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            title = viewModel.error,
            description = viewModel.errorDescription,
            onDismiss = { showErrorSheet = false },
        )
    }


}
