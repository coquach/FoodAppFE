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
    sharedTransitionScope: SharedTransitionScope
) {
    with(sharedTransitionScope) {
        composable<Home> {

            ScreenContainer {
                HomeStaffScreen(navController, this)
            }

        }
    }

    composable<Cart>{

        ScreenContainer {
            CartScreen(navController)
        }

    }
    composable<CheckoutStaff> {

        ScreenContainer {
            CheckoutScreen(navController)
        }

    }
    composable<VoucherCheck>{
        ScreenContainer {
            VoucherCheckScreen(navController)
        }
    }




}
