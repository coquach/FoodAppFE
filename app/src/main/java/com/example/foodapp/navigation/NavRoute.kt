package com.example.foodapp.navigation

import com.example.foodapp.data.model.Export
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.Staff

import kotlinx.serialization.Serializable
import java.math.BigDecimal

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
data class FoodDetails(val food: Food) : NavRoute

@Serializable
object Cart : NavRoute

@Serializable
object Notification : NavRoute

@Serializable
data class Profile(val isUpdating: Boolean = false) : NavRoute



@Serializable
object Reservation : NavRoute

@Serializable
object Favorite : NavRoute

@Serializable
object OrderList : NavRoute


@Serializable
data class MyAddressList(val isCheckout: Boolean) : NavRoute

@Serializable
object AddAddress : NavRoute

@Serializable
object CheckoutCustomer : NavRoute

@Serializable
data class CheckoutStaff(val foodTableID: Int) : NavRoute

@Serializable
object GetFoodForStaff: NavRoute



@Serializable
data class OrderSuccess(val orderId: Long) : NavRoute

@Serializable
data class OrderDetailsCustomer(val order: Order) : NavRoute

@Serializable
data class OrderDetailsStaff(val order: Order) : NavRoute

@Serializable
object Setting : NavRoute

@Serializable
object Welcome : NavRoute


@Serializable
object SendEmail: NavRoute

@Serializable
object SendEmailSuccess: NavRoute

@Serializable
object ResetPasswordSuccess : NavRoute

@Serializable
object ResetPassword : NavRoute

@Serializable
object Security : NavRoute






@Serializable
object Warehouse : NavRoute

@Serializable
object Employee : NavRoute

@Serializable
data class EmployeeDetails(val staff: Staff, val isUpdating: Boolean) : NavRoute


@Serializable
object Menu : NavRoute

@Serializable
data class FoodDetailsAdmin(val food: Food, val isUpdating: Boolean) : NavRoute

@Serializable
object Category: NavRoute

@Serializable
object Supplier : NavRoute

@Serializable
object Material: NavRoute

@Serializable
object Import: NavRoute

@Serializable
object ChatBoxAdmin: NavRoute



@Serializable
data class ImportDetails(val import: Import, val isUpdating: Boolean) : NavRoute

@Serializable
object Export: NavRoute


@Serializable
data class ExportDetails(val export: Export, val isUpdating: Boolean) : NavRoute

@Serializable
object  Voucher : NavRoute

@Serializable
object VoucherPublic : NavRoute

@Serializable
object VoucherCheck : NavRoute

@Serializable
data class VoucherCheckStaff(val totalPrice: String) : NavRoute


@Serializable
data class FeedbackDetails(val orderItemId: Long) : NavRoute

@Serializable
object FoodTableAdmin: NavRoute



@Serializable
data class Tracking(val long: Double, val lat: Double): NavRoute





