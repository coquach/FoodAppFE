package com.se114.foodapp.ui.screen.warehouse.imports

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Import
import com.example.foodapp.navigation.ImportDetails
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.flow.collectLatest
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    navController: NavController,
    viewModel: ImportViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imports = remember(uiState.filter) {
        viewModel.getImports(uiState.filter)
    }.collectAsLazyPagingItems()


    var showDialogDelete by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }

    var isDeletable by rememberSaveable { mutableStateOf(false) }


    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collectLatest {
            when (it) {
                is ImportState.Event.GoToImportDetails -> {
                    navController.navigate(ImportDetails(it.import, true))
                }

                is ImportState.Event.ShowSuccessToast -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                ImportState.Event.ShowError -> {
                    showErrorSheet = true
                }

                is ImportState.Event.ShowSuccess -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                ImportState.Event.AddImport -> {
                    navController.navigate(
                        ImportDetails(
                            import = Import(importDate = LocalDate.now()),
                            isUpdating = false
                        )
                    )
                }

                ImportState.Event.OnBack -> {
                    navController.popBackStack()
                }

                ImportState.Event.NotifyCantDelete -> {
                    Toast.makeText(
                        context,
                        "Không thể xóa phiếu nhập vì đã quá hạn",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    Scaffold(
        floatingActionButton =
            {
                MyFloatingActionButton(
                    onClick = {
                        viewModel.onAction(ImportState.Action.AddImport)
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
                    viewModel.onAction(ImportState.Action.OnBack)
                },
                text = "Đơn nhập hàng",

                )
            SearchField(
                searchInput = uiState.nameSearch,
                searchChange = {
                    viewModel.onAction(ImportState.Action.OnNameSearch(it))
                },
                searchFilter = {
                    viewModel.onAction(ImportState.Action.OnSearchFilter)
                },
                switchState = uiState.filter.order == "desc",
                switchChange = {
                    when (it) {
                        true -> viewModel.onAction(ImportState.Action.OnOrderChange("desc"))
                        false -> viewModel.onAction(ImportState.Action.OnOrderChange("asc"))
                    }
                },
                filterChange = {
                    when (it) {
                        "Id" -> viewModel.onAction(ImportState.Action.OnSortByChange("id"))
                        "Tổng giá" -> viewModel.onAction(ImportState.Action.OnSortByChange("totalPrice"))
                    }
                },
                filters = listOf("Id", "Tổng giá"),
                filterSelected = when (uiState.filter.sortBy) {
                    "id" -> "Id"
                    "totalPrice" -> "Tổng giá"
                    else -> "Id"
                },
                placeHolder = "Tìm kiếm theo tên NCC..."
            )

            DateRangePickerSample(
                modifier = Modifier.width(170.dp),
                startDateText = "Bắt đầu",
                endDateText = "Kết thúc",
                startDate = uiState.filter.startDate,
                endDate = uiState.filter.endDate,
                onDateRangeSelected = { startDate, endDate ->
                    viewModel.onAction(ImportState.Action.OnDateChange(startDate, endDate))
                }
            )

            LazyPagingSample(
                onRefresh = {
                    viewModel.onAction(ImportState.Action.OnRefresh)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                items = imports,
                textNothing = "Không có đơn nào",
                iconNothing = Icons.Default.Description,
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
                            icon = rememberVectorPainter(Icons.Default.Delete),
                            background = MaterialTheme.colorScheme.error,
                            onSwipe = {
                                isDeletable =
                                    it.importDate == LocalDate.now()

                                if (isDeletable) {
                                    viewModel.onAction(ImportState.Action.OnImportSelected(it.id!!))
                                    showDialogDelete = true
                                } else {
                                    viewModel.onAction(ImportState.Action.NotifyCantDelete)
                                }

                            }
                        ))
                ) {
                    ImportCard(
                        import = it,
                        onClick = {
                            viewModel.onAction(ImportState.Action.OnImportClick(it))
                        }
                    )
                }
            }


        }
    }
    if (showDialogDelete) {

        FoodAppDialog(
            title = "Xóa phiếu nhập",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn xóa đơn đã chọn khỏi danh sách không?",
            onDismiss = {

                showDialogDelete = false
            },
            onConfirm = {
                viewModel.onAction(ImportState.Action.OnDelete)
                showDialogDelete = false

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error ?: "Lỗi không xác định",
            onDismiss = { showErrorSheet = false },
        )
    }
}

@Composable
fun ImportDetails(import: Import) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,

            ) {

            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {

                Text(
                    text = "Mã đơn nhập: ${import.id}",
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Supplier Icon",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = import.supplierName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Clock Icon",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = StringUtils.formatLocalDate(import.importDate)!!,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }


        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Tổng giá: ${StringUtils.formatCurrency(import.totalPrice)}",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

        }


    }
}

@Composable
fun ImportCard(import: Import, onClick: () -> Unit) {
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
            ImportDetails(import = import)
            Button(onClick = onClick) {
                Text(
                    text = "Chi tiết",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

        }
    }

}
