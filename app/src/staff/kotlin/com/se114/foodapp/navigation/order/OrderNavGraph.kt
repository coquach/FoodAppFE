package com.se114.foodapp.navigation.order

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.Order
import com.example.foodapp.navigation.OrderDetails
import com.example.foodapp.navigation.OrderList
import com.example.foodapp.navigation.OrderSuccess
import com.example.foodapp.navigation.orderNavType
import com.example.foodapp.ui.screen.order_success.OrderSuccessScreen
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.order.OrderListScreen
import com.se114.foodapp.ui.screen.order.order_detail.OrderDetailScreen
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
    composable<OrderSuccess> {
        val orderID = it.toRoute<OrderSuccess>().orderId
        shouldShowBottomNav.value = false
        ScreenContainer {
            OrderSuccessScreen(orderID, navController)
        }

    }
    composable<OrderDetails>(
        typeMap = mapOf(typeOf<Order>() to orderNavType)
    ) {
        val route = it.toRoute<OrderDetails>()
        shouldShowBottomNav.value = false
        ScreenContainer {
            OrderDetailScreen(navController)
        }

    }



}