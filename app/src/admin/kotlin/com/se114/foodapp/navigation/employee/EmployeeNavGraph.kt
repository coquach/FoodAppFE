package com.se114.foodapp.navigation.employee

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.navigation.AddEmployee
import com.example.foodapp.navigation.Employee
import com.example.foodapp.navigation.UpdateEmployee
import com.example.foodapp.navigation.staffNavType
import com.example.foodapp.data.model.Staff
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.employee.EmployeeScreen
import com.se114.foodapp.ui.screen.employee.add_employee.AddEmployeeScreen
import kotlin.reflect.typeOf


fun NavGraphBuilder.employeeGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>
) {
   composable<Employee> {
       shouldShowBottomNav.value = true
       ScreenContainer {
           EmployeeScreen(navController)
       }

   }
    composable<UpdateEmployee>(
        typeMap = mapOf(typeOf<Staff>() to staffNavType)
    ) {
        shouldShowBottomNav.value = false
        val route = it.toRoute<UpdateEmployee>()
        ScreenContainer {
            AddEmployeeScreen(navController, isUpdating = true, staff = route.staff)
        }

    }
    composable<AddEmployee>
     {
        shouldShowBottomNav.value = false
         ScreenContainer {
             AddEmployeeScreen(navController)
         }

    }
}