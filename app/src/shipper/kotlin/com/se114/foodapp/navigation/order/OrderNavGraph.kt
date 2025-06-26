package com.se114.foodapp.navigation.order

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.data.model.Order
import com.example.foodapp.navigation.OrderDetailsStaff
import com.example.foodapp.navigation.OrderList
import com.example.foodapp.navigation.Tracking
import com.example.foodapp.navigation.orderNavType
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.order.OrderListScreen
import com.se114.foodapp.ui.screen.order.order_detail.OrderDetailScreen
import com.se114.foodapp.ui.screen.tracking.TrackingScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.orderGraph(
    navController: NavHostController,
    ) {

    composable<OrderList> {
        ScreenContainer {
            OrderListScreen(navController)
        }

    }

    composable<OrderDetailsStaff>(
        typeMap = mapOf(typeOf<Order>() to orderNavType)
    ) {
        ScreenContainer {
            OrderDetailScreen(navController)
        }

    }
    composable<Tracking> {
        ScreenContainer {
            TrackingScreen(navController)
        }
    }



}