package com.se114.foodapp.ui.screen.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems

import com.example.foodapp.R
import com.example.foodapp.navigation.Cart
import com.example.foodapp.navigation.FoodDetails
import com.example.foodapp.ui.screen.common.FoodList
import com.example.foodapp.ui.screen.components.ItemCount


import com.example.foodapp.ui.screen.components.MyFloatingActionButton

import com.example.foodapp.ui.screen.components.SearchField

import com.example.foodapp.ui.screen.notification.NotificationViewModel

import com.se114.foodapp.ui.screen.chat_box.ChatBoxScreen
import com.se114.foodapp.ui.screen.home.banner.Banners


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel(),

    ) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foods = viewModel.getFoodsByMenuId(uiState.menuIdSelected).collectAsLazyPagingItems()
    val menus = viewModel.menus.collectAsLazyPagingItems()
    var isOpenChatBox by remember { mutableStateOf(false) }

    Log.d("isOpenChatBox", isOpenChatBox.toString())
    
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { event ->
                when (event) {
                    Home.Event.GoToCart -> {
                        navController.navigate(Cart)
                    }
                    is Home.Event.GoToDetails -> {
                        navController.navigate(FoodDetails(event.food))
                    }
                    Home.Event.ShowChatBox -> {
                        isOpenChatBox = true
                    }
                    Home.Event.ShowError -> {

                    }
                }
            }
    }

    Scaffold(
        floatingActionButton =
            {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MyFloatingActionButton(
                        onClick = {
                            isOpenChatBox = true
                        },
                        bgColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(R.drawable.chatbot_ic),
                                contentDescription = "Chat box",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                            )
                        }

                    }
                    MyFloatingActionButton(
                        onClick = {
                            viewModel.onAction(Home.Action.OnCartClicked)
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

                            if (uiState.cartSize > 0) {
                                ItemCount(uiState.cartSize)
                            }
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
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {


                    Spacer(modifier = Modifier.size(20.dp))
                    Banners(onClick = {
                    })
                }
            }




            FoodList(
                foods = foods,
                animatedVisibilityScope = animatedVisibilityScope,
                onItemClick = {
                    viewModel.onAction(Home.Action.OnFoodClicked(it))
                },
                isCustomer = true,
            )


        }


    }
    if (isOpenChatBox) {
        ChatBoxScreen(
            onDismiss = {
                isOpenChatBox = false
            },
            modifier = Modifier.height(700.dp)
        )
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
