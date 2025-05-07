package com.se114.foodapp.ui.screen.setting.supplier

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems

import com.example.foodapp.data.model.Supplier

import com.example.foodapp.ui.screen.components.BasicDialog
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.confirm
import com.se114.foodapp.ui.screen.employee.EmployeeItemView
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierScreen(
    navController: NavController,
    viewModel: SupplierViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val supplier = viewModel.suppliers.collectAsLazyPagingItems()
    val supplierRequest by viewModel.supplierRequest.collectAsStateWithLifecycle()

    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showSupplierDialog by rememberSaveable { mutableStateOf(false) }
    var isUpdating by rememberSaveable { mutableStateOf(false) }
    var supplierSelected by rememberSaveable { mutableStateOf<Long?>(null) }
    var showSetActiveDialog by rememberSaveable { mutableStateOf(false) }
    var isHide by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }



    when (uiState) {
        is SupplierViewModel.SupplierState.Loading -> {
            isLoading = true
        }

        is SupplierViewModel.SupplierState.Error -> {
            isLoading = false
            showErrorSheet = true
        }

        else -> {
            isLoading = false
        }
    }


    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    viewModel.loadSupplier()
                    showSupplierDialog = true
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
                onBack = {
                    navController.navigateUp()
                },
                text = "Nhà cung cấp"
            )
            TabWithPager(
                tabs = listOf("Đang hiển thị", "Đã ẩn"),
                pages = listOf(
                    {
                        SupplierListSection(
                            suppliers = supplier,
                            onClick = {
                                isUpdating = true
                                supplierSelected = it.id
                                viewModel.loadSupplier(it)
                                showSupplierDialog = true
                            },
                            endAction = { it ->
                                SwipeAction(
                                    icon = rememberVectorPainter(Icons.Default.Visibility),
                                    background = MaterialTheme.colorScheme.error,
                                    onSwipe = {
                                        isHide = true
                                        supplierSelected = it
                                        showSetActiveDialog = true
                                    }
                                )
                            }
                        )
                    },
                    {
                        SupplierListSection(
                            suppliers = supplier,
                            onClick = {
                                isUpdating = true
                                supplierSelected = it.id
                                viewModel.loadSupplier(it)
                                showSupplierDialog = true
                            },
                            endAction = { it ->
                                SwipeAction(
                                    icon = rememberVectorPainter(Icons.Default.Visibility),
                                    background = MaterialTheme.colorScheme.confirm,
                                    onSwipe = {
                                        supplierSelected = it
                                        showSetActiveDialog = true
                                    }
                                )
                            }
                        )
                    }
                ),

                onTabSelected = {
                    viewModel.setTab(it)
                }
            )
        }
    }
    if (showErrorSheet) {
        ModalBottomSheet(onDismissRequest = { showErrorSheet = false }, sheetState = sheetState) {
            BasicDialog(
                title = viewModel.error,
                description = viewModel.errorDescription,
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        showErrorSheet = false
                    }
                }
            )
        }
    }
    if (showSupplierDialog) {
        Dialog(
            onDismissRequest = {
                showSupplierDialog = false
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {


                    Text(
                        text = "Thông tin",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    FoodAppTextField(
                        labelText = "Tên",
                        value = supplierRequest.name,
                        onValueChange = {
                            viewModel.onNameChange(it)
                        },
                        singleLine = true
                    )
                    FoodAppTextField(
                        labelText = "Số địện thoại",
                        value = supplierRequest.phone,
                        onValueChange = {
                            viewModel.onPhoneChange(it)
                        },
                        singleLine = true
                    )
                    FoodAppTextField(
                        labelText = "Email",
                        value = supplierRequest.email,
                        onValueChange = {
                            viewModel.onEmailChange(it)
                        },
                        singleLine = true
                    )
                    FoodAppTextField(
                        labelText = "Địa chỉ",
                        value = supplierRequest.address,
                        onValueChange = {
                            viewModel.onAddressChange(it)
                        },
                        singleLine = true
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        Button(
                            onClick = {
                                showSupplierDialog = false
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
                                    viewModel.updateSupplier(supplierSelected!!)

                                } else viewModel.addSupplier()

                                showSupplierDialog = false
                                isUpdating = false


                            },
                            modifier = Modifier.heightIn(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
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
    if (showSetActiveDialog) {
        FoodAppDialog(
            title = if (isHide) "Ẩn nhà cung cấp" else "Hiện nhà cung cấp",
            message = if (isHide) "Bạn có chắc chắn muốn ẩn nhà cung cấp này không?" else "Bạn có chắc chắn muốn hiện nhà cung cấp này không?",
            onDismiss = {

                showSetActiveDialog = false
                supplierSelected = null
                isHide = false
            },
            onConfirm = {

                if (isHide) {
                    viewModel.setActiveSupplier(supplierSelected!!, false)
                } else viewModel.setActiveSupplier(supplierSelected!!, true)
                showSetActiveDialog = false
                supplierSelected = null
                isHide = false

            },
            confirmText = if (isHide) "Ẩn" else "Hiện",
            dismissText = "Đóng",
            showConfirmButton = true
        )
    }
}

@Composable
fun SupplierListSection(
    suppliers: LazyPagingItems<Supplier>,
    onClick: (Supplier) -> Unit,
    endAction: @Composable (Long) -> SwipeAction
) {
    if (suppliers.itemSnapshotList.items.isEmpty() && suppliers.loadState.refresh !is LoadState.Loading) {

        Nothing(
            text = "Không có nhà cung cấp nào",
            icon = Icons.Default.Groups
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .heightIn(max = 10000.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            gridItems(
                suppliers, 1, key = { supplier -> supplier.id },
                itemContent = { supplier ->
                    supplier?.let { it ->
                        SwipeableActionsBox(
                            modifier = Modifier
                                .padding(
                                    8.dp,
                                )
                                .clip(RoundedCornerShape(12.dp)),
                            endActions =  listOf(endAction(it.id))
                        ) {
                            SupplierCard(
                                supplier = it,
                                onClick = { onClick(supplier) }
                            )
                        }

                    }

                },
                placeholderContent = {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .background(Color.Gray.copy(alpha = 0.3f))
                    )
                }
            )

        }
    }
}

@Composable
fun SupplierCard(supplier: Supplier, onClick: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            SupplierDetails(supplier = supplier)
            Button(onClick = { onClick.invoke(supplier.id) }) {
                Text(
                    text = "Chi tiết",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }

}

@Composable
fun SupplierDetails(supplier: Supplier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)
    ) {
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SupplierDetailsRow(
                icon = Icons.Default.LocationCity,
                text = supplier.name
            )
            SupplierDetailsRow(
                icon = Icons.Default.Phone,
                text = supplier.phone
            )
            SupplierDetailsRow(
                icon = Icons.Default.Email,
                text = supplier.email
            )
            SupplierDetailsRow(
                icon = Icons.Default.LocationOn,
                text = supplier.address
            )
        }
    }
}

@Composable
fun SupplierDetailsRow(
    text: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.outline
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

