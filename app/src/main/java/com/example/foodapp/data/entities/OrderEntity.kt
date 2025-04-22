package com.example.foodapp.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.data.model.enums.ServingType
import com.example.foodapp.utils.Constants.ORDER_TABLE
import com.example.foodapp.utils.json_format.LocalDateSerializer
import com.example.foodapp.utils.json_format.LocalTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = ORDER_TABLE)
data class OrderEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,

    val customerId: String? = null,
    val tableNumber: Int? = null,
    val staffId: String?= null,
    val voucher: Double? = null,
    val status: String,
    val paymentMethod: String,
    val orderDate: LocalDate,
    val createAt: LocalTime,
    val paymentAt: LocalTime,
    val note: String? = null,
    val address: String? = null,
    val servingType: String,
    val isDeleted: Boolean = false,
)