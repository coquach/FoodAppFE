package com.example.foodapp.data.dto.filter

import com.google.gson.annotations.SerializedName

data class SupplierFilter (
    val name: String?=null,
    val phone: String?= null,
    val email: String?=null,
    val address: String?= null,
    @SerializedName("isActive")
    val isActive: Boolean?= null,
    )