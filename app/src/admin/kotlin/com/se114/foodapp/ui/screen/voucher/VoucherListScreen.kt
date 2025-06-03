package com.se114.foodapp.ui.screen.voucher


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.VoucherType
import com.example.foodapp.ui.screen.common.VoucherCard
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.example.foodapp.ui.screen.components.SearchField
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherListScreen(
    navController: NavController,
    viewModel: VoucherListViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val vouchers = viewModel.vouchers.collectAsLazyPagingItems()


    var showVoucherDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                VoucherSate.Event.OnBack -> {
                    navController.popBackStack()
                }

                VoucherSate.Event.Refresh -> {
                    vouchers.refresh()
                }

                VoucherSate.Event.ShowError -> {
                    showErrorSheet = true
                }
            }
        }

    }



    Scaffold(
        floatingActionButton =
            {
                MyFloatingActionButton(
                    onClick = {
                        viewModel.onAction(VoucherSate.Action.OnVoucherSelected(Voucher()))
                        viewModel.onAction(VoucherSate.Action.OnUpdateStatus(false))
                        showVoucherDialog = true
                    },
                    bgColor = MaterialTheme.colorScheme.primary,
                ) {
                    Box(modifier = Modifier.size(56.dp)) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Center)
                                .size(35.dp)
                        )
                    }
                }
            }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        start = padding.calculateStartPadding(LayoutDirection.Ltr),
                        end = padding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                )
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {


            HeaderDefaultView(
                text = "Voucher",
                onBack = {
                    viewModel.onAction(VoucherSate.Action.OnBack)
                },

                )



            LazyPagingSample(
                modifier = Modifier.weight(1f),
                items = vouchers,
                textNothing = "Không có voucher nào cả...",
                iconNothing = Icons.Default.LocalOffer,
                columns = 1,
                key = {
                    it.id!!
                }
            ) {
                SwipeableActionsBox(
                    modifier = Modifier
                        .padding(
                            8.dp,
                        )
                        .clip(RoundedCornerShape(12.dp)),
                    endActions = listOf(
                        SwipeAction(
                            icon = rememberVectorPainter(Icons.Default.Visibility),
                            background = MaterialTheme.colorScheme.error,
                            onSwipe = {
                                viewModel.onAction(VoucherSate.Action.OnVoucherSelected(it))
                                showDeleteDialog = true
                            }
                        ))
                ) {
                    VoucherCard(
                        modifier = Modifier.fillMaxWidth(),
                        voucher = it,
                        onClick = {
                            viewModel.onAction(VoucherSate.Action.OnVoucherSelected(it))
                           viewModel.onAction(VoucherSate.Action.OnUpdateStatus(true))
                            showVoucherDialog = true
                        }
                    )
                }

            }

        }
        if (showErrorSheet) {
            ErrorModalBottomSheet(
                description = uiState.error ?: "Lỗi không xác định",
                onDismiss = { showErrorSheet = false },
            )
        }
        if (showVoucherDialog) {
            Dialog(
                onDismissRequest = {
                    showVoucherDialog = false
                },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.inversePrimary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(30.dp)

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),

                        ) {


                        Text(
                            text = "Thông tin",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        FoodAppTextField(
                            labelText = "Mã code",
                            value = uiState.voucherSelected.code,
                            onValueChange = {
                                viewModel.onAction(VoucherSate.Action.OnCodeChange(it))
                            },
                            singleLine = true
                        )
                        ChipsGroupWrap(
                            text = "Loại",
                            options = VoucherType.entries.map { it.name },
                            selectedOption = uiState.voucherSelected.type,
                            onOptionSelected = {
                                viewModel.onAction(VoucherSate.Action.OnTypeChange(it))
                            },
                            isFlowLayout = false,
                            shouldSelectDefaultOption = true
                        )
                        if (uiState.voucherSelected.type == VoucherType.PERCENTAGE.name) {
                            FoodAppTextField(
                                labelText = "Giảm tối đa",
                                value = uiState.voucherSelected.maxValue.toPlainString(),
                                onValueChange = {
                                    viewModel.onAction(VoucherSate.Action.OnMaxValueChange(it.toBigDecimalOrNull()))
                                },
                                singleLine = true
                            )
                        }
                        FoodAppTextField(
                            labelText = "Giá trị",
                            value = uiState.voucherSelected.value.toString(),
                            onValueChange = {
                                viewModel.onAction(VoucherSate.Action.OnValueChange(it.toDoubleOrNull()))
                            },
                            singleLine = true
                        )
                        FoodAppTextField(
                            labelText = "Đơn tối thiểu",
                            value = uiState.voucherSelected.minOrderPrice.toPlainString(),
                            onValueChange = {
                                viewModel.onAction(VoucherSate.Action.OnMinOrderPriceChange(it.toBigDecimalOrNull()))
                            },
                            singleLine = true
                        )
                        FoodAppTextField(
                            labelText = "Số lượng",
                            value =  uiState.voucherSelected.quantity.toString(),
                            onValueChange = {
                                viewModel.onAction(VoucherSate.Action.OnQuantityChange(it.toIntOrNull()))
                            },
                            singleLine = true
                        )
                        DateRangePickerSample(
                            startDate = uiState.voucherSelected.startDate,
                            endDate = uiState.voucherSelected.endDate,
                            modifier = Modifier.fillMaxWidth(),
                            isColumn = true,
                            onDateRangeSelected = { startDate, endDate ->
                                viewModel.onAction(VoucherSate.Action.OnStartDateChange(startDate))
                                viewModel.onAction(VoucherSate.Action.OnEndDateChange(endDate))
                            }
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                        ) {
                            Button(
                                onClick = {
                                    showVoucherDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.heightIn(48.dp),
                                shape = RoundedCornerShape(12.dp)


                            ) {
                                Text(text = "Đóng", modifier = Modifier.padding(horizontal = 16.dp))
                            }


                            Button(
                                onClick = {

                                    if (uiState.isUpdating) {
                                        viewModel.onAction(VoucherSate.Action.OnUpdateVoucher)

                                    } else viewModel.onAction(VoucherSate.Action.OnAddVoucher)

                                    showVoucherDialog = false

                                },
                                modifier = Modifier.heightIn(48.dp),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = if (uiState.isUpdating) "Cập nhật" else "Tạo",
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }


                        }
                    }
                }
            }

        }
        if (showDeleteDialog) {
            FoodAppDialog(
                title = "Xóa voucher",
                message = "Bạn có chắc chắn muốn xóa voucher này không?",
                onDismiss = {

                    showDeleteDialog = false

                },
                onConfirm = {

                    viewModel.onAction(VoucherSate.Action.OnDeleteVoucher)
                    showDeleteDialog = false

                },
                confirmText = "Xóa",
                dismissText = "Đóng",
                showConfirmButton = true
            )
        }

    }
}