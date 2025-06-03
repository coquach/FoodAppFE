package com.se114.foodapp.ui.screen.menu



import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Food

import com.example.foodapp.navigation.Category
import com.example.foodapp.navigation.FoodDetailsAdmin
import com.example.foodapp.ui.screen.common.FoodList


import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.theme.confirm
import me.saket.swipe.SwipeAction


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MenuScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodListAdminViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var search by remember { mutableStateOf("") }

    val foods = viewModel.getFoodsByIdAndStatus(uiState.tabIndex, uiState.menuIdSelected)
        .collectAsLazyPagingItems()

    var showStatusDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
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
                    navController.navigate(FoodDetailsAdmin(food = Food.sample(),isUpdating = false))
                }

                FoodListAdmin.Event.Refresh -> {
                    viewModel.onAction(FoodListAdmin.Action.OnRefresh)
                    foods.refresh()
                }
            }
        }
    }

    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        if (handle?.get<Boolean>("shouldRefresh") == true) {
            handle["shouldRefresh"] = false
            viewModel.onAction(FoodListAdmin.Action.OnRefresh)
            foods.refresh()
        }
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
                Spacer(modifier = Modifier.size(8.dp))
            }




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
                                    icon = rememberVectorPainter(Icons.Default.Visibility),
                                    background = MaterialTheme.colorScheme.confirm,
                                    onSwipe = {
                                        viewModel.onAction(FoodListAdmin.Action.OnFoodSelected(it))
                                        showStatusDialog = true
                                    }
                                )
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
                            }
                        )
                    }),
                onTabSelected = { index ->
                    viewModel.onAction(FoodListAdmin.Action.OnTabSelected(index))
                }
            )


        }
    }
    if (showStatusDialog) {
        val isActive = uiState.selectedFood!!.active
        FoodAppDialog(
            title = if (isActive) "Ẩn món ăn" else "Hiển thị món ăn",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn xóa ${if (isActive) "ẩn" else "hiển thị"} món đã chọn không?",
            onDismiss = {

                showStatusDialog = false

            },
            onConfirm = {
                viewModel.onAction(FoodListAdmin.Action.OnToggleStatusFood)
                showStatusDialog = false


            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )


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

