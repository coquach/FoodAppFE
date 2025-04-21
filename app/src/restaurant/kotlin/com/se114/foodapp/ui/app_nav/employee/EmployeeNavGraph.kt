package com.se114.foodapp.ui.app_nav.employee

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.ui.navigation.AddEmployee
import com.example.foodapp.ui.navigation.Employee
import com.example.foodapp.ui.navigation.UpdateEmployee
import com.example.foodapp.ui.navigation.staffNavType
import com.example.foodapp.data.model.Staff
import com.se114.foodapp.ui.screen.employee.EmployeeScreen
import com.se114.foodapp.ui.screen.employee.add_employee.AddEmployeeScreen
import kotlin.reflect.typeOf


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