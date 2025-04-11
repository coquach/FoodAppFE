package com.se114.foodapp.app_nav

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.SplashViewModel
import com.example.foodapp.ui.navigation.FoodAppNavHost
import com.example.foodapp.ui.navigation.NavRoute
import com.example.foodapp.ui.navigation.Welcome
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.app_nav.auth.authGraph
import com.se114.foodapp.app_nav.home.homeGraph
import com.se114.foodapp.app_nav.order.orderGraph
import com.se114.foodapp.app_nav.setting.settingGraph
import com.example.foodapp.ui.screen.welcome.WelcomeScreen

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
            homeGraph(navController, shouldShowBottomNav, notificationViewModel, sharedTransitionScope)
            orderGraph(navController, shouldShowBottomNav)
            settingGraph(navController, shouldShowBottomNav, isDarkMode, onThemeUpdated)
            composable<Welcome> {
                shouldShowBottomNav.value = false
                WelcomeScreen(navController)
            }
        }

}


