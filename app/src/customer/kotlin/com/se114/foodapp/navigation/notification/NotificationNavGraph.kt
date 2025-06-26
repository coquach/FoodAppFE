package com.se114.foodapp.navigation.notification

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Notification
import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.utils.ScreenContainer

fun NavGraphBuilder.notificationGraph(
    navController: NavHostController,
    notificationViewModel: NotificationViewModel,
){
    composable<Notification> {


        ScreenContainer {
            NotificationListScreen(navController, notificationViewModel)
        }


    }
}