package com.se114.foodapp.navigation.warehouse

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.navigation.AddImportDetails
import com.example.foodapp.navigation.Import


import com.example.foodapp.navigation.Material
import com.example.foodapp.navigation.UpdateImportDetails

import com.example.foodapp.navigation.Warehouse
import com.example.foodapp.navigation.importNavType
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.warehouse.WarehouseScreen
import com.se114.foodapp.ui.screen.warehouse.imports.ImportDetailsScreen
import com.se114.foodapp.ui.screen.warehouse.imports.ImportScreen
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
    composable<Import> {
        shouldShowBottomNav.value = false
        ScreenContainer {
            ImportScreen(navController)
        }
    }

    composable<UpdateImportDetails>(
        typeMap = mapOf(typeOf<com.example.foodapp.data.model.Import>() to importNavType)
    ) {
        shouldShowBottomNav.value = false
        val route = it.toRoute<UpdateImportDetails>()
        ScreenContainer {
            ImportDetailsScreen(navController, isUpdating = true, import = route.import)
        }
    }

    composable<AddImportDetails>(
    ) {
        shouldShowBottomNav.value = false
        ScreenContainer {
            ImportDetailsScreen(navController)
        }
    }
}
