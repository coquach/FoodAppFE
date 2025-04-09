package com.se114.foodapp.app_nav.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.Checkout
import com.example.foodapp.ui.navigation.FoodDetails
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.foodItemNavType
import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.screen.cart.CartScreen
import com.se114.foodapp.ui.screen.checkout.CheckoutScreen
import com.se114.foodapp.ui.screen.food_item_details.FoodDetailsScreen
import com.se114.foodapp.ui.screen.home.HomeScreen
import kotlin.reflect.typeOf

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    notificationViewModel: NotificationViewModel,
    sharedTransitionScope: SharedTransitionScope
) {
    with(sharedTransitionScope) {
        composable<Home> {
            shouldShowBottomNav.value = true
            HomeScreen(
                navController, this,
                notificationViewModel = notificationViewModel
            )
        }
        composable<FoodDetails>(
            typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
        ) {
            val route = it.toRoute<FoodDetails>()
            shouldShowBottomNav.value = false
            FoodDetailsScreen(navController, route.foodItem, this)
        }
        composable<Cart> {
            shouldShowBottomNav.value = false
            CartScreen(navController)
        }
        composable<Checkout> {
            shouldShowBottomNav.value = false
            CheckoutScreen(navController)
        }
        composable<Notification> {
            shouldShowBottomNav.value = false
            NotificationListScreen(navController)
        }
    }

}
