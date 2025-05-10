package com.example.foodapp.ui.navigation

import com.example.foodapp.data.model.Export
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Food
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
data class FoodDetails(val food: Food) : NavRoute

@Serializable
object Cart : NavRoute

@Serializable
object Notification : NavRoute

@Serializable
object CreateProfile : NavRoute

@Serializable
object UpdateProfile : NavRoute

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
data class OrderSuccess(val orderId: Long) : NavRoute

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
data class UpdateFood(val Food: Food) : NavRoute

@Serializable
object AddFood : NavRoute

@Serializable
object Category: NavRoute

@Serializable
object Supplier : NavRoute

@Serializable
object Material: NavRoute

@Serializable
object Import: NavRoute

@Serializable
object AddImportDetails : NavRoute

@Serializable
data class UpdateImportDetails(val import: Import) : NavRoute

@Serializable
object Export: NavRoute

@Serializable
object  AddExportDetails: NavRoute

@Serializable
data class  UpdateExportDetails(val export: Export) : NavRoute

@Serializable
object  Voucher : NavRoute

@Serializable
object MyVoucher : NavRoute

@Serializable
object VoucherCustomerCheck : NavRoute

@Serializable
object VoucherStaffCheck : NavRoute

@Serializable
data class Feedbacks(val foodId: Long) : NavRoute

@Serializable
data class FeedbackDetails(val foodId: Long) : NavRoute



