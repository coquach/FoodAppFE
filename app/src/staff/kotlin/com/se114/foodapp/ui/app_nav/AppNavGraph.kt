package com.se114.foodapp.ui.app_nav


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.foodapp.ui.navigation.FoodAppNavHost
import com.example.foodapp.ui.navigation.NavRoute
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.app_nav.auth.authGraph
import com.se114.foodapp.ui.app_nav.home.homeGraph
import com.se114.foodapp.ui.app_nav.order.orderGraph


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
        homeGraph(navController, shouldShowBottomNav, notificationViewModel)
        orderGraph(navController, shouldShowBottomNav)

    }

}


