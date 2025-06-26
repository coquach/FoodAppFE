package com.se114.foodapp.ui.screen.staff


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Staff
import com.example.foodapp.navigation.EmployeeDetails
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import kotlinx.coroutines.flow.MutableStateFlow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeScreen(
    navController: NavController,
    viewModel: EmployeeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialogDelete by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }

    val staffs = viewModel.staffs.collectAsLazyPagingItems()
    val context = LocalContext.current




    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {

                is EmployeeSate.Event.ShowError -> {
                    showErrorSheet = true
                }

                is EmployeeSate.Event.ShowSuccessToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }

                is EmployeeSate.Event.GoToDetail -> {
                    navController.navigate(EmployeeDetails(staff = it.staff, isUpdating = true))
                }

                EmployeeSate.Event.GoToAddStaff -> {
                    navController.navigate(EmployeeDetails(staff = Staff(), isUpdating = false))
                }
            }
        }
    }


    Scaffold(
        floatingActionButton =
            {
                MyFloatingActionButton(
                    onClick = {
                        viewModel.onAction(EmployeeSate.Action.OnAddStaff)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            HeaderDefaultView(
                text = "Danh sách nhân viên"
            )
            SearchField(
                searchInput = uiState.nameSearch,
                searchChange ={
                    viewModel.onAction(EmployeeSate.Action.OnNameSearchChange(it))
                },
                searchFilter = {
                    viewModel.onAction(EmployeeSate.Action.OnSearchFilter)
                },
                switchState = uiState.staffFilter.order == "desc",
                switchChange = {
                    when(it){
                        true -> viewModel.onAction(EmployeeSate.Action.OnOrderChange("desc"))
                        false -> viewModel.onAction(EmployeeSate.Action.OnOrderChange("asc"))
                    }
                },
                filterChange = {
                    when(it){
                        "Id" -> viewModel.onAction(EmployeeSate.Action.OnSortByChange("id"))
                        "Tên" -> viewModel.onAction(EmployeeSate.Action.OnSortByChange("fullName"))
                        "Lương" -> viewModel.onAction(EmployeeSate.Action.OnSortByChange("basicSalary"))
                    }
                },
                filters = listOf("Id", "Tên", "Lương"),
                filterSelected = when(uiState.staffFilter.sortBy){
                    "id" -> "Id"
                    "fullName" -> "Tên"
                    "basicSalary" -> "Lương"
                    else -> "Id"
                },
                placeHolder = "Tìm kiếm theo tên nhân viên..."
            )

            TabWithPager(
                tabs = listOf("Đang hiển thị", "Đã nghỉ"),
                pages = listOf(
                    {
                        LazyPagingSample(
                            modifier = Modifier.fillMaxSize(),
                            items = staffs,
                            textNothing = "Không có nhân viên nào",
                            iconNothing = Icons.Default.Groups,
                            columns = 2,
                            key = {
                                it.id!!
                            }
                        ) {
                            EmployeeItemView(
                                staff = it,
                                onClick = {
                                    viewModel.onAction(EmployeeSate.Action.OnStaffClicked(it))
                                },
                                endAction = {
                                    SwipeAction(
                                        icon = rememberVectorPainter(Icons.Default.Close),
                                        background = MaterialTheme.colorScheme.error,
                                        onSwipe = {
                                            viewModel.onAction(
                                                EmployeeSate.Action.OnStaffSelected(
                                                    it
                                                )
                                            )
                                            showDialogDelete = true
                                        }
                                    )
                                }
                            )
                        }
                    },
                    {
                        LazyPagingSample(
                            modifier = Modifier.fillMaxSize(),
                            items = staffs,
                            textNothing = "Không có nhân viên nào",
                            iconNothing = Icons.Default.Groups,
                            columns = 2,
                            key = {
                                it.id!!
                            }
                        ) {
                            EmployeeItemView(
                                staff = it,
                                onClick = {
                                    viewModel.onAction(EmployeeSate.Action.OnStaffClicked(it))
                                },
                            )
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth().weight(1f),
                onTabSelected = {
                   when(it){
                       0 -> viewModel.onAction(EmployeeSate.Action.OnChangeStatusFilter(true))
                       1 -> viewModel.onAction(EmployeeSate.Action.OnChangeStatusFilter(false))
                   }
                }
            )
        }
    }

    if (showDialogDelete) {

        FoodAppDialog(
            title = "Xóa nhân viên",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn xóa nhân viên đã chọn ra khỏi danh sách không?",
            onDismiss = {

                showDialogDelete = false
            },
            onConfirm = {
                viewModel.onAction(EmployeeSate.Action.OnDeleteStaff)
                showDialogDelete = false

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = {
                showErrorSheet = false
            },
        )
    }
}







@Composable
fun EmployeeItemView(
    staff: Staff,
    onClick: (Staff) -> Unit,
    endAction: (@Composable (Staff) -> SwipeAction)?= null,
) {
    SwipeableActionsBox(
        modifier = Modifier
            .padding(
                8.dp,
            )
            .clip(RoundedCornerShape(12.dp)),
        endActions = endAction?.let { listOf(it(staff)) } ?: emptyList()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .width(162.dp)
                    .height(216.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                        spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                    )
                    .background(color = MaterialTheme.colorScheme.surface)
                    .clickable {
                        onClick.invoke(staff)
                    }
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(147.dp)
                ) {
                    AsyncImage(
                        model = staff.avatar?.url, contentDescription = null,
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
                        text = staff.fullName,
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


}

//@Composable
//fun <T : Any> ObserveLoadState(
//    lazyPagingItems: LazyPagingItems<T>,
//    statusName: String,
//) {
//
//    // Lắng nghe và xử lý trạng thái loadState
//    LaunchedEffect(lazyPagingItems.loadState.refresh) {
//        when (val state = lazyPagingItems.loadState.refresh) {
//            is LoadState.Loading -> {
//                Log.d("Staffs", "Loading $statusName Staffs")
//            }
//
//            is LoadState.Error -> {
//                Log.d("Staffs", "Error loading $statusName Orders: ${state.error.message}")
//            }
//
//            else -> {
//                Log.d("Staffs", "$statusName Staffs loaded")
//            }
//        }
//    }
//}