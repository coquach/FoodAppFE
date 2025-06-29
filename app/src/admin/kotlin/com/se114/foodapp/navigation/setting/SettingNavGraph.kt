package com.se114.foodapp.navigation.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.ChatBoxAdmin
import com.example.foodapp.navigation.FoodTableAdmin
import com.example.foodapp.navigation.Setting
import com.example.foodapp.navigation.Supplier
import com.example.foodapp.navigation.Voucher
import com.se114.foodapp.ui.screen.voucher.VoucherListScreen
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.chat_box.ChatBoxScreen
import com.se114.foodapp.ui.screen.food_table.FoodTableScreen
import com.se114.foodapp.ui.screen.setting.SettingScreen
import com.se114.foodapp.ui.screen.supplier.SupplierScreen


fun NavGraphBuilder.settingGraph(
    navController: NavHostController,
    isDarkMode: Boolean,
    onThemeUpdated: () -> Unit
) {
    composable<Setting> {
        ScreenContainer {
            SettingScreen(navController, isDarkMode, onThemeUpdated)
        }

    }
    composable<Supplier> {
        ScreenContainer {
            SupplierScreen(navController)
        }

    }
    composable<Voucher> {
        ScreenContainer {
            VoucherListScreen(navController)
        }
    }
    composable<FoodTableAdmin>{
        ScreenContainer {
            FoodTableScreen(navController)
        }
    }
    composable<ChatBoxAdmin>{
        ScreenContainer {
            ChatBoxScreen(navController)
        }
    }
}
