package com.se114.foodapp.ui.app_nav


import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import com.se114.foodapp.ui.app_nav.employee.employeeGraph
import com.se114.foodapp.ui.app_nav.menu.menuGraph
import com.se114.foodapp.ui.app_nav.setting.settingGraph
import com.se114.foodapp.ui.app_nav.statistics.statisticsGraph
import com.se114.foodapp.ui.app_nav.warehouse.warehouseGraph

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
        employeeGraph(navController, shouldShowBottomNav)
        menuGraph(
            navController, shouldShowBottomNav,
            sharedTransitionScope
        )
        warehouseGraph(navController, shouldShowBottomNav)
        settingGraph(navController, shouldShowBottomNav, isDarkMode, onThemeUpdated)
        statisticsGraph(navController, shouldShowBottomNav, notificationViewModel)
    }

}


