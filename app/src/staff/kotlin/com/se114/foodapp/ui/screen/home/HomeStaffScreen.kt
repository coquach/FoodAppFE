package com.se114.foodapp.ui.screen.home

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.enums.FoodTableStatus
import com.example.foodapp.navigation.Cart
import com.example.foodapp.navigation.CheckoutStaff
import com.example.foodapp.navigation.Home
import com.example.foodapp.ui.screen.common.FoodList
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.FoodTableCard
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ItemCount
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.utils.StringUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.MutableStateFlow


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeStaffScreen(
    navController: NavController,
    viewModel: HomeStaffViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foodTables = remember(uiState.filter) {
        viewModel.getFoodTables(uiState.filter)
    }.collectAsLazyPagingItems()

    var isOpenDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }



    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { event ->
                when (event) {


                    HomeStaffState.Event.ShowError -> {
                        showErrorSheet = true
                    }

                    is HomeStaffState.Event.NavigateToCheckout -> {
                        navController.navigate(CheckoutStaff(event.id))
                    }
                }
            }
    }
    val notificationPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        ) else null

    if (notificationPermissionState != null) {
        LaunchedEffect(Unit) {

            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }

        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)


        ) {
            HeaderDefaultView(
                text = "Bàn tại quán"
            )

            SearchField(
                searchInput = uiState.nameSearch,
                searchChange = {
                    viewModel.onAction(HomeStaffState.Action.OnNameSearchChange(it))
                },
                searchFilter = {
                    viewModel.onAction(HomeStaffState.Action.OnSearchFilter)
                },
                switchState = uiState.filter.order == "asc",
                switchChange = {
                    when(it){
                        true -> viewModel.onAction(HomeStaffState.Action.OnOrderChange("asc"))
                        false -> viewModel.onAction(HomeStaffState.Action.OnOrderChange("desc"))
                    }
                },
                filterChange = {
                    when(it){
                        "Số chỗ ngồi" -> viewModel.onAction(HomeStaffState.Action.OnSortByChange("seatCapacity"))
                        "Tên bàn" -> viewModel.onAction(HomeStaffState.Action.OnSortByChange("tableName"))
                    }
                },
                filters = listOf(
                    "Số chỗ ngồi",
                    "Tên bàn"
                ),
                filterSelected = when(uiState.filter.sortBy){
                    "seatCapacity" -> "Số chỗ ngồi"
                    "tableName" -> "Tên bàn"
                    else -> "Số chỗ ngồi"
                },
                placeHolder = "Tìm kiếm bàn ăn theo tên",
            )
            TabWithPager(
                modifier = Modifier.fillMaxWidth().weight(1f),
                tabs = listOf("Đang trống", "Đang dùng"),
                pages = listOf(
                    {
                        LazyPagingSample(
                            onRefresh = {
                                viewModel.onAction(HomeStaffState.Action.OnRefresh)
                            },
                            modifier = Modifier.fillMaxSize(),
                            items = foodTables,
                            textNothing = "Không có bàn nào",
                            iconNothing = Icons.Default.TableRestaurant,
                            onRetry = {
                                viewModel.getFoodTables(uiState.filter)
                            },
                            columns = 2,
                            key = {
                                it.id!!
                            }
                        ) {
                            FoodTableCard(
                                foodTable = it
                            ) {
                                viewModel.onAction(HomeStaffState.Action.OnFoodTableSelected(it))
                                isOpenDialog = true

                            }
                        }
                    },
                    {
                        LazyPagingSample(
                            onRefresh = {
                                viewModel.onAction(HomeStaffState.Action.OnRefresh)
                            },
                            modifier = Modifier.fillMaxSize(),
                            items = foodTables,
                            textNothing = "Không có bàn nào",
                            iconNothing = Icons.Default.TableRestaurant,
                            onRetry = {
                                viewModel.getFoodTables(uiState.filter)
                            },
                            columns = 2,
                            key = {
                                it.id!!
                            }
                        ) {
                            FoodTableCard(
                                foodTable = it
                            ) {
                                viewModel.onAction(HomeStaffState.Action.OnNavigateToOrder(it.id!!))

                            }
                        }
                    }
                ),
                onTabSelected = {
                    when (it){
                        0 -> viewModel.onAction(HomeStaffState.Action.OnStatusChange(FoodTableStatus.EMPTY.name))
                        1 -> viewModel.onAction(HomeStaffState.Action.OnStatusChange(FoodTableStatus.OCCUPIED.name))
                    }
                }
            )







        }



    if (isOpenDialog) {
        FoodAppDialog(
            title = "Tạo hóa đơn",
            titleColor = MaterialTheme.colorScheme.primary,
            message = "Bạn muốn tạo hóa đơn cho bàn này",
            onDismiss = {
                isOpenDialog = false
            },
            containerConfirmButtonColor = MaterialTheme.colorScheme.primary,
            onConfirm = {
                viewModel.onAction(HomeStaffState.Action.CreateOrderForTable)
            },
            confirmText = "Xác nhận",
            dismissText = "Đóng",

        )
    }

    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = {
                showErrorSheet = false
            }
        )
    }

}


