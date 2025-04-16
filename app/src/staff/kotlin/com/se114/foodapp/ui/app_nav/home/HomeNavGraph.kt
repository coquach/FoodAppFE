package com.se114.foodapp.app_nav.home
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.se114.foodapp.ui.screen.home.HomeStaffScreen

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    notificationViewModel: NotificationViewModel,

) {

        composable<Home> {
            shouldShowBottomNav.value = true
            HomeStaffScreen(navController, notificationViewModel)
        }
        composable<Notification> {
            shouldShowBottomNav.value = false
            NotificationListScreen(navController)
        }


}
