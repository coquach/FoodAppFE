package com.se114.foodapp.app_nav.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.ui.navigation.Profile
import com.example.foodapp.ui.navigation.Setting
import com.se114.foodapp.ui.screen.setting.SettingScreen
import com.se114.foodapp.ui.screen.setting.profile.ProfileScreen

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
    composable<Profile> {
        shouldShowBottomNav.value = false
        ProfileScreen(navController)
    }
}
