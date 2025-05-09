package com.se114.foodapp.app_nav.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.MenuItem

import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.Checkout
import com.example.foodapp.ui.navigation.FeedbackDetails
import com.example.foodapp.ui.navigation.Feedbacks

import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.MenuItemDetails
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.Voucher
import com.example.foodapp.ui.navigation.menuItemNavType

import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.screen.cart.CartScreen
import com.se114.foodapp.ui.screen.checkout.CheckoutScreen
import com.se114.foodapp.ui.screen.feedback.FeedbackListScreen
import com.se114.foodapp.ui.screen.feedback.feedback_details.FeedbackDetailsScreen

import com.se114.foodapp.ui.screen.home.HomeScreen
import com.se114.foodapp.ui.screen.menu_item_details.MenuDetailsScreen
import com.se114.foodapp.ui.screen.vouchers.VouchersScreen
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
        composable<MenuItemDetails>(
            typeMap = mapOf(typeOf<MenuItem>() to menuItemNavType)
        ) {
            val route = it.toRoute<MenuItemDetails>()
            shouldShowBottomNav.value = false
            MenuDetailsScreen(navController, route.menuItem, this)
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
        composable<Voucher> {
            shouldShowBottomNav.value = false
            VouchersScreen(navController)
        }
        composable<Feedbacks> {
            shouldShowBottomNav.value = false
            val menuItemId= it.toRoute<Feedbacks>().menuItemId
            FeedbackListScreen(navController, menuItemId)
        }
        composable<FeedbackDetails>{
            shouldShowBottomNav.value = false
            val menuItemId= it.toRoute<Feedbacks>().menuItemId
            FeedbackDetailsScreen(navController, menuItemId)
        }
    }

}
