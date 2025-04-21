package com.se114.foodapp.ui.app_nav.order

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.OrderList
import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.screen.home.HomeStaffScreen
import com.se114.foodapp.ui.screen.order.OrderListScreen

fun NavGraphBuilder.orderGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    ) {

    composable<OrderList> {
        shouldShowBottomNav.value = true
        OrderListScreen(navController)
    }



}