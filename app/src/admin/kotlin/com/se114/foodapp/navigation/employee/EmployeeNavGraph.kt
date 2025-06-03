package com.se114.foodapp.navigation.employee

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.navigation.Employee
import com.example.foodapp.navigation.staffNavType
import com.example.foodapp.data.model.Staff
import com.example.foodapp.navigation.EmployeeDetails
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.staff.EmployeeScreen
import com.se114.foodapp.ui.screen.staff.staff_details.StaffDetailsScreen
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
    composable<EmployeeDetails>(
        typeMap = mapOf(typeOf<Staff>() to staffNavType)
    ) {
        shouldShowBottomNav.value = false

        ScreenContainer {
            StaffDetailsScreen(navController)
        }

    }

}