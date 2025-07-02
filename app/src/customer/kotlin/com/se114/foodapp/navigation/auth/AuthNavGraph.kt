package com.se114.foodapp.navigation.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.Login
import com.example.foodapp.navigation.Profile
import com.example.foodapp.navigation.ResetPassword
import com.example.foodapp.navigation.ResetPasswordSuccess
import com.example.foodapp.navigation.SendEmail
import com.example.foodapp.navigation.SendEmailSuccess
import com.example.foodapp.navigation.SignUp
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.se114.foodapp.ui.screen.setting.security.change_password.ChangePasswordScreen
import com.se114.foodapp.ui.screen.setting.security.reset_success.ResetPassSuccessScreen
import com.example.foodapp.ui.screen.auth.forgot_password.send_email.SendEmailScreen
import com.example.foodapp.ui.screen.auth.forgot_password.send_email.SendEmailSuccessScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.ui.screen.auth.signup.SignUpScreen
import com.se114.foodapp.ui.screen.setting.profile.ProfileScreen
import com.example.foodapp.utils.ScreenContainer

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
  
) {

        composable<Auth> {
      
            ScreenContainer(applyStatusBarInset = false) {
                AuthScreen(navController)
            }

        }
        composable<SignUp> {
       
            ScreenContainer{
                SignUpScreen(navController)
            }

        }
        composable<Profile>(
        ) {
    
            ScreenContainer{
                ProfileScreen(navController)
            }

        }
        composable<Login> {
            
            LoginScreen(navController, isCustomer = true)
        }
        composable<SendEmail> {
            
            ScreenContainer{
                SendEmailScreen(navController)
            }

        }
        composable<SendEmailSuccess> {
           

            ScreenContainer{
                SendEmailSuccessScreen(navController)
            }

        }
        composable<ResetPassword>{
            
            ScreenContainer{
                ChangePasswordScreen(navController)
            }

        }
        composable<ResetPasswordSuccess> {
            
            ScreenContainer{
                ResetPassSuccessScreen(navController)

            }

        }


}