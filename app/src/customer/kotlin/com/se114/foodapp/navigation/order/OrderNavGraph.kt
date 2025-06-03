package com.se114.foodapp.navigation.order

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Order
import com.example.foodapp.navigation.FoodNavType
import com.example.foodapp.navigation.OrderDetails
import com.example.foodapp.navigation.OrderList
import com.example.foodapp.navigation.OrderSuccess
import com.example.foodapp.navigation.orderNavType
import com.se114.foodapp.ui.screen.order.OrderListScreen
import com.example.foodapp.ui.screen.order.order_detail.OrderDetailScreen
import com.example.foodapp.ui.screen.order.order_success.OrderSuccessScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.orderGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
    composable<OrderList> {
        shouldShowBottomNav.value = true
        OrderListScreen(navController)
    }
    composable<OrderSuccess> {
        val orderID = it.toRoute<OrderSuccess>().orderId
        shouldShowBottomNav.value = false
        OrderSuccessScreen(orderID, navController)
    }
    composable<OrderDetails>(
        typeMap = mapOf(typeOf<Order>() to orderNavType)
    ){
        shouldShowBottomNav.value = false
        OrderDetailScreen(navController)
    }

}
