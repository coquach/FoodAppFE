package com.se114.foodapp.ui.screen.warehouse.imports

import android.widget.Toast
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.R
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.ui.navigation.AddMenuItem
import com.example.foodapp.ui.navigation.Category
import com.example.foodapp.ui.navigation.ImportDetails
import com.example.foodapp.ui.navigation.OrderDetails
import com.example.foodapp.ui.navigation.UpdateEmployee
import com.example.foodapp.ui.navigation.UpdateMenuItem
import com.example.foodapp.ui.screen.common.MenuItemList
import com.example.foodapp.ui.screen.common.OrderListSection
import com.example.foodapp.ui.screen.components.DeleteBar
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.ui.screen.employee.EmployeeItemView
import kotlinx.coroutines.flow.collectLatest
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun ImportScreen(
    navController: NavController,
    viewModel: ImportViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val importList = viewModel.importList.collectAsLazyPagingItems()
    var search by remember { mutableStateOf("") }

    var selectedImportId by rememberSaveable { mutableStateOf<Long?>(null) }
    val showDialogDelete = rememberSaveable { mutableStateOf(false) }
    var loading by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when(it) {
                ImportViewModel.ImportEvents.ShowDeleteDialog -> {
                    showDialogDelete.value = true
                }

                is ImportViewModel.ImportEvents.ShowSuccessToast -> {

                }
            }
        }
    }
    when(uiState) {
        is ImportViewModel.ImportState.Loading ->{
            loading = true
        }
        is ImportViewModel.ImportState.Error -> {
            loading = false
            val message = (uiState as ImportViewModel.ImportState.Error).message
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
        else -> {
            loading = false
        }
    }

    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {

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
                .padding(horizontal = 16.dp)

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderDefaultView(
                    text = "Danh sách món ăn",
                    icon = Icons.Default.Category,
                    iconClick = {
                        navController.navigate(Category)
                    },
                    tintIcon = MaterialTheme.colorScheme.primary
                )
                SearchField(
                    searchInput = search,
                    searchChange = { search = it }
                )

            }
            if (importList.itemSnapshotList.items.isEmpty() && importList.loadState.refresh !is LoadState.Loading) {

                Nothing(
                    text = "Không có nhân viên nào",
                    icon = Icons.Default.Groups,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()

                ) {
                    gridItems(
                        importList, 1, key = { import -> import.id },
                        itemContent = { import ->
                            import?.let { it ->
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
                                                selectedImportId = it.id
                                                viewModel.onRemoveSwipe()
                                            }
                                        ))
                                ) {
                                    ImportCart(
                                        import = it,
                                        onClick = {

                                        }
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
    }
    if (showDialogDelete.value) {

        FoodAppDialog(
            title = "Xóa đơn nhập",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn xóa đơn đã chọn khỏi danh sách không?",
            onDismiss = {

                showDialogDelete.value = false
            },
            onConfirm = {
                viewModel.removeImport(selectedImportId!!)
                showDialogDelete.value = false
                selectedImportId = null

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
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


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Staff Icon",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = import.staffName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
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
                        text = StringUtils.formatDateTime(import.importDate),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
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
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.inversePrimary
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "Tổng giá: ${StringUtils.formatCurrency(import.totalPrice)}",
                color = MaterialTheme.colorScheme.inversePrimary,
                fontWeight = FontWeight.Bold
            )

        }


    }
}

@Composable
fun ImportCart(import: Import, onClick: () -> Unit) {
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
