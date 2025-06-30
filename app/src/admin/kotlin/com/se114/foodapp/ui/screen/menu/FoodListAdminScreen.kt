package com.se114.foodapp.ui.screen.menu


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Order

import com.example.foodapp.navigation.Category
import com.example.foodapp.navigation.FoodDetailsAdmin
import com.example.foodapp.ui.screen.common.FoodList

import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet


import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.theme.confirm
import kotlinx.coroutines.flow.MutableStateFlow
import me.saket.swipe.SwipeAction


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodListAdminScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodListAdminViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    val foods = remember(uiState.foodFilter) {
        viewModel.getFoods(uiState.foodFilter)
    }.collectAsLazyPagingItems()


    var showStatusDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {

                is FoodListAdmin.Event.GoToUpdateFood -> {
                    navController.navigate(FoodDetailsAdmin(it.food, true))
                }

                FoodListAdmin.Event.ShowError -> {
                    showErrorSheet = true
                }

                FoodListAdmin.Event.GoToAddFood -> {
                    navController.navigate(
                        FoodDetailsAdmin(
                            food = Food.sample(),
                            isUpdating = false
                        )
                    )
                }

                is FoodListAdmin.Event.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getMenus()
    }


    Scaffold(
        floatingActionButton =
            {
                MyFloatingActionButton(
                    onClick = {
                        viewModel.onAction(FoodListAdmin.Action.OnAddClicked)
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
                    text = "Danh sách món ăn",
                    icon = Icons.Default.Category,
                    iconClick = {
                        navController.navigate(Category)
                    },
                    tintIcon = MaterialTheme.colorScheme.primary
                )

                SearchField(
                    searchInput = uiState.nameSearch,
                    searchChange = {
                        viewModel.onAction(FoodListAdmin.Action.OnChangeNameSearch(it))
                    },
                    searchFilter = {
                        viewModel.onAction(FoodListAdmin.Action.OnSearchFilter)
                    },
                    switchState = uiState.foodFilter.order == "desc",
                    switchChange = {
                        when(it){
                            true -> viewModel.onAction(FoodListAdmin.Action.OnOrderChange("desc"))
                            false -> viewModel.onAction(FoodListAdmin.Action.OnOrderChange("asc"))

                        }
                    },
                    filterChange = {
                        when(it){
                            "Id" -> viewModel.onAction(FoodListAdmin.Action.OnSortByChange("id"))
                            "Giá" -> viewModel.onAction(FoodListAdmin.Action.OnSortByChange("price"))
                            "Số lượng" -> viewModel.onAction(FoodListAdmin.Action.OnSortByChange("remainingQuantity"))

                        }
                    },
                    filters = listOf("Id", "Giá", "Số lượng"),
                    filterSelected = when(uiState.foodFilter.sortBy){
                        "id" -> "Id"
                        "price" -> "Giá"
                        "remainingQuantity" -> "Số lượng"
                        else -> "Id"

                    },
                    placeHolder = "Tìm kiếm theo tên món ăn..."
                )




            ChipsGroupWrap(
                modifier = Modifier.fillMaxWidth(),
                options = uiState.menus.map { it.name },
                selectedOption = uiState.menuName,
                onOptionSelected = { selectedName ->
                    val selectedMenu = uiState.menus.find { it.name == selectedName }
                    selectedMenu?.let {
                        viewModel.onAction(FoodListAdmin.Action.OnMenuClicked(it.id!!, it.name))
                    }
                },
                containerColor = MaterialTheme.colorScheme.inversePrimary,
                isFlowLayout = false,
                shouldSelectDefaultOption = true
            )

            TabWithPager(
                tabs = listOf("Đang hiển thị", "Đã ẩn"),
                pages = listOf(
                    {
                        FoodList(
                            foods = foods,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onItemClick = {
                                viewModel.onAction(FoodListAdmin.Action.OnFoodClicked(it))
                            },
                            endAction = { it ->
                                SwipeAction(
                                    icon = rememberVectorPainter(Icons.Default.VisibilityOff),
                                    background = MaterialTheme.colorScheme.error,
                                    onSwipe = {
                                        viewModel.onAction(FoodListAdmin.Action.OnFoodSelected(it))
                                        showStatusDialog = true
                                    }
                                )
                            },
                            isSwipeAction = true,
                            onRefresh = {
                                viewModel.onAction(FoodListAdmin.Action.OnRefresh)
                            }

                        )


                    },
                    {
                        FoodList(
                            foods = foods,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onItemClick = {
                                viewModel.onAction(FoodListAdmin.Action.OnFoodClicked(it))
                            },
                            endAction = { it ->
                                SwipeAction(
                                    icon = rememberVectorPainter(Icons.Default.Visibility),
                                    background = MaterialTheme.colorScheme.confirm,
                                    onSwipe = {
                                        viewModel.onAction(FoodListAdmin.Action.OnFoodSelected(it))
                                        showStatusDialog = true
                                    }
                                )
                            },
                            isSwipeAction = true,
                            onRefresh = {
                                viewModel.onAction(FoodListAdmin.Action.OnRefresh)
                            }
                        )
                    }),
                onTabSelected = { index ->
                   when(index){
                       0 -> viewModel.onAction(FoodListAdmin.Action.OnChangeStatusFood(true))
                       1 -> viewModel.onAction(FoodListAdmin.Action.OnChangeStatusFood(false))

                   }
                }
            )


        }
    }
    if (showStatusDialog) {
        val isActive = uiState.selectedFood!!.active
        FoodAppDialog(
            title = if (isActive) "Ẩn món ăn" else "Hiển thị món ăn",
            titleColor = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.confirm,
            message = "Bạn có chắc chắn muốn xóa ${if (isActive) "ẩn" else "hiển thị"} món đã chọn không?",
            onDismiss = {

                showStatusDialog = false

            },
            onConfirm = {
                viewModel.onAction(FoodListAdmin.Action.OnToggleStatusFood)
                showStatusDialog = false


            },
            containerConfirmButtonColor = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.confirm,
            confirmText = if (isActive) "Ẩn" else "Hiện",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = {
                showErrorSheet = false
            })
    }

}

//@Composable
//fun <T : Any> ObserveLoadState(
//    lazyPagingItems: LazyPagingItems<T>,
//    statusName: String,
//
//    ) {
//    val context = LocalContext.current
//    // Lắng nghe và xử lý trạng thái loadState
//    LaunchedEffect(lazyPagingItems.loadState.refresh) {
//        when (val state = lazyPagingItems.loadState.refresh) {
//            is LoadState.Loading -> {
//                Log.d("FoodScreen", "Loading $statusName Foods")
//            }
//
//            is LoadState.Error -> {
//                Log.d(
//                    "FoodScreen",
//                    "Error loading $statusName Foods: ${state.error.message}"
//                )
//                Toast.makeText(
//                    context,
//                    "Không thể tải dữ liệu: ${state.error.message}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//
//            else -> {
//                Log.d("FoodScreen", "$statusName Foods loaded")
//                Log.d("FoodScreen", "${lazyPagingItems.itemCount} size")
//
//            }
//        }
//    }
//}

