package com.se114.foodapp.data.dto.filter



data class SupplierFilter (
    val name: String?=null,
    val phone: String?= null,
    val email: String?=null,
    val address: String?= null,
    val isActive: Boolean?= null,

    )