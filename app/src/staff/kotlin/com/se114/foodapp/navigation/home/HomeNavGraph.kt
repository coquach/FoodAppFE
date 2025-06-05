package com.se114.foodapp.navigation.home
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Cart
import com.example.foodapp.navigation.CheckoutStaff
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.VoucherCheck
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.cart.CartScreen
import com.se114.foodapp.ui.screen.checkout.CheckoutScreen
import com.se114.foodapp.ui.screen.checkout.voucher_check.VoucherCheckScreen
import com.se114.foodapp.ui.screen.home.HomeStaffScreen

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
                HomeStaffScreen(navController, this)
            }

        }
    }

    composable<Cart>{
        shouldShowBottomNav.value = false
        ScreenContainer {
            CartScreen(navController)
        }

    }
    composable<CheckoutStaff> {
        shouldShowBottomNav.value = false
        ScreenContainer {
            CheckoutScreen(navController)
        }

    }
    composable<VoucherCheck>{
        shouldShowBottomNav.value = false
        ScreenContainer {
            VoucherCheckScreen(navController)
        }
    }




}
