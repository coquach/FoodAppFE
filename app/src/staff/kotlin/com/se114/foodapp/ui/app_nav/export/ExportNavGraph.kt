package com.se114.foodapp.ui.app_nav.export

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.ui.navigation.AddExportDetails

import com.example.foodapp.ui.navigation.AddImportDetails
import com.example.foodapp.ui.navigation.Export
import com.example.foodapp.ui.navigation.Import
import com.example.foodapp.ui.navigation.UpdateExportDetails
import com.example.foodapp.ui.navigation.UpdateImportDetails
import com.example.foodapp.ui.navigation.importNavType
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.export.ExportScreen
import com.se114.foodapp.ui.screen.export.export_detail.ExportDetailsScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.exportGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
    composable<Export> {
        shouldShowBottomNav.value = true
        ScreenContainer {
            ExportScreen(navController)
        }
    }

    composable<UpdateExportDetails>(
        typeMap = mapOf(typeOf<com.example.foodapp.data.model.Export>() to importNavType)
    ) {
        shouldShowBottomNav.value = false
        val route = it.toRoute<UpdateExportDetails>()
        ScreenContainer {
            ExportDetailsScreen(navController, isUpdating = true, export = route.export)
        }
    }

    composable<AddExportDetails>(
    ) {
        shouldShowBottomNav.value = false
        ScreenContainer {
            ExportDetailsScreen(navController)
        }
    }
}