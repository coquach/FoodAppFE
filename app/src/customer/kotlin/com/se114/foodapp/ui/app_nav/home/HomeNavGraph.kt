package com.se114.foodapp.app_nav.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.Food

import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.Checkout
import com.example.foodapp.ui.navigation.FeedbackDetails
import com.example.foodapp.ui.navigation.Feedbacks

import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.FoodDetails
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.Voucher
import com.example.foodapp.ui.navigation.FoodNavType
import com.example.foodapp.ui.navigation.VoucherCustomerCheck

import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.screen.cart.CartScreen
import com.se114.foodapp.ui.screen.checkout.CheckoutScreen
import com.se114.foodapp.ui.screen.checkout.voucher_check.VoucherCheckScreen
import com.se114.foodapp.ui.screen.feedback.FeedbackListScreen
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
            val route = it.toRoute<FoodDetails>()
            shouldShowBottomNav.value = false
            FoodDetailsScreen(navController, route.food, this)
        }
        composable<Cart> {
            shouldShowBottomNav.value = false
            CartScreen(navController)
        }
        composable<Checkout> {
            shouldShowBottomNav.value = false
            CheckoutScreen(navController)
        }
        composable<VoucherCustomerCheck> {
            shouldShowBottomNav.value = false
            VoucherCheckScreen(navController, isCustomer = true)
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
            val foodId= it.toRoute<Feedbacks>().foodId
            FeedbackListScreen(navController, foodId)
        }
        composable<FeedbackDetails>{
            shouldShowBottomNav.value = false
            val foodId= it.toRoute<Feedbacks>().foodId
            FeedbackDetailsScreen(navController, foodId)
        }
    }

}
