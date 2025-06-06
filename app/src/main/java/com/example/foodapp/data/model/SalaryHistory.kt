package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SalaryHistory(
    val month: Int,
    val year: Int ,
    val currentSalary: Double,
)
