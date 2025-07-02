package com.se114.foodapp.navigation.setting

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Contact
import com.example.foodapp.navigation.Help
import com.example.foodapp.navigation.Privacy
import com.example.foodapp.navigation.Setting
import com.example.foodapp.ui.screen.setting.contact.ContactScreen
import com.example.foodapp.ui.screen.setting.help.HelpScreen
import com.example.foodapp.ui.screen.setting.privacy.PrivacyScreen
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.setting.SettingScreen

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
    composable<Help> {
        ScreenContainer {
            HelpScreen(navController, "shipper")
        }
    }
    composable<Contact> {
        ScreenContainer {
            ContactScreen(navController)
        }
    }
    composable<Privacy> {
        ScreenContainer {
            PrivacyScreen(navController)
        }
    }

}