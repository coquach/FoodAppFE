package com.se114.foodapp.ui.screen.supplier

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.Retry
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierScreen(
    navController: NavController,
    viewModel: SupplierViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSupplierDialog by rememberSaveable { mutableStateOf(false) }
    var showSetActiveDialog by rememberSaveable { mutableStateOf(false) }

    var showErrorSheet by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                SupplierState.Event.OnBack -> {
                    navController.popBackStack()
                }

                SupplierState.Event.ShowError -> {
                    showErrorSheet = true
                }

                is SupplierState.Event.ShowToastSuccess -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getSuppliers()
    }


    Scaffold(
        floatingActionButton =
            {
                MyFloatingActionButton(
                    onClick = {
                        viewModel.onAction(SupplierState.Action.OnSupplierSelected(Supplier()))
                        viewModel.onAction(SupplierState.Action.OnUpdateStatus(false))
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
                    viewModel.onAction(SupplierState.Action.OnBack)
                },
                text = "Nhà cung cấp"
            )
            SearchField(
                searchInput = uiState.nameSearch,
                searchChange = {
                    viewModel.onAction(SupplierState.Action.OnNameSearchChange(it))
                },
                searchFilter = {
                    viewModel.onAction(SupplierState.Action.OnSearchFilterChange)
                },
                switchState = false,
                switchChange = {
                },
                filterChange = {},
                filters = emptyList(),
                placeHolder = "Tìm kiếm theo tên nhà cung cấp",
                isFilterBox = false,
                filterSelected = ""
            )

            TabWithPager(
                tabs = listOf("Đang hiển thị", "Đã ẩn"),
                pages = listOf(
                    {
                        when (uiState.getSupplierState) {
                            is SupplierState.GetSupplierState.Error -> {
                                val error =
                                    (uiState.getSupplierState as SupplierState.GetSupplierState.Error).message
                                Retry(
                                    message = error,
                                    onClicked = {
                                        viewModel.getSuppliers()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            SupplierState.GetSupplierState.Loading -> {
                                Loading(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            SupplierState.GetSupplierState.Success -> {
                                SupplierListSection(
                                    modifier = Modifier.fillMaxSize(),
                                    suppliers = uiState.suppliers,
                                    onClick = {
                                        viewModel.onAction(
                                            SupplierState.Action.OnSupplierSelected(
                                                it
                                            )
                                        )
                                        viewModel.onAction(SupplierState.Action.OnUpdateStatus(true))
                                        showSupplierDialog = true
                                    },
                                    endAction = { it ->
                                        SwipeAction(
                                            icon = rememberVectorPainter(Icons.Default.VisibilityOff),
                                            background = MaterialTheme.colorScheme.error,
                                            onSwipe = {
                                                viewModel.onAction(
                                                    SupplierState.Action.OnSupplierSelected(
                                                        it
                                                    )
                                                )
                                                viewModel.onAction(
                                                    SupplierState.Action.OnUpdateHide(
                                                        true
                                                    )
                                                )
                                                showSetActiveDialog = true
                                            }
                                        )
                                    }
                                )
                            }
                        }

                    },
                    {
                        when (uiState.getSupplierState) {
                            is SupplierState.GetSupplierState.Error -> {
                                val error =
                                    (uiState.getSupplierState as SupplierState.GetSupplierState.Error).message
                                Retry(
                                    message = error,
                                    onClicked = {
                                        viewModel.getSuppliers()
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            SupplierState.GetSupplierState.Loading -> {
                                Loading(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            SupplierState.GetSupplierState.Success -> {
                                SupplierListSection(
                                    modifier = Modifier.fillMaxSize(),
                                    suppliers = uiState.suppliers,
                                    onClick = {
                                        viewModel.onAction(
                                            SupplierState.Action.OnSupplierSelected(
                                                it
                                            )
                                        )
                                        viewModel.onAction(SupplierState.Action.OnUpdateStatus(true))
                                        showSupplierDialog = true
                                    },
                                    endAction = { it ->
                                        SwipeAction(
                                            icon = rememberVectorPainter(Icons.Default.Visibility),
                                            background = MaterialTheme.colorScheme.error,
                                            onSwipe = {
                                                viewModel.onAction(
                                                    SupplierState.Action.OnSupplierSelected(
                                                        it
                                                    )
                                                )
                                                viewModel.onAction(
                                                    SupplierState.Action.OnUpdateHide(
                                                        false
                                                    )
                                                )
                                                showSetActiveDialog = true
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onTabSelected = {
                    when (it) {
                        0 -> viewModel.onAction(SupplierState.Action.OnStatusFilterChange(true))
                        1 -> viewModel.onAction(SupplierState.Action.OnStatusFilterChange(false))
                    }
                }
            )
        }
    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error ?: "Lỗi không xác định",
            onDismiss = { showErrorSheet = false }
        )
    }
    if (showSupplierDialog) {
        Dialog(
            onDismissRequest = {
                showSupplierDialog = false
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.background,
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
                        value = uiState.supplierSelected.name,
                        onValueChange = {
                            viewModel.onAction(SupplierState.Action.OnNameChange(it))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    FoodAppTextField(
                        labelText = "Số địện thoại",
                        value = uiState.supplierSelected.phone,
                        onValueChange = {
                            viewModel.onAction(SupplierState.Action.OnPhoneChange(it))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    FoodAppTextField(
                        labelText = "Email",
                        value = uiState.supplierSelected.email,
                        onValueChange = {
                            viewModel.onAction(SupplierState.Action.OnEmailChange(it))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    FoodAppTextField(
                        labelText = "Địa chỉ",
                        value = uiState.supplierSelected.address,
                        onValueChange = {
                            viewModel.onAction(SupplierState.Action.OnAddressChange(it))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
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


                        LoadingButton(
                            onClick = {

                                if (uiState.isUpdating) {
                                    viewModel.onAction(SupplierState.Action.UpdateSupplier)

                                } else viewModel.onAction(SupplierState.Action.AddSupplier)

                                showSupplierDialog = false

                            },
                            loading = uiState.isLoading,
                            text = if (uiState.isUpdating) "Cập nhật" else "Tạo"
                        )
                    }
                }
            }
        }
    }
    if (showSetActiveDialog) {
        FoodAppDialog(
            title = if (uiState.isHide) "Ẩn nhà cung cấp" else "Hiện nhà cung cấp",
            message = if (uiState.isHide) "Bạn có chắc chắn muốn ẩn nhà cung cấp này không?" else "Bạn có chắc chắn muốn hiện nhà cung cấp này không?",
            onDismiss = {
                showSetActiveDialog = false
            },
            onConfirm = {

                if (uiState.isHide) {
                    viewModel.onAction(SupplierState.Action.SetActiveSupplier(false))
                } else viewModel.onAction(SupplierState.Action.SetActiveSupplier(true))
                showSetActiveDialog = false

            },
            confirmText = if (uiState.isHide) "Ẩn" else "Hiện",
            dismissText = "Đóng",
            showConfirmButton = true
        )
    }
}

@Composable
fun SupplierListSection(
    modifier: Modifier = Modifier,
    suppliers: List<Supplier>,
    onClick: (Supplier) -> Unit,
    endAction: @Composable (Supplier) -> SwipeAction,

    ) {
    if (suppliers.isEmpty()) {

        Nothing(
            text = "Không có nhà cung cấp nào",
            icon = Icons.Default.Groups,
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            items(items = suppliers, key = { it.id!! }) { supplier ->
                SwipeableActionsBox(
                    modifier = Modifier
                        .padding(
                            8.dp,
                        )
                        .clip(RoundedCornerShape(12.dp)),
                    endActions = listOf(endAction(supplier))
                ) {
                    SupplierCard(
                        supplier = supplier,
                        onClick = { onClick(supplier) }
                    )
                }
            }

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
            Button(onClick = { onClick.invoke(supplier.id!!) }) {
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
    color: Color = MaterialTheme.colorScheme.outline,
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

