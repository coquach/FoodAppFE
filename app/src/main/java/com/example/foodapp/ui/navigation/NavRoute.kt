package com.example.foodapp.ui.navigation

import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.ResetPasswordArgs
import com.example.foodapp.data.model.Staff

import kotlinx.serialization.Serializable

interface NavRoute

@Serializable
object Auth : NavRoute

@Serializable
object SignUp : NavRoute

@Serializable
object Login : NavRoute

@Serializable
object Home : NavRoute

@Serializable
data class FoodDetails(val menuItem: MenuItem) : NavRoute

@Serializable
object Cart : NavRoute

@Serializable
object Notification : NavRoute

@Serializable
object Profile : NavRoute

@Serializable
object Reservation : NavRoute

@Serializable
object Favorite : NavRoute

@Serializable
object OrderList : NavRoute

@Serializable
object AddressList : NavRoute

@Serializable
object AddAddress : NavRoute

@Serializable
object Checkout : NavRoute

@Serializable
data class OrderSuccess(val orderId: String) : NavRoute

@Serializable
data class OrderDetails(val order: Order) : NavRoute

@Serializable
object Setting : NavRoute

@Serializable
object Welcome : NavRoute


@Serializable
object SendEmail: NavRoute


@Serializable
object ResetPasswordSuccess : NavRoute

@Serializable
data class ResetPassword(val resetPasswordArgs: ResetPasswordArgs) : NavRoute

@Serializable
object Statistics : NavRoute

@Serializable
object Warehouse : NavRoute

@Serializable
object Employee : NavRoute

@Serializable
data class UpdateEmployee(val staff: Staff) : NavRoute

@Serializable
object AddEmployee : NavRoute

@Serializable
object Menu : NavRoute

@Serializable
data class UpdateMenuItem(val menuItem: MenuItem) : NavRoute

@Serializable
object AddMenuItem : NavRoute



