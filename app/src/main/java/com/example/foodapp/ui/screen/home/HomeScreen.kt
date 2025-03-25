package com.example.foodapp.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.ui.ItemCount
import com.example.foodapp.ui.MyFloatingActionButton
import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.screen.home.categories.CategoriesList
import com.example.foodapp.ui.screen.notification.NotificationViewModel
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
                        painter = painterResource(id = R.drawable.ic_cart),
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        contentDescription = null,
                        modifier = Modifier.align(Center)
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
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(50.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_nofication),
                            tint = MaterialTheme.colorScheme.primaryContainer,
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
                val uiSate = viewModel.uiState.collectAsStateWithLifecycle()
                when (uiSate.value) {
                    is HomeViewModel.HomeState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is HomeViewModel.HomeState.Empty -> {
                        // Show empty state message
                    }

                    is HomeViewModel.HomeState.Success -> {
                        val categories = viewModel.categories
                        CategoriesList(categories = categories, onCategorySelected = {

                        })
                    }
                }
            }
        }
    }


}


//Fake data

