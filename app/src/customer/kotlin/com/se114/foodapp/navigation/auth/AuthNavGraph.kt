package com.se114.foodapp.ui.app_nav.auth

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.ResetPasswordArgs
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.Login
import com.example.foodapp.navigation.Profile
import com.example.foodapp.navigation.ResetPassword
import com.example.foodapp.navigation.ResetPasswordSuccess
import com.example.foodapp.navigation.SendEmail
import com.example.foodapp.navigation.SignUp
import com.example.foodapp.navigation.resetPasswordNavType
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.forgot_password.change_password.ChangePasswordScreen
import com.example.foodapp.ui.screen.auth.forgot_password.reset_success.ResetPassSuccessScreen
import com.example.foodapp.ui.screen.auth.forgot_password.send_email.SendEmailScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.ui.screen.auth.signup.SignUpScreen
import com.example.foodapp.ui.screen.auth.signup.profile.ProfileScreen

import kotlin.reflect.typeOf

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
    composable<Auth> {
        shouldShowBottomNav.value = false
        AuthScreen(navController)
    }
    composable<SignUp> {
        shouldShowBottomNav.value = false
        SignUpScreen(navController)
    }
    composable<Profile>(

    ) {
        shouldShowBottomNav.value = false
        ProfileScreen(navController)
    }
    composable<Login> {
        shouldShowBottomNav.value = false
        LoginScreen(navController, isCustomer = true)
    }
    composable<SendEmail> {
        shouldShowBottomNav.value = false
        SendEmailScreen(navController)
    }
    composable<ResetPassword>(
        typeMap = mapOf(typeOf<ResetPasswordArgs>() to resetPasswordNavType)
    ) {
        val route = it.toRoute<ResetPassword>()
        shouldShowBottomNav.value = false
        ChangePasswordScreen(
            navController,
            route.resetPasswordArgs.oobCode,
            route.resetPasswordArgs.method
        )
    }
    composable<ResetPasswordSuccess> {
        shouldShowBottomNav.value = false
        ResetPassSuccessScreen(navController)
    }
}