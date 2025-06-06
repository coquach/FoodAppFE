package com.se114.foodapp.ui.app_nav

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.FoodAppNavHost
import com.example.foodapp.navigation.NavRoute
import com.example.foodapp.navigation.Welcome
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.navigation.auth.authGraph
import com.se114.foodapp.navigation.home.homeGraph
import com.se114.foodapp.navigation.order.orderGraph
import com.se114.foodapp.navigation.setting.settingGraph
import com.example.foodapp.ui.screen.welcome.WelcomeScreen
import com.se114.foodapp.navigation.notification.notificationGraph

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    shouldShowBottomNav: MutableState<Boolean>,
    notificationViewModel: NotificationViewModel,
    startDestination: NavRoute,
    isDarkMode: Boolean,
    onThemeUpdated: () -> Unit,
    sharedTransitionScope: SharedTransitionScope
) {
        FoodAppNavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            authGraph(navController, shouldShowBottomNav)
            homeGraph(navController, shouldShowBottomNav, sharedTransitionScope)
            orderGraph(navController, shouldShowBottomNav)
            notificationGraph(navController, shouldShowBottomNav, notificationViewModel)
            settingGraph(navController, shouldShowBottomNav, isDarkMode, onThemeUpdated)
            composable<Welcome> {
                shouldShowBottomNav.value = false
                WelcomeScreen(navController)
            }
        }

}


