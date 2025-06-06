package com.se114.foodapp.navigation.export

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.data.model.Export
import com.example.foodapp.navigation.ExportDetails
import com.example.foodapp.navigation.exportNavType

import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.export.ExportScreen
import com.se114.foodapp.ui.screen.export.export_detail.ExportDetailsScreen
import java.time.LocalDate
import kotlin.reflect.typeOf

fun NavGraphBuilder.exportGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
    composable<com.example.foodapp.navigation.Export> {
        shouldShowBottomNav.value = true
        ScreenContainer {
            ExportScreen(navController)
        }
    }

    composable<ExportDetails>(
        typeMap = mapOf( typeOf<Export>() to exportNavType)
    ) {
        shouldShowBottomNav.value = false
        ScreenContainer {
            ExportDetailsScreen(navController)
        }
    }


}