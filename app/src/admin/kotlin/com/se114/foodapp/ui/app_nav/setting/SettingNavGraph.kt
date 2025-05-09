package com.se114.foodapp.ui.app_nav.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.ui.navigation.Category
import com.example.foodapp.ui.navigation.Setting
import com.example.foodapp.ui.navigation.Supplier
import com.example.foodapp.ui.navigation.Voucher
import com.example.foodapp.ui.screen.voucher.VoucherListScreen
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.setting.SettingScreen
import com.se114.foodapp.ui.screen.menu.category.CategoryScreen
import com.se114.foodapp.ui.screen.setting.supplier.SupplierScreen


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
}
