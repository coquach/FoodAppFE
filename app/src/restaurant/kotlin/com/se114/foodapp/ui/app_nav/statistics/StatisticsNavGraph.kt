package com.se114.foodapp.ui.app_nav.statistics

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.Statistics
import com.example.foodapp.ui.screen.notification.NotificationListScreen

import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.screen.statistics.StatisticsScreen



fun NavGraphBuilder.statisticsGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    notificationViewModel: NotificationViewModel,
) {
    composable<Statistics> {
        shouldShowBottomNav.value = true
        StatisticsScreen(navController, notificationViewModel = notificationViewModel)
    }
    composable<Notification> {
        shouldShowBottomNav.value = false
        NotificationListScreen(navController)
    }
}


