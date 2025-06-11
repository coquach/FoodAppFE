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
import com.se114.foodapp.navigation.auth.authGraph
import com.se114.foodapp.navigation.employee.employeeGraph
import com.se114.foodapp.navigation.menu.menuGraph
import com.se114.foodapp.navigation.setting.settingGraph
import com.se114.foodapp.navigation.statistics.statisticsGraph
import com.se114.foodapp.navigation.warehouse.warehouseGraph

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    shouldShowBottomNav: MutableState<Boolean>,
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
        statisticsGraph(navController, shouldShowBottomNav)
    }

}


