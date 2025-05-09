package com.se114.foodapp.ui.app_nav.auth

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.ResetPasswordArgs
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.navigation.ResetPassword
import com.example.foodapp.ui.navigation.ResetPasswordSuccess
import com.example.foodapp.ui.navigation.SendEmail
import com.example.foodapp.ui.navigation.resetPasswordNavType
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.forgot_password.change_password.ChangePasswordScreen
import com.example.foodapp.ui.screen.auth.forgot_password.reset_success.ResetPassSuccessScreen
import com.example.foodapp.ui.screen.auth.forgot_password.send_email.SendEmailScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.utils.ScreenContainer
import kotlin.reflect.typeOf

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
    composable<Auth> {
        shouldShowBottomNav.value = false
        ScreenContainer(applyStatusBarInset = false) {
            AuthScreen(navController, isCustomer = false)
        }

    }
    composable<Login> {
        shouldShowBottomNav.value = false
        ScreenContainer {
            LoginScreen(navController)
        }

    }
    
}