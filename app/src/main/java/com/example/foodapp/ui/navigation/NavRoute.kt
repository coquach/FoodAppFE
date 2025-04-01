package com.example.foodapp.ui.navigation

import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.data.model.Order
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
data class FoodDetails(val foodItem: FoodItem) : NavRoute

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

