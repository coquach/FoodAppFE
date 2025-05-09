package com.se114.foodapp.app_nav.auth

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.ResetPasswordArgs
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.CreateProfile
import com.example.foodapp.ui.navigation.Login

import com.example.foodapp.ui.navigation.ResetPassword
import com.example.foodapp.ui.navigation.ResetPasswordSuccess
import com.example.foodapp.ui.navigation.SendEmail
import com.example.foodapp.ui.navigation.SignUp
import com.example.foodapp.ui.navigation.resetPasswordNavType
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
    composable<CreateProfile> {
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