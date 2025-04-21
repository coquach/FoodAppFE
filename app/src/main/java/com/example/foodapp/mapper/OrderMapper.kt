package com.example.foodapp.mapper

import com.example.foodapp.data.entities.OrderEntity
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.data.model.enums.ServingType
import com.example.foodapp.utils.Converters
import com.example.foodapp.utils.StringUtils

import java.time.LocalDate
import java.time.LocalTime

object OrderMapper {
    fun OrderEntity.toOrder(): Order {
        return Order(
            id = this.id,
            customerId = this.customerId,
            tableNumber = this.tableNumber,
            voucher = this.voucher,
            status = this.status,
            paymentMethod = this.paymentMethod,
            orderDate =  this.orderDate,
            createAt = this.createAt,
            paymentAt = this.paymentAt,
            note = this.note,
            address = this.address,
            servingType = this.servingType,
            isDeleted = this.isDeleted,
            orderItems = emptyList()
        )
    }

    fun Order.toEntity(): OrderEntity {
        return OrderEntity(
            id = this.id,
            customerId = this.customerId,
            tableNumber = this.tableNumber,
            voucher = this.voucher,
            status = this.status,
            paymentMethod = this.paymentMethod,
            orderDate =this.orderDate,
            createAt = this.createAt,
            paymentAt = this.paymentAt,
            note = this.note,
            address = this.address,
            servingType = this.servingType,
            isDeleted = this.isDeleted

        )
    }

}