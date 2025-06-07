package com.se114.foodapp.navigation


import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.foodapp.navigation.FoodAppNavHost
import com.example.foodapp.navigation.NavRoute
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.navigation.auth.authGraph
import com.se114.foodapp.navigation.notification.notificationGraph
import com.se114.foodapp.navigation.order.orderGraph
import com.se114.foodapp.navigation.setting.settingGraph


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
) {
    FoodAppNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        authGraph(navController, shouldShowBottomNav)
        notificationGraph(navController, shouldShowBottomNav, notificationViewModel)
        orderGraph(navController, shouldShowBottomNav)
        settingGraph(navController, shouldShowBottomNav, isDarkMode, onThemeUpdated)

    }

}


