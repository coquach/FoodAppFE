package com.se114.foodapp.ui.app_nav.auth

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen


fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
    composable<Auth> {
        shouldShowBottomNav.value = false
        AuthScreen(navController)
    }
    composable<Login> {
        shouldShowBottomNav.value = false
        LoginScreen(navController, isCustomer = false)
    }
}