package com.se114.foodapp.navigation


import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import com.se114.foodapp.navigation.export.exportGraph
import com.se114.foodapp.navigation.home.homeGraph
import com.se114.foodapp.navigation.notification.notificationGraph
import com.se114.foodapp.navigation.order.orderGraph
import com.se114.foodapp.navigation.setting.settingGraph


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
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
        authGraph(navController )
        homeGraph(navController, sharedTransitionScope)
        notificationGraph(navController, notificationViewModel)
        orderGraph(navController)
        exportGraph(navController)
        settingGraph(navController, isDarkMode, onThemeUpdated)

    }

}


