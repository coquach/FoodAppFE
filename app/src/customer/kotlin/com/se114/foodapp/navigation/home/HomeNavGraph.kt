package com.se114.foodapp.navigation.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.data.model.Food
import com.example.foodapp.navigation.Cart
import com.example.foodapp.navigation.CheckoutCustomer
import com.example.foodapp.navigation.Favorite
import com.example.foodapp.navigation.FeedbackDetails
import com.example.foodapp.navigation.FoodDetails
import com.example.foodapp.navigation.FoodNavType
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.VoucherCheck
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.cart.CartScreen
import com.se114.foodapp.ui.screen.checkout.CheckoutScreen
import com.se114.foodapp.ui.screen.checkout.voucher_check.VoucherCheckScreen
import com.se114.foodapp.ui.screen.favorite.FavoriteScreen
import com.se114.foodapp.ui.screen.feedback.feedback_details.FeedbackDetailsScreen
import com.se114.foodapp.ui.screen.food_details.FoodDetailsScreen
import com.se114.foodapp.ui.screen.home.HomeScreen
import kotlin.reflect.typeOf

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    sharedTransitionScope: SharedTransitionScope
) {
    with(sharedTransitionScope) {
        composable<Home> {
            shouldShowBottomNav.value = true
            ScreenContainer {
                HomeScreen(
                    navController, this,
                )
            }

        }
        composable<FoodDetails>(
            typeMap = mapOf(typeOf<Food>() to FoodNavType)
        ) {
            shouldShowBottomNav.value = false
            ScreenContainer {
                FoodDetailsScreen(navController, this)
            }

        }
        composable<Favorite>{
            shouldShowBottomNav.value = true
            ScreenContainer {
                FavoriteScreen(navController, this)
            }
        }
    }
        composable<Cart> {
            shouldShowBottomNav.value = false
            ScreenContainer {
                CartScreen(navController)
            }

        }
        composable<CheckoutCustomer> {
            shouldShowBottomNav.value = false
            ScreenContainer {
                CheckoutScreen(navController)
            }

        }

        composable<VoucherCheck> {
            shouldShowBottomNav.value = false
            ScreenContainer {
                VoucherCheckScreen(navController)
            }

        }

        composable<FeedbackDetails>{
            shouldShowBottomNav.value = false
            ScreenContainer {
                FeedbackDetailsScreen(navController)
            }

        }


}
