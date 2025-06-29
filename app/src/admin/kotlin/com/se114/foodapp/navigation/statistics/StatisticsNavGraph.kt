package com.se114.foodapp.navigation.statistics

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Home
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.statistics.StatisticsScreen


fun NavGraphBuilder.statisticsGraph(
    navController: NavHostController,
) {
    composable<Home> {
        ScreenContainer {
            StatisticsScreen(navController)
        }

    }

}


