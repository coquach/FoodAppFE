package com.example.foodapp.data.model

data class Order(
    val address: Address,
    val createdAt: String,
    val id: String,
    val items: List<OrderItem>,
    val paymentMethod: String,
    val status: String,
    val totalAmount: Double,
    val updatedAt: String,
    val userId: String
)
