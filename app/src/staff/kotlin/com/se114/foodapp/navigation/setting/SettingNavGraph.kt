package com.se114.foodapp.navigation.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.FoodTableStaff
import com.example.foodapp.navigation.Setting
import com.example.foodapp.navigation.VoucherPublic
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.food_table.FoodTableScreen
import com.se114.foodapp.ui.screen.setting.SettingScreen
import com.se114.foodapp.ui.screen.vouchers.VouchersScreen

fun NavGraphBuilder.settingGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    isDarkMode: Boolean,
    onThemeUpdated: () -> Unit
) {
    composable<Setting> {
        shouldShowBottomNav.value = true
        ScreenContainer {
            SettingScreen(navController, isDarkMode, onThemeUpdated)
        }

    }
    composable<VoucherPublic> {
        shouldShowBottomNav.value = false
        ScreenContainer {
            VouchersScreen(navController)
        }

    }
    composable<FoodTableStaff> {
        shouldShowBottomNav.value = false
        ScreenContainer {
            FoodTableScreen(navController)
        }
    }


}