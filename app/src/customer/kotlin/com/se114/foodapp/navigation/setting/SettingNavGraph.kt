package com.se114.foodapp.navigation.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.AddAddress
import com.example.foodapp.navigation.MyAddressList
import com.example.foodapp.navigation.MyVoucher
import com.example.foodapp.navigation.Setting
import com.se114.foodapp.ui.screen.address.AddressListScreen

import com.se114.foodapp.ui.screen.address.addAddress.AddAddressScreen
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
    composable<MyVoucher> {
        shouldShowBottomNav.value = false
        VouchersScreen(navController)
    }
    composable<MyAddressList> {
        shouldShowBottomNav.value = false
        AddressListScreen(navController)
    }
    composable<AddAddress>{
        shouldShowBottomNav.value = false
        AddAddressScreen(navController)
    }
}
