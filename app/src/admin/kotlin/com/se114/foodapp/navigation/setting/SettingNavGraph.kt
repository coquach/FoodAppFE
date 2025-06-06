package com.se114.foodapp.navigation.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.FoodTableAdmin
import com.example.foodapp.navigation.Setting
import com.example.foodapp.navigation.Supplier
import com.example.foodapp.navigation.Voucher
import com.se114.foodapp.ui.screen.voucher.VoucherListScreen
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.food_table.FoodTableScreen
import com.se114.foodapp.ui.screen.setting.SettingScreen
import com.se114.foodapp.ui.screen.supplier.SupplierScreen


fun NavGraphBuilder.settingGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    isDarkMode: Boolean,
    onThemeUpdated: () -> Unit
) {
    composable<Setting> {
        shouldShowBottomNav.value = true
        ScreenContainer(isBottomBarVisible = true) {
            SettingScreen(navController, isDarkMode, onThemeUpdated)
        }

    }
    composable<Supplier> {
        shouldShowBottomNav.value = false
        ScreenContainer {
            SupplierScreen(navController)
        }

    }
    composable<Voucher> {
        shouldShowBottomNav.value = false
        ScreenContainer {
            VoucherListScreen(navController)
        }
    }
    composable<FoodTableAdmin>{
        shouldShowBottomNav.value = false
        ScreenContainer {
            FoodTableScreen(navController)
        }
    }
}
