package com.example.foodapp.ui.screen.voucher


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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
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
    val voucherRequest by viewModel.voucherRequest.collectAsStateWithLifecycle()

    var isUpdating by remember { mutableStateOf(false) }
    var showVoucherDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedVoucher by remember { mutableStateOf<Long?>(null) }

    var search by remember { mutableStateOf("") }

    var showErrorSheet by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }


    val scope = rememberCoroutineScope()



    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            scope.launch {
                showErrorSheet = true
            }
    }

    when(uiState) {
        VoucherListViewModel.VoucherListState.Error -> {
            errorMessage.value = "Failed"
        }
        else -> {
            errorMessage.value = null
        }
    }

    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    viewModel.loadVoucher()
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
                SearchField(
                    searchInput = search,
                    searchChange = { search = it }
                )

            }
            LazyPagingSample(
                modifier = Modifier.weight(1f),
                items = vouchers,
                textNothing = "Không có voucher nào cả...",
                iconNothing = Icons.Default.LocalOffer,
                columns = 1,
                key = {
                    it.id
                }
            ) {
                SwipeableActionsBox(
                    modifier = Modifier
                        .padding(
                            8.dp,
                        )
                        .clip(RoundedCornerShape(12.dp)),
                    endActions = listOf(SwipeAction(
                        icon = rememberVectorPainter(Icons.Default.Visibility),
                        background = MaterialTheme.colorScheme.error,
                        onSwipe = {
                            selectedVoucher = it.id
                            showDeleteDialog = true
                        }
                    ))
                ) {
                    VoucherCard(
                        modifier = Modifier.fillMaxWidth(),
                        voucher = it,
                        onClick = {
                            viewModel.loadVoucher(it)
                            selectedVoucher = it.id
                            isUpdating = true
                            showVoucherDialog = true
                        }
                    )
                }

            }

        }
        if (showErrorSheet) {
            ErrorModalBottomSheet(
                title = viewModel.error,
                description = viewModel.errorDescription,
                onDismiss = { showErrorSheet = false },
            )
        }
        if (showVoucherDialog) {
            Dialog(
                onDismissRequest = {
                    showVoucherDialog = false
                    isUpdating = false
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
                            value = voucherRequest.code,
                            onValueChange = {
                                viewModel.onCodeChange(it)
                            },
                            singleLine = true
                        )
                        ChipsGroupWrap(
                            text = "Loại",
                            options = VoucherType.entries.map { it.name },
                            selectedOption = voucherRequest.type,
                            onOptionSelected = {
                                viewModel.onTypeChange(it)
                            },
                            isFlowLayout = false,
                            shouldSelectDefaultOption = true
                        )
                        if (voucherRequest.type == VoucherType.PERCENTAGE.name) {
                            FoodAppTextField(
                                labelText = "Giảm tối đa",
                                value = voucherRequest.maxValue.toPlainString(),
                                onValueChange = {
                                    viewModel.onMaxValueChange(it)
                                },
                                singleLine = true
                            )
                        }
                        FoodAppTextField(
                            labelText = "Giá trị",
                            value = voucherRequest.value.toString(),
                            onValueChange = {
                                viewModel.onValueChange(it)
                            },
                            singleLine = true
                        )
                        FoodAppTextField(
                            labelText = "Đơn tối thiểu",
                            value = voucherRequest.minOrderPrice.toPlainString(),
                            onValueChange = {
                                viewModel.onMinOrderPriceChange(it)
                            },
                            singleLine = true
                        )
                        FoodAppTextField(
                            labelText = "Số lượng",
                            value = voucherRequest.total.toString(),
                            onValueChange = {
                                viewModel.onTotalChange(it)
                            },
                            singleLine = true
                        )
                        DateRangePickerSample(
                            startDate = voucherRequest.startDate,
                            endDate = voucherRequest.endDate,
                            modifier = Modifier.fillMaxWidth(),
                            isColumn = true,
                            onDateRangeSelected = { startDate, endDate ->
                                viewModel.onStartDateChange(startDate)
                                viewModel.onEndDateChange(endDate)
                            }
                        )
                        val selectedOption = if (voucherRequest.published) "Công khai" else "Ẩn"
                        Log.d("selectedOption", selectedOption.toString())
                        RadioGroupWrap(
                            text = "Phạm vi sử dụng",
                            options = listOf("Công khai", "Ẩn"),
                            selectedOption = selectedOption,
                            onOptionSelected = {
                                val isPublished = it == "Công khai"
                                viewModel.onPublishedChange(isPublished)
                            },
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                        ) {
                            Button(
                                onClick = {
                                    showVoucherDialog = false
                                    isUpdating = false
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

                                    if (isUpdating) {
                                        viewModel.updateVoucher(selectedVoucher!!)

                                    } else viewModel.addVoucher()

                                    showVoucherDialog = false
                                    isUpdating = false


                                },
                                modifier = Modifier.heightIn(48.dp),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = if (isUpdating) "Cập nhật" else "Tạo",
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
                    selectedVoucher = null
                },
                onConfirm = {

                   viewModel.deleteVoucher(selectedVoucher!!)
                    showDeleteDialog = false
                    selectedVoucher = null


                },
                confirmText = "Xóa",
                dismissText = "Đóng",
                showConfirmButton = true
            )
        }

    }
}