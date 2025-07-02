package com.se114.foodapp.navigation.home
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.navigation.CheckoutStaff
import com.example.foodapp.navigation.GetFoodForStaff
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.OrderSuccess
import com.example.foodapp.navigation.VoucherCheck
import com.example.foodapp.navigation.VoucherCheckStaff
import com.example.foodapp.ui.screen.order_success.OrderSuccessScreen
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.checkout.CheckoutScreen
import com.se114.foodapp.ui.screen.checkout.get_foods.GetFoodsScreen
import com.se114.foodapp.ui.screen.checkout.voucher_check.VoucherCheckScreen
import com.se114.foodapp.ui.screen.home.HomeStaffScreen

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope
) {

        composable<Home> {

            ScreenContainer {
                HomeStaffScreen(navController)
            }

        }


    composable<CheckoutStaff> {

        ScreenContainer {
            CheckoutScreen(navController)
        }

    }

    with(sharedTransitionScope) {
        composable<GetFoodForStaff> {
            ScreenContainer {
                GetFoodsScreen(
                    navController,
                    animatedVisibilityScope = this
                )
            }
        }
    }

    composable<VoucherCheckStaff>{
        ScreenContainer {
            VoucherCheckScreen(navController)
        }
    }

    composable<OrderSuccess> {
        val orderID = it.toRoute<OrderSuccess>().orderId
        ScreenContainer {
            OrderSuccessScreen(navController = navController, orderID = orderID)
        }
    }




}
