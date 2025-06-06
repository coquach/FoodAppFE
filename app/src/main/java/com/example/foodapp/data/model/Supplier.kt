package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Supplier(
    val id: Long?= null,
    val name: String= "",
    val phone: String= "",
    val email: String= "",
    val address: String= ""

)
