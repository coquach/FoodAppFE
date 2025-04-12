package com.se114.foodapp.ui.app_nav.employee

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.data.model.ResetPasswordArgs
import com.example.foodapp.data.model.Staff
import com.example.foodapp.ui.navigation.AddEmployee
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Employee
import com.example.foodapp.ui.navigation.FoodDetails
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.navigation.ResetPassword
import com.example.foodapp.ui.navigation.ResetPasswordSuccess
import com.example.foodapp.ui.navigation.SendEmail
import com.example.foodapp.ui.navigation.SignUp
import com.example.foodapp.ui.navigation.UpdateEmployee
import com.example.foodapp.ui.navigation.UpdateMenuItem
import com.example.foodapp.ui.navigation.foodItemNavType
import com.example.foodapp.ui.navigation.resetPasswordNavType
import com.example.foodapp.ui.navigation.staffNavType
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.forgot_password.change_password.ChangePasswordScreen
import com.example.foodapp.ui.screen.auth.forgot_password.reset_success.ResetPassSuccessScreen
import com.example.foodapp.ui.screen.auth.forgot_password.send_email.SendEmailScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.ui.screen.auth.signup.SignUpScreen
import com.se114.foodapp.ui.screen.employee.EmployeeScreen
import com.se114.foodapp.ui.screen.employee.add_employee.AddEmployeeScreen
import com.se114.foodapp.ui.screen.menu.add_menu_item.AddMenuItemScreen
import kotlin.reflect.typeOf

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.employeeGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
   composable<Employee> {
       shouldShowBottomNav.value = true
       EmployeeScreen(navController)
   }
    composable<UpdateEmployee>(
        typeMap = mapOf(typeOf<Staff>() to staffNavType)
    ) {
        shouldShowBottomNav.value = false
        val route = it.toRoute<UpdateEmployee>()
        AddEmployeeScreen(navController, isUpdating = true, staff = route.staff)
    }
    composable<AddEmployee>
     {
        shouldShowBottomNav.value = false
         AddEmployeeScreen(navController)
    }
}