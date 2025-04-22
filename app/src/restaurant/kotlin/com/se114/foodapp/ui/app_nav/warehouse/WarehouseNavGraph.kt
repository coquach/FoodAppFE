package com.se114.foodapp.ui.app_nav.warehouse

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

import com.example.foodapp.ui.navigation.Warehouse
import com.se114.foodapp.ui.screen.warehouse.WarehouseScreen

fun NavGraphBuilder.warehouseGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
    composable<Warehouse>{
        shouldShowBottomNav.value = true
        WarehouseScreen(navController)
    }
}
