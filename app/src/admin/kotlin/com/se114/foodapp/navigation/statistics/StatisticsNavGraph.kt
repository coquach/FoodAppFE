package com.se114.foodapp.navigation.statistics

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.Notification
import com.example.foodapp.navigation.Statistics
import com.example.foodapp.ui.screen.notification.NotificationListScreen

import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.statistics.StatisticsScreen



fun NavGraphBuilder.statisticsGraph(
    navController: NavHostController,
) {
    composable<Home> {
        ScreenContainer(isBottomBarVisible = true) {
            StatisticsScreen(navController)
        }

    }

}


