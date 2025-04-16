package com.se114.foodapp.ui.screen.employee

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.se114.foodapp.data.model.Staff
import com.example.foodapp.ui.navigation.AddEmployee

import com.example.foodapp.ui.navigation.UpdateEmployee
import com.example.foodapp.ui.screen.components.CustomCheckbox
import com.example.foodapp.ui.screen.components.DeleteBar
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.gridItems

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmployeeScreen(
    navController: NavController,
    viewModel: EmployeeViewModel = hiltViewModel()
) {
    var search by remember { mutableStateOf("") }

    var isInSelectionMode by rememberSaveable { mutableStateOf(false) }
    var isSelectAll by rememberSaveable { mutableStateOf(false) }

    val showDialogDelete = remember { mutableStateOf(false) }

    val staffs = viewModel.getAllStaffs.collectAsLazyPagingItems()

    when (val state = staffs.loadState.refresh) {
        is LoadState.Loading -> {
            Loading()
        }

        is LoadState.Error -> {
            Toast.makeText(
                LocalContext.current,
                "Không thể tải thêm dữ liệu: ${state.error.message}",
                Toast.LENGTH_LONG
            ).show()
            state.error.message?.let { Log.d("Paging", it) }
        }

        is LoadState.NotLoading -> {

        }
    }

    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    navController.navigate(AddEmployee)
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !isInSelectionMode,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HeaderDefaultView(
                            text = "Danh sách nhân viên"
                        )
                        SearchField(
                            searchInput = search,
                            searchChange = { search = it }
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = isInSelectionMode,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    DeleteBar(
                        onSelectAll = {
                            isSelectAll = !isSelectAll
                            viewModel.selectAllItems(
                                staffList = staffs.itemSnapshotList.items,
                                isSelectAll = isSelectAll
                            )
                        },
                        onDeleteSelected = { viewModel.onRemoveClicked() }
                    )
                }
            }
            if (staffs.itemSnapshotList.items.isEmpty() && staffs.loadState.refresh !is LoadState.Loading) {

                com.example.foodapp.ui.screen.components.Nothing(
                    text = "Không có nhân viên nào",
                    icon = Icons.Default.Groups
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()

                ) {
                    gridItems(staffs, 2, key = { staff -> staff.id.toString() }) { staff ->
                        staff?.let {
                            EmployeeItemView(
                                staff = it,
                                isInSelectionMode = isInSelectionMode,
                                isSelected = viewModel.selectedItems.contains(it),
                                onCheckedChange = { staff ->
                                    viewModel.toggleSelection(staff)
                                },
                                onClick = {
                                    navController.navigate(UpdateEmployee(it))
                                },
                                onLongClick = {
                                    isInSelectionMode = !isInSelectionMode
                                },
                            )
                        }

                    }
                }
            }

        }
    }

    if (showDialogDelete.value) {

        FoodAppDialog(
            title = "Xóa nhân viên",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn xóa nhân viên đã chọn ra khỏi danh sách không?",
            onDismiss = {

                showDialogDelete.value = false
            },
            onConfirm = {
                viewModel.removeItem()
                showDialogDelete.value = false
                isInSelectionMode = false

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeeItemView(
    staff: Staff,
    onClick: (Staff) -> Unit,
    onLongClick: ((Boolean) -> Unit)? = null,
    isInSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onCheckedChange: ((Staff) -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AnimatedVisibility(visible = isInSelectionMode) {
            CustomCheckbox(
                checked = isSelected,
                onCheckedChange = { onCheckedChange?.invoke(staff) },
            )
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(162.dp)
                .height(216.dp)
                .graphicsLayer {
                    alpha = if (isSelected && isInSelectionMode) 0.7f else 1f
                }
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                    spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                )
                .background(color = MaterialTheme.colorScheme.surface)
                .combinedClickable(
                    onClick = { onClick.invoke(staff) },
                    onLongClick = {
                        onLongClick?.invoke(true)
                    }
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(147.dp)
            ) {
                AsyncImage(
                    model = staff.imageUrl, contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                )

            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = staff.fullName ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = staff.position ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}