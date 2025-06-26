package com.se114.foodapp.navigation.auth

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.Login
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.utils.ScreenContainer


fun NavGraphBuilder.authGraph(
    navController: NavHostController,
) {
    composable<Auth> {
        ScreenContainer(applyStatusBarInset = false) {  AuthScreen(navController, isCustomer = false) }

    }
    composable<Login> {
        ScreenContainer() {
            LoginScreen(navController, isCustomer = false)
        }

    }
}