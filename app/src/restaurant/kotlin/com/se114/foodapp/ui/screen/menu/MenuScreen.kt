package com.se114.foodapp.ui.screen.menu


import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.ui.navigation.AddMenuItem
import com.example.foodapp.ui.navigation.UpdateMenuItem
import com.example.foodapp.ui.screen.common.MenuItemList
import com.example.foodapp.ui.screen.components.DeleteBar
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal
import java.time.LocalTime

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MenuScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: MenuViewModel = hiltViewModel()
) {
    var search by remember { mutableStateOf("") }

    var isInSelectionMode by rememberSaveable { mutableStateOf(false) }
    var isSelectAll by rememberSaveable { mutableStateOf(false) }
    var shouldRefresh by remember { mutableStateOf(false) }


    val menuItemsAvailable = viewModel.menuItemsAvailable.collectAsLazyPagingItems()
    val menuItemsHidden = viewModel.menuItemsHidden.collectAsLazyPagingItems()



    val showDialogDelete = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                MenuViewModel.MenuEvents.NavigateToDetail -> {

                }

                MenuViewModel.MenuEvents.ShowDeleteDialog -> {
                    showDialogDelete.value = true
                }
            }
        }
    }

    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        val condition = handle?.get<Boolean>("added") == true ||
                handle?.get<Boolean>("updated") == true
        if (condition) {
            shouldRefresh = true
            handle?.set("added", false)
            handle?.set("updated", false)
        }
    }
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            menuItemsAvailable.refresh()
            menuItemsHidden.refresh()
            shouldRefresh = false
        }
    }

    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    navController.navigate(AddMenuItem)
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
                            text = "Danh sách món ăn"
                        )
                        SearchField(
                            searchInput = search,
                            searchChange = { search = it }
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
//                androidx.compose.animation.AnimatedVisibility(
//                    visible = isInSelectionMode,
//                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
//                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
//                ) {
//                    DeleteBar(
//                        onSelectAll = {
//                            isSelectAll = !isSelectAll
//                            viewModel.selectAllItems(menuItemsAvailable, isSelectAll)
//                        },
//                        onDeleteSelected = { viewModel.onRemoveClicked() }
//                    )
//                }
            }

            TabWithPager(
                tabs = listOf("Đang hiển thị", "Đã ẩn"),
                pages = listOf(
                    {
                        MenuItemList(
                            menuItems = menuItemsAvailable,
                            isInSelectionMode = isInSelectionMode,
                            isSelected = { menuItem -> viewModel.selectedItems.contains(menuItem) },
                            onCheckedChange = { menuItem -> viewModel.toggleSelection(menuItem) },
                            animatedVisibilityScope = animatedVisibilityScope,
                            onItemClick = {
                                navController.navigate(UpdateMenuItem(it))
                            },
                            onLongClick = {
                                isInSelectionMode = !isInSelectionMode
                            },
                        )


                    },
                    {
                        MenuItemList(
                            menuItems = menuItemsHidden,
                            isInSelectionMode = isInSelectionMode,
                            isSelected = { menuItem -> viewModel.selectedItems.contains(menuItem) },
                            onCheckedChange = { menuItem -> viewModel.toggleSelection(menuItem) },
                            animatedVisibilityScope = animatedVisibilityScope,
                            onItemClick = {},
                            onLongClick = {
                                isInSelectionMode = !isInSelectionMode
                            },
                        )
                    })
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
    statusName: String
) {

    // Lắng nghe và xử lý trạng thái loadState
    LaunchedEffect(lazyPagingItems.loadState.refresh) {
        when (val state = lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                Log.d("MenuItemScreen", "Loading $statusName MenuItems")
            }

            is LoadState.Error -> {
                Log.d("MenuItemScreen", "Error loading $statusName Orders: ${state.error.message}")
            }

            else -> {
                Log.d("MenuItemScreen", "$statusName MenuItems loaded")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Previewmenu() {

    FoodAppTheme {

    }

}