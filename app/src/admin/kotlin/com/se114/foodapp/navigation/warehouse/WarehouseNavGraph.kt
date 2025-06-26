package com.se114.foodapp.navigation.warehouse


import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Import
import com.example.foodapp.navigation.ImportDetails
import com.example.foodapp.navigation.Material
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
) {
    composable<Warehouse>{
        ScreenContainer(isBottomBarVisible = true) {
            WarehouseScreen(navController)
        }

    }
    composable<Material> {
        ScreenContainer {
            MaterialScreen(navController)
        }
    }
    composable<Import> {

        ScreenContainer {
            ImportScreen(navController)
        }
    }

    composable<ImportDetails>(
        typeMap = mapOf(
            typeOf<com.example.foodapp.data.model.Import>() to importNavType
        ),

    ) {
        ScreenContainer {
            ImportDetailsScreen(navController)
        }
    }

}
