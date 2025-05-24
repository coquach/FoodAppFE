package com.se114.foodapp.ui.app_nav.order

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.Order
import com.example.foodapp.ui.navigation.AddAddress
import com.example.foodapp.ui.navigation.AddressListCheckout
import com.example.foodapp.ui.navigation.OrderDetails
import com.example.foodapp.ui.navigation.OrderList
import com.example.foodapp.ui.navigation.OrderSuccess
import com.example.foodapp.ui.navigation.orderNavType
import com.se114.foodapp.ui.screen.order.OrderListScreen
import com.example.foodapp.ui.screen.order.order_detail.OrderDetailScreen
import com.example.foodapp.ui.screen.order.order_success.OrderSuccessScreen
import com.se114.foodapp.ui.screen.address.AddressListScreen
import com.se114.foodapp.ui.screen.address.addAddress.AddAddressScreen
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
    ) {
        val route = it.toRoute<OrderDetails>()
        shouldShowBottomNav.value = false
        OrderDetailScreen(navController, route.order)
    }
    composable<AddressListCheckout> {
        shouldShowBottomNav.value = false
        AddressListScreen(navController, isCheckout = true)
    }
    composable<AddAddress> {
        shouldShowBottomNav.value = false
        AddAddressScreen(navController)
    }
}
