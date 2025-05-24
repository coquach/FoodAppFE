package com.se114.foodapp.ui.screen.menu


import android.util.Log
import android.widget.Toast

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems

import com.example.foodapp.navigation.AddFood
import com.example.foodapp.navigation.Category
import com.example.foodapp.navigation.UpdateFood
import com.example.foodapp.ui.screen.common.FoodList
import com.example.foodapp.ui.screen.components.DeleteBar

import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MenuScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    var search by remember { mutableStateOf("") }

    var isInSelectionMode by rememberSaveable { mutableStateOf(false) }
    var isSelectAll by rememberSaveable { mutableStateOf(false) }


    val currentTab by viewModel.tabIndex.collectAsStateWithLifecycle()

    val Foods = viewModel.getFoodsByTab(currentTab).collectAsLazyPagingItems()


    val showDialogDelete = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {

                MenuViewModel.MenuEvents.ShowDeleteDialog -> {
                    showDialogDelete.value = true
                }

                is MenuViewModel.MenuEvents.ShowErrorMessage -> {

                }
            }
        }
    }

    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        if (handle?.get<Boolean>("shouldRefresh") == true) {
            handle["shouldRefresh"] = false
            viewModel.refreshAllTabs()
        }
    }


    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    navController.navigate(AddFood)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
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
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = isInSelectionMode,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    DeleteBar(
                        onSelectAll = {
                            isSelectAll = !isSelectAll
                            viewModel.selectAllItems(emptyList(), isSelectAll)
                        },
                        onDeleteSelected = { viewModel.onRemoveClicked() }
                    )
                }
            }

            TabWithPager(
                tabs = listOf("Đang hiển thị", "Đã ẩn"),
                pages = listOf(
                    {
                        FoodList(
                            Foods = Foods,
                            isInSelectionMode = isInSelectionMode,
                            isSelected = { Food -> viewModel.selectedItems.contains(Food) },
                            onCheckedChange = { Food -> viewModel.toggleSelection(Food) },
                            animatedVisibilityScope = animatedVisibilityScope,
                            onItemClick = {
                                navController.navigate(UpdateFood(it))
                            },
                            onLongClick = {
                                isInSelectionMode = !isInSelectionMode
                            },
                        )


                    },
                    {
                        FoodList(
                            Foods = Foods,
                            isInSelectionMode = isInSelectionMode,
                            isSelected = { Food -> viewModel.selectedItems.contains(Food) },
                            onCheckedChange = { Food -> viewModel.toggleSelection(Food) },
                            animatedVisibilityScope = animatedVisibilityScope,
                            onItemClick = {},
                            onLongClick = {
                                isInSelectionMode = !isInSelectionMode
                            },
                        )
                    }),
                onTabSelected = { index ->
                    viewModel.setTab(index)
                }
            )


        }
    }
    if (showDialogDelete.value) {

        FoodAppDialog(
            title = "Xóa món ăn",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn xóa món đã chọn khỏi danh sách không?",
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

@Composable
fun <T : Any> ObserveLoadState(
    lazyPagingItems: LazyPagingItems<T>,
    statusName: String,

    ) {
    val context = LocalContext.current
    // Lắng nghe và xử lý trạng thái loadState
    LaunchedEffect(lazyPagingItems.loadState.refresh) {
        when (val state = lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                Log.d("FoodScreen", "Loading $statusName Foods")
            }

            is LoadState.Error -> {
                Log.d(
                    "FoodScreen",
                    "Error loading $statusName Foods: ${state.error.message}"
                )
                Toast.makeText(
                    context,
                    "Không thể tải dữ liệu: ${state.error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                Log.d("FoodScreen", "$statusName Foods loaded")
                Log.d("FoodScreen", "${lazyPagingItems.itemCount} size")

            }
        }
    }
}

