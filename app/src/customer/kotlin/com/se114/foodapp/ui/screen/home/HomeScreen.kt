package com.se114.foodapp.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.ItemCount
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.Feedbacks
import com.example.foodapp.ui.navigation.MenuItemDetails
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.Voucher
import com.example.foodapp.ui.screen.common.MenuItemList

import com.example.foodapp.ui.screen.common.MenuItemView
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.screen.home.banner.Banners
import com.se114.foodapp.ui.screen.vouchers.VouchersScreen
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel

) {
    val unReadCount by notificationViewModel.unreadCount.collectAsState()
    val cartSize by viewModel.cartSize.collectAsState()
    val menuItems = viewModel.menuItems.collectAsLazyPagingItems()

    var searchInput by remember { mutableStateOf("") }
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest {
            when (it) {
                HomeViewModel.HomeNavigationEvents.NavigateToDetail -> {

                }

                HomeViewModel.HomeNavigationEvents.NavigateToNotification -> {
                    navController.navigate(Notification)
                }

            }
        }
    }

    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    navController.navigate(Cart)
                },
                bgColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Box(modifier = Modifier.size(56.dp)) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Center)
                            .size(24.dp)
                    )

                    if (cartSize > 0) {
                        ItemCount(cartSize)
                    }
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
                    .height(400.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.size(50.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_nofication),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Center)
                                    .clickable {
                                        viewModel.onNotificationClicked()
                                    }
                            )

                            if (unReadCount > 0) {
                                ItemCount(unReadCount)
                            }

                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    SearchField(
                        searchInput = searchInput,
                        searchChange = { searchInput = it }
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Banners(onClick = {
                        navController.navigate(Feedbacks(1))
                    })
                }
            }


//            val uiSate = viewModel.uiState.collectAsStateWithLifecycle()
//            when (uiSate.value) {
//                is HomeViewModel.HomeState.Loading -> {
//                    CircularProgressIndicator()
//                }
//
//                is HomeViewModel.HomeState.Empty -> {
//                    // Show empty state message
//                }
//
//                is HomeViewModel.HomeState.Success -> {
//                    val categories = viewModel.categories
//                    CategoriesList(categories = categories, onCategorySelected = {
//
//                    })


            MenuItemList(
                menuItems = menuItems,
                animatedVisibilityScope = animatedVisibilityScope,
                onItemClick = {
                    navController.navigate(
                        MenuItemDetails(
                            menuItem = it
                        )
                    )
                },
                isCustomer = true,
                onLongClick = {},
                onCheckedChange = {},
            )


        }


    }
}
//fun LazyListScope.gridItems(
//    count: Int,
//    nColumns: Int,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
//    itemContent: @Composable BoxScope.(Int) -> Unit,
//) {
//    gridItems(
//        data = List(count) { it },
//        nColumns = nColumns,
//        horizontalArrangement = horizontalArrangement,
//        itemContent = itemContent,
//    )
//}
//
//fun <T> LazyListScope.gridItems(
//    data: List<T>,
//    nColumns: Int,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
//    key: ((item: T) -> Any)? = null,
//    itemContent: @Composable BoxScope.(T) -> Unit,
//) {
//    val rows = if (data.isEmpty()) 0 else 1 + (data.count() - 1) / nColumns
//    items(rows) { rowIndex ->
//        Row(horizontalArrangement = horizontalArrangement) {
//            for (columnIndex in 0 until nColumns) {
//                val itemIndex = rowIndex * nColumns + columnIndex
//                if (itemIndex < data.count()) {
//                    val item = data[itemIndex]
//                    androidx.compose.runtime.key(key?.invoke(item)) {
//                        Box(
//                            modifier = Modifier.weight(1f, fill = true),
//                            propagateMinConstraints = true
//                        ) {
//                            itemContent.invoke(this, item)
//                        }
//                    }
//                } else {
//                    Spacer(Modifier.weight(1f, fill = true))
//                }
//            }
//        }
//    }
//}
//
//
//
