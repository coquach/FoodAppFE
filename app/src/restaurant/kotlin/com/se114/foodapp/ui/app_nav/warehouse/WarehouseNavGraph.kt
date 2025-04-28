package com.se114.foodapp.ui.app_nav.warehouse

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.ui.navigation.Import
import com.example.foodapp.ui.navigation.ImportDetails
import com.example.foodapp.ui.navigation.Material

import com.example.foodapp.ui.navigation.Warehouse
import com.example.foodapp.ui.navigation.importNavType
import com.example.foodapp.ui.navigation.menuItemNavType
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.warehouse.WarehouseScreen
import com.se114.foodapp.ui.screen.warehouse.material.MaterialScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.warehouseGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
    composable<Warehouse>{
        shouldShowBottomNav.value = true
        ScreenContainer {
            WarehouseScreen(navController)
        }

    }
    composable<Material> {
        shouldShowBottomNav.value = false
        ScreenContainer {
            MaterialScreen(navController)
        }
    }
    composable<Import> {  }

    composable<ImportDetails>(
        typeMap = mapOf(typeOf<Import>() to importNavType)
    ) {

    }
}
