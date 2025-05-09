package com.se114.foodapp.ui.app_nav.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.ui.navigation.MyVoucher

import com.example.foodapp.ui.navigation.Setting
import com.example.foodapp.ui.navigation.UpdateProfile
import com.example.foodapp.ui.navigation.Voucher

import com.example.foodapp.ui.screen.auth.signup.profile.ProfileScreen
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
        SettingScreen(navController, isDarkMode, onThemeUpdated)
    }
    composable<UpdateProfile> {
        shouldShowBottomNav.value = false
        ProfileScreen(navController, isUpdating = true)
    }
    composable<MyVoucher> {
        shouldShowBottomNav.value = false
        VouchersScreen(navController, isMyVoucher = true)
    }
}
