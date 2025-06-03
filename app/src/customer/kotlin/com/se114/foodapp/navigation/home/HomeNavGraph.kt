package com.se114.foodapp.navigation.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.data.model.Food
import com.example.foodapp.navigation.Cart
import com.example.foodapp.navigation.Checkout
import com.example.foodapp.navigation.FeedbackDetails
import com.example.foodapp.navigation.FoodDetails
import com.example.foodapp.navigation.FoodNavType
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.Voucher
import com.example.foodapp.navigation.VoucherCheck

import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.screen.cart.CartScreen
import com.se114.foodapp.ui.screen.checkout.CheckoutScreen
import com.se114.foodapp.ui.screen.checkout.voucher_check.VoucherCheckScreen
import com.se114.foodapp.ui.screen.feedback.feedback_details.FeedbackDetailsScreen
import com.se114.foodapp.ui.screen.food_details.FoodDetailsScreen

import com.se114.foodapp.ui.screen.home.HomeScreen
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
        composable<FoodDetails>(
            typeMap = mapOf(typeOf<Food>() to FoodNavType)
        ) {
            shouldShowBottomNav.value = false
            FoodDetailsScreen(navController, this)
        }
        composable<Cart> {
            shouldShowBottomNav.value = false
            CartScreen(navController)
        }
        composable<Checkout> {
            shouldShowBottomNav.value = false
            CheckoutScreen(navController)
        }

        composable<VoucherCheck> {
            shouldShowBottomNav.value = false
            VoucherCheckScreen(navController)
        }

        composable<FeedbackDetails>{
            shouldShowBottomNav.value = false
            FeedbackDetailsScreen(navController)
        }
    }

}
