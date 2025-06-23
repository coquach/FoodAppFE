package com.se114.foodapp.navigation.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.AddAddress
import com.example.foodapp.navigation.MyAddressList
import com.example.foodapp.navigation.Setting
import com.example.foodapp.navigation.VoucherPublic
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.address.AddressListScreen
import com.se114.foodapp.ui.screen.address.addAddress.AddAddressScreen
import com.se114.foodapp.ui.screen.setting.SettingScreen
import com.se114.foodapp.ui.screen.vouchers.VouchersScreen


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
    composable<VoucherPublic> {

        ScreenContainer {
            VouchersScreen(navController)
        }

    }
    composable<MyAddressList> {

        ScreenContainer {
            AddressListScreen(navController)
        }

    }
    composable<AddAddress>{

        ScreenContainer {
            AddAddressScreen(navController)
        }

    }
}
