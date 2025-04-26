package com.se114.foodapp.ui.app_nav.order

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.Order

import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.OrderDetails
import com.example.foodapp.ui.navigation.OrderList
import com.example.foodapp.ui.navigation.orderNavType
import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.ui.screen.order.order_detail.OrderDetailScreen
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.home.HomeStaffScreen
import com.se114.foodapp.ui.screen.order.OrderListScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.orderGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    ) {

    composable<OrderList> {
        shouldShowBottomNav.value = true
        ScreenContainer {
            OrderListScreen(navController)
        }

    }
    composable<OrderDetails>(
        typeMap = mapOf(typeOf<Order>() to orderNavType)
    ) {
        val route = it.toRoute<OrderDetails>()
        shouldShowBottomNav.value = false
        ScreenContainer {
            OrderDetailScreen(navController, route.order, isStaff = true)
        }

    }



}