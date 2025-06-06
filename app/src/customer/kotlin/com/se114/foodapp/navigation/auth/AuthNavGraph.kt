package com.se114.foodapp.navigation.auth

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.Login
import com.example.foodapp.navigation.Profile
import com.example.foodapp.navigation.ResetPassword
import com.example.foodapp.navigation.ResetPasswordSuccess
import com.example.foodapp.navigation.SendEmail
import com.example.foodapp.navigation.SignUp
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.forgot_password.change_password.ChangePasswordScreen
import com.example.foodapp.ui.screen.auth.forgot_password.reset_success.ResetPassSuccessScreen
import com.example.foodapp.ui.screen.auth.forgot_password.send_email.SendEmailScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.ui.screen.auth.signup.SignUpScreen
import com.example.foodapp.ui.screen.auth.signup.profile.ProfileScreen
import com.example.foodapp.utils.ScreenContainer

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
) {
    composable<Auth> {
        shouldShowBottomNav.value = false
        ScreenContainer(applyStatusBarInset = false) {
            AuthScreen(navController)
        }

    }
    composable<SignUp> {
        shouldShowBottomNav.value = false
        ScreenContainer{
            SignUpScreen(navController)
        }

    }
    composable<Profile>(
    ) {
        shouldShowBottomNav.value = false
        ScreenContainer{
            ProfileScreen(navController)
        }

    }
    composable<Login> {
        shouldShowBottomNav.value = false
        LoginScreen(navController, isCustomer = true)
    }
    composable<SendEmail> {
        shouldShowBottomNav.value = false
        ScreenContainer{
            SendEmailScreen(navController)
        }

    }
    composable<ResetPassword>{
        shouldShowBottomNav.value = false
        ScreenContainer{
            ChangePasswordScreen(navController)
        }

    }
    composable<ResetPasswordSuccess> {
        shouldShowBottomNav.value = false
        ScreenContainer{
            ResetPassSuccessScreen(navController)

        }

    }
}